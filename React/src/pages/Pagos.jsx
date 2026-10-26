import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import {
  createMovimientoPago,
  createOperacionCobrable,
  createPago,
  getOperacionCobrable,
  getOperacionesCobrables,
  getPagos,
  getReservas,
  getSolicitudesEspeciales,
  getVuelos,
  updateOperacionCobrable,
} from '../services/api'

const panel = 'rounded-2xl border border-white/10 bg-[#171717]'
const field = 'w-full rounded-lg border border-white/10 bg-[#0D0D0D] px-4 py-3 text-sm text-[#F5F2EA] outline-none focus:border-[#C9A227]'
const label = 'mb-2 block text-xs font-bold uppercase tracking-widest text-white/60'
const medios = ['Transferencia bancaria', 'Tarjeta débito', 'Tarjeta de crédito', 'Efectivo', 'Nequi', 'Daviplata', 'PSE']

function nuevaClave() {
  return globalThis.crypto?.randomUUID?.() || `mov-${Date.now()}-${Math.random().toString(36).slice(2)}`
}

function fechaHoraLocal() {
  const ahora = new Date()
  return `${ahora.getFullYear()}-${String(ahora.getMonth() + 1).padStart(2, '0')}-${String(ahora.getDate()).padStart(2, '0')}T${String(ahora.getHours()).padStart(2, '0')}:${String(ahora.getMinutes()).padStart(2, '0')}`
}

function dinero(valor) {
  if (valor === null || valor === undefined || valor === '') return 'No disponible'
  return `$ ${Number(valor).toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function idDeSolicitud(solicitud) { return solicitud.idSistema ?? solicitud.idSolicitud }

export default function Pagos() {
  const [pagosAntiguos, setPagosAntiguos] = useState([])
  const [reservas, setReservas] = useState([])
  const [vuelos, setVuelos] = useState([])
  const [solicitudes, setSolicitudes] = useState([])
  const [operaciones, setOperaciones] = useState([])
  const [operacionSeleccionada, setOperacionSeleccionada] = useState(null)
  const [movimientos, setMovimientos] = useState([])
  const [cargando, setCargando] = useState(true)
  const cargaActual = useRef(0)
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [tipoOperacion, setTipoOperacion] = useState('VUELO_PROGRAMADO')
  const [nuevaOperacion, setNuevaOperacion] = useState({ idVuelo: '', idSolicitud: '', valorTotal: '' })
  const [totalesEditados, setTotalesEditados] = useState({})
  const [movimiento, setMovimiento] = useState({ monto: '', fechaPago: fechaHoraLocal(), medioPago: medios[0], referenciaTransaccion: '', estado: 'Confirmado' })
  const [claveIdempotencia, setClaveIdempotencia] = useState(nuevaClave)
  const [pagoAntiguo, setPagoAntiguo] = useState({ idReserva: '', monto: '', numeroFactura: '', fechaPago: '', estado: 'Pagado' })

  const cargar = useCallback(async () => {
    const idCarga = ++cargaActual.current
    setCargando(true)
    setError('')

    const cargarRecurso = async (nombre, solicitud, actualizar) => {
      try {
        const datos = await solicitud()
        if (cargaActual.current === idCarga) actualizar(datos)
      } catch (err) {
        if (cargaActual.current !== idCarga) return
        const detalle = err.message || 'Error desconocido.'
        setError((actual) => actual ? `${actual} ${nombre}: ${detalle}` : `${nombre}: ${detalle}`)
      }
    }

    await Promise.all([
      cargarRecurso('Pagos', getPagos, setPagosAntiguos),
      cargarRecurso('Reservas', getReservas, setReservas),
      cargarRecurso('Vuelos', getVuelos, setVuelos),
      cargarRecurso('Solicitudes especiales', getSolicitudesEspeciales, setSolicitudes),
      cargarRecurso('Operaciones cobrables', getOperacionesCobrables, setOperaciones),
    ])
    if (cargaActual.current === idCarga) setCargando(false)
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(cargar, 0)
    return () => {
      window.clearTimeout(timer)
      cargaActual.current += 1
    }
  }, [cargar])

  const vuelosPorId = useMemo(() => new Map(vuelos.map((vuelo) => [Number(vuelo.idVuelo), vuelo])), [vuelos])
  const solicitudesPorId = useMemo(() => new Map(solicitudes.map((solicitud) => [Number(idDeSolicitud(solicitud)), solicitud])), [solicitudes])

  const seleccionarOperacion = async (id) => {
    if (!id) { setOperacionSeleccionada(null); setMovimientos([]); return }
    try {
      const datos = await getOperacionCobrable(id)
      setOperacionSeleccionada(datos)
      setMovimientos(datos.movimientos || [])
      setMovimiento((actual) => ({ ...actual, fechaPago: fechaHoraLocal() }))
    } catch (err) { setError(err.message || 'No fue posible consultar el historial.') }
  }

  const registrarOperacion = async (evento) => {
    evento.preventDefault()
    setError(''); setMensaje('')
    const relacion = tipoOperacion === 'VUELO_PROGRAMADO' ? nuevaOperacion.idVuelo : nuevaOperacion.idSolicitud
    if (!relacion) { setError('Selecciona el vuelo o la solicitud que tendrá la operación de pago.'); return }
    const total = nuevaOperacion.valorTotal === '' ? null : Number(nuevaOperacion.valorTotal)
    if (total !== null && (!Number.isFinite(total) || total <= 0)) { setError('El valor acordado debe ser mayor que cero.'); return }
    setGuardando(true)
    try {
      const resultado = await createOperacionCobrable({
        tipoOperacion,
        idVuelo: tipoOperacion === 'VUELO_PROGRAMADO' ? Number(relacion) : null,
        idSolicitud: tipoOperacion === 'SOLICITUD_ESPECIAL' ? Number(relacion) : null,
        valorTotal: total,
      })
      setMensaje('Operación de pago registrada correctamente.')
      setNuevaOperacion({ idVuelo: '', idSolicitud: '', valorTotal: '' })
      await cargar()
      await seleccionarOperacion(resultado.idOperacion)
    } catch (err) { setError(err.message || 'No fue posible registrar la operación.') }
    finally { setGuardando(false) }
  }

  const guardarTotal = async (operacion) => {
    setError(''); setMensaje('')
    const raw = totalesEditados[operacion.idOperacion] ?? (operacion.valorTotal ?? '')
    const valorTotal = raw === '' ? null : Number(raw)
    if (valorTotal !== null && (!Number.isFinite(valorTotal) || valorTotal <= 0)) { setError('El valor acordado debe ser mayor que cero.'); return }
    setGuardando(true)
    try {
      const actualizada = await updateOperacionCobrable(operacion.idOperacion, { valorTotal })
      setOperaciones((actuales) => actuales.map((item) => item.idOperacion === actualizada.idOperacion ? actualizada : item))
      if (operacionSeleccionada?.idOperacion === actualizada.idOperacion) setOperacionSeleccionada(actualizada)
      setMensaje('Valor acordado actualizado correctamente.')
    } catch (err) { setError(err.message || 'No fue posible actualizar el valor acordado.') }
    finally { setGuardando(false) }
  }

  const registrarMovimiento = async (evento) => {
    evento.preventDefault()
    if (!operacionSeleccionada) { setError('Selecciona una operación de pago.'); return }
    const amount = Number(movimiento.monto)
    if (!Number.isFinite(amount) || amount <= 0) { setError('El abono debe ser mayor que cero.'); return }
    setError(''); setMensaje(''); setGuardando(true)
    try {
      const respuesta = await createMovimientoPago(operacionSeleccionada.idOperacion, {
        ...movimiento,
        monto: amount,
        fechaPago: movimiento.fechaPago.replace('T', ' ') + ':00',
        referenciaTransaccion: movimiento.referenciaTransaccion.trim() || null,
        claveIdempotencia,
      })
      setOperacionSeleccionada(respuesta.operacion)
      setMovimientos(respuesta.operacion.movimientos || [])
      setOperaciones((actuales) => actuales.map((item) => item.idOperacion === respuesta.operacion.idOperacion ? respuesta.operacion : item))
      setMovimiento((actual) => ({ ...actual, monto: '', referenciaTransaccion: '', fechaPago: fechaHoraLocal() }))
      setClaveIdempotencia(nuevaClave())
      setMensaje(respuesta.movimiento?.estado === 'Rechazado' ? 'Intento de pago rechazado registrado.' : 'Movimiento de pago registrado correctamente.')
    } catch (err) { setError(err.message || 'No fue posible registrar el movimiento.') }
    finally { setGuardando(false) }
  }

  const registrarPagoAnterior = async (evento) => {
    evento.preventDefault()
    setError(''); setMensaje('')
    const reserva = reservas.find((item) => Number(item.idReserva) === Number(pagoAntiguo.idReserva))
    if (!reserva) { setError('Selecciona una reserva válida.'); return }
    const monto = Number(pagoAntiguo.monto)
    if (!Number.isFinite(monto) || monto <= 0) { setError('El monto debe ser mayor que cero.'); return }
    setGuardando(true)
    try {
      await createPago({ idReserva: Number(reserva.idReserva), idCliente: Number(reserva.idCliente), idVuelo: Number(reserva.idVuelo), monto, numeroFactura: pagoAntiguo.numeroFactura.trim(), fechaPago: pagoAntiguo.fechaPago, estado: pagoAntiguo.estado })
      setPagoAntiguo({ idReserva: '', monto: '', numeroFactura: '', fechaPago: '', estado: 'Pagado' })
      setMensaje('Pago de reserva registrado correctamente.')
      await cargar()
    } catch (err) { setError(err.message || 'No fue posible registrar el pago de reserva.') }
    finally { setGuardando(false) }
  }

  const etiquetaOperacion = (operacion) => {
    const relacionado = operacion.tipoOperacion === 'VUELO_PROGRAMADO'
      ? vuelosPorId.get(Number(operacion.idVuelo))
      : solicitudesPorId.get(Number(operacion.idSolicitud))
    const referencia = relacionado
      ? operacion.tipoOperacion === 'VUELO_PROGRAMADO' ? relacionado.codigo : `Solicitud #${idDeSolicitud(relacionado)}`
      : operacion.referenciaOperacion
    const ruta = relacionado ? `${relacionado.origen} → ${relacionado.destino}` : `${operacion.origen || ''} → ${operacion.destino || ''}`
    return `${operacion.tipoOperacion === 'VUELO_PROGRAMADO' ? 'Vuelo' : 'Solicitud especial'} ${referencia || ''} · ${ruta}`
  }

  return (
    <div className="min-h-screen bg-[#0D0D0D] px-4 py-8 text-[#F5F2EA] md:px-8">
      <main className="mx-auto max-w-7xl space-y-8">
        <header className="flex flex-wrap items-end justify-between gap-4">
          <div><p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-[#A32646]">Administración financiera</p><h1 className="font-serif text-3xl">Pagos</h1><p className="mt-2 text-sm text-white/55">Operaciones, abonos e historial confirmado.</p></div>
          <button type="button" onClick={cargar} disabled={cargando} className="rounded-lg border border-white/15 px-4 py-2 text-xs font-bold uppercase tracking-widest hover:border-[#C9A227] hover:text-[#C9A227]">Actualizar</button>
        </header>
        {error && <p role="alert" className="rounded-lg border border-[#A32646]/60 bg-[#A32646]/15 p-4 text-sm text-red-200">{error}</p>}
        {mensaje && <p role="status" className="rounded-lg border border-[#C9A227]/30 bg-[#C9A227]/10 p-4 text-sm text-[#F5F2EA]">{mensaje}</p>}

        <section className={`${panel} p-6`}>
          <h2 className="mb-2 font-serif text-xl text-[#C9A227]">Crear operación cobrable</h2>
          <p className="mb-5 text-sm text-white/50">El valor total puede quedar sin registrar hasta que exista un acuerdo. Los pagos existentes de reservas siguen disponibles abajo.</p>
          <form onSubmit={registrarOperacion} className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <label><span className={label}>Tipo de operación</span><select className={field} value={tipoOperacion} onChange={(e) => { setTipoOperacion(e.target.value); setNuevaOperacion({ idVuelo: '', idSolicitud: '', valorTotal: '' }) }}><option value="VUELO_PROGRAMADO">Vuelo programado</option><option value="SOLICITUD_ESPECIAL">Solicitud especial</option></select></label>
            {tipoOperacion === 'VUELO_PROGRAMADO' ? <label><span className={label}>Vuelo</span><select className={field} value={nuevaOperacion.idVuelo} onChange={(e) => setNuevaOperacion((v) => ({ ...v, idVuelo: e.target.value }))} required><option value="">Seleccionar vuelo</option>{vuelos.map((vuelo) => <option key={vuelo.idVuelo} value={vuelo.idVuelo}>{vuelo.codigo} · {vuelo.origen} → {vuelo.destino} · {vuelo.fecha}</option>)}</select></label> : <label><span className={label}>Solicitud especial</span><select className={field} value={nuevaOperacion.idSolicitud} onChange={(e) => setNuevaOperacion((v) => ({ ...v, idSolicitud: e.target.value }))} required><option value="">Seleccionar solicitud</option>{solicitudes.map((solicitud) => <option key={idDeSolicitud(solicitud)} value={idDeSolicitud(solicitud)}>#{idDeSolicitud(solicitud)} · {solicitud.origen} → {solicitud.destino} · {solicitud.fechaReq}</option>)}</select></label>}
            <label><span className={label}>Valor acordado (opcional)</span><input className={field} type="number" min="0.01" step="0.01" value={nuevaOperacion.valorTotal} onChange={(e) => setNuevaOperacion((v) => ({ ...v, valorTotal: e.target.value }))} placeholder="Sin valor registrado" /></label>
            <div className="flex items-end"><button disabled={guardando} className="w-full rounded-lg bg-[#C9A227] px-5 py-3 text-xs font-bold uppercase tracking-widest text-[#0D0D0D] hover:bg-[#F5F2EA] disabled:opacity-50">Crear operación</button></div>
          </form>
        </section>

        <section className={`${panel} overflow-hidden`}>
          <div className="border-b border-white/10 p-6"><h2 className="font-serif text-xl text-[#C9A227]">Operaciones y saldos</h2><p className="mt-1 text-xs text-white/45">Una operación por vuelo o solicitud; cada abono se registra por separado.</p></div>
          {cargando ? <p className="p-6 text-sm text-white/55">Cargando operaciones…</p> : operaciones.length === 0 ? <p className="p-6 text-sm text-white/55">No hay operaciones cobrables registradas.</p> : <div className="overflow-x-auto"><table className="w-full min-w-[950px] text-left text-sm"><thead className="bg-[#0D0D0D] text-[10px] uppercase tracking-widest text-white/50"><tr>{['Operación', 'Total acordado', 'Recibido', 'Saldo', 'Estado', 'Movimientos', 'Acción'].map((name) => <th key={name} className="px-4 py-3">{name}</th>)}</tr></thead><tbody>{operaciones.map((op) => <tr key={op.idOperacion} className="border-t border-white/5"><td className="px-4 py-4"><div>{etiquetaOperacion(op)}</div><div className="mt-1 text-xs text-white/40">{op.avionSolicitado || 'Sin avión asignado'}{op.contacto ? ` · ${op.contacto}` : ''}</div></td><td className="px-4 py-4"><div className="flex gap-2"><input aria-label={`Valor total operación ${op.idOperacion}`} className="w-32 rounded border border-white/10 bg-[#0D0D0D] px-2 py-1" type="number" min="0.01" step="0.01" value={totalesEditados[op.idOperacion] ?? (op.valorTotal ?? '')} onChange={(e) => setTotalesEditados((v) => ({ ...v, [op.idOperacion]: e.target.value }))} placeholder="No registrado"/><button type="button" disabled={guardando} onClick={() => guardarTotal(op)} className="text-xs text-[#C9A227] hover:underline">Guardar</button></div></td><td className="px-4 py-4">{dinero(op.totalPagado)}</td><td className="px-4 py-4">{dinero(op.saldoPendiente)}</td><td className="px-4 py-4">{op.estadoPago}</td><td className="px-4 py-4">{op.cantidadMovimientos}</td><td className="px-4 py-4"><button type="button" onClick={() => seleccionarOperacion(op.idOperacion)} className="rounded border border-[#C9A227]/40 px-3 py-2 text-xs font-bold text-[#C9A227] hover:bg-[#C9A227]/10">Ver historial</button></td></tr>)}</tbody></table></div>}
        </section>

        {operacionSeleccionada && <section className={`${panel} grid gap-7 p-6 lg:grid-cols-2`}>
          <div><h2 className="font-serif text-xl text-[#C9A227]">Registrar abono</h2><p className="mb-5 mt-1 text-sm text-white/50">{etiquetaOperacion(operacionSeleccionada)} · Saldo: {dinero(operacionSeleccionada.saldoPendiente)}</p>
            <form onSubmit={registrarMovimiento} className="grid gap-4 sm:grid-cols-2">
              <label><span className={label}>Valor</span><input className={field} type="number" min="0.01" step="0.01" value={movimiento.monto} onChange={(e) => setMovimiento((v) => ({ ...v, monto: e.target.value }))} required /></label>
              <label><span className={label}>Fecha y hora</span><input className={field} type="datetime-local" value={movimiento.fechaPago} onChange={(e) => setMovimiento((v) => ({ ...v, fechaPago: e.target.value }))} required /></label>
              <label><span className={label}>Medio de pago</span><select className={field} value={movimiento.medioPago} onChange={(e) => setMovimiento((v) => ({ ...v, medioPago: e.target.value }))}>{medios.map((medio) => <option key={medio}>{medio}</option>)}</select></label>
              <label><span className={label}>Resultado</span><select className={field} value={movimiento.estado} onChange={(e) => setMovimiento((v) => ({ ...v, estado: e.target.value }))}><option value="Confirmado">Confirmado</option><option value="Rechazado">Rechazado</option></select></label>
              <label className="sm:col-span-2"><span className={label}>Referencia (opcional)</span><input className={field} maxLength="100" value={movimiento.referenciaTransaccion} onChange={(e) => setMovimiento((v) => ({ ...v, referenciaTransaccion: e.target.value }))} placeholder="Referencia de transferencia o transacción" /></label>
              <div className="sm:col-span-2"><button disabled={guardando} className="rounded-lg bg-[#C9A227] px-5 py-3 text-xs font-bold uppercase tracking-widest text-[#0D0D0D] hover:bg-[#F5F2EA] disabled:opacity-50">Registrar movimiento</button></div>
            </form>
          </div>
          <div><h3 className="mb-4 font-serif text-lg">Historial de movimientos</h3>{movimientos.length === 0 ? <p className="text-sm text-white/50">No hay movimientos registrados.</p> : <div className="space-y-3">{movimientos.map((item) => <article key={item.idMovimiento} className="flex flex-wrap justify-between gap-3 rounded-lg border border-white/10 bg-[#0D0D0D] p-4"><div><p className="font-semibold">{dinero(item.monto)} · {item.medioPago}</p><p className="mt-1 text-xs text-white/45">{String(item.fechaPago).replace('T', ' ').slice(0, 16)}{item.referenciaTransaccion ? ` · Ref. ${item.referenciaTransaccion}` : ''}</p></div><span className={item.estado === 'Confirmado' ? 'text-[#C9A227]' : 'text-[#A32646]'}>{item.estado}</span></article>)}</div>}</div>
        </section>}

        <section className={`${panel} p-6`}>
          <h2 className="mb-2 font-serif text-xl text-[#C9A227]">Registrar pago de reserva existente</h2>
          <p className="mb-5 text-sm text-white/50">Los registros históricos y su relación actual con reservas se mantienen sin cambios.</p>
          <form onSubmit={registrarPagoAnterior} className="grid gap-4 md:grid-cols-2 lg:grid-cols-5">
            <label className="lg:col-span-2"><span className={label}>Reserva</span><select className={field} value={pagoAntiguo.idReserva} onChange={(e) => setPagoAntiguo((v) => ({ ...v, idReserva: e.target.value }))} required><option value="">Seleccionar reserva</option>{reservas.map((r) => <option key={r.idReserva} value={r.idReserva}>Reserva #{r.idReserva} · Cliente #{r.idCliente} · Vuelo #{r.idVuelo}</option>)}</select></label>
            <label><span className={label}>Monto</span><input className={field} type="number" min="0.01" step="0.01" value={pagoAntiguo.monto} onChange={(e) => setPagoAntiguo((v) => ({ ...v, monto: e.target.value }))} required /></label>
            <label><span className={label}>Número de factura</span><input className={field} maxLength="50" value={pagoAntiguo.numeroFactura} onChange={(e) => setPagoAntiguo((v) => ({ ...v, numeroFactura: e.target.value }))} required /></label>
            <label><span className={label}>Fecha</span><input className={field} type="date" value={pagoAntiguo.fechaPago} onChange={(e) => setPagoAntiguo((v) => ({ ...v, fechaPago: e.target.value }))} required /></label>
            <label><span className={label}>Estado</span><select className={field} value={pagoAntiguo.estado} onChange={(e) => setPagoAntiguo((v) => ({ ...v, estado: e.target.value }))}><option>Pagado</option><option>Pendiente</option><option>Cancelado</option></select></label>
            <div className="flex items-end"><button disabled={guardando} className="rounded-lg border border-[#A32646] px-5 py-3 text-xs font-bold uppercase tracking-widest hover:bg-[#A32646]/20 disabled:opacity-50">Registrar pago anterior</button></div>
          </form>
          <div className="mt-6 overflow-x-auto"><table className="w-full min-w-[600px] text-left text-sm"><thead className="text-[10px] uppercase tracking-widest text-white/45"><tr>{['Factura', 'Reserva', 'Vuelo', 'Monto', 'Fecha', 'Estado'].map((x) => <th key={x} className="px-3 py-2">{x}</th>)}</tr></thead><tbody>{pagosAntiguos.map((p) => <tr key={p.idPago} className="border-t border-white/5"><td className="px-3 py-3">{p.numeroFactura}</td><td className="px-3 py-3">#{p.idReserva}</td><td className="px-3 py-3">#{p.idVuelo}</td><td className="px-3 py-3">{dinero(p.monto)}</td><td className="px-3 py-3">{p.fechaPago}</td><td className="px-3 py-3">{p.estado}</td></tr>)}</tbody></table>{!cargando && pagosAntiguos.length === 0 && <p className="py-4 text-sm text-white/45">No hay pagos históricos.</p>}</div>
        </section>
      </main>
    </div>
  )
}
