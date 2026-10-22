import { useCallback, useEffect, useState } from 'react'
import { exportarReporte, getReporte } from '../services/api'

const tiposReporte = [
  { id: 'clientes', titulo: 'Clientes' },
  { id: 'vuelos', titulo: 'Vuelos' },
  { id: 'solicitudes', titulo: 'Solicitudes especiales' },
  { id: 'financiero', titulo: 'Financiero' },
]
const aeronaves = ['Beechcraft 1900D', 'LET L-410', 'Piper Navajo']
const medios = ['Transferencia bancaria', 'Tarjeta débito', 'Tarjeta de crédito', 'Efectivo', 'Nequi', 'Daviplata', 'PSE']
const labelClass = 'mb-2 block text-[10px] font-bold uppercase tracking-widest text-white/55'
const inputClass = 'w-full rounded-lg border border-white/10 bg-[#0D0D0D] px-3 py-2.5 text-sm text-[#F5F2EA] outline-none focus:border-[#C9A227]'

function dinero(valor) {
  if (valor === null || valor === undefined || valor === '') return 'No registrado'
  return `$ ${Number(valor || 0).toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function mostrar(valor) {
  if (valor === null || valor === undefined || valor === '') return 'No disponible'
  if (typeof valor === 'object') return JSON.stringify(valor)
  return String(valor)
}

export default function Reportes() {
  const [tipo, setTipo] = useState('clientes')
  const [filtros, setFiltros] = useState({ fechaInicio: '', fechaFin: '', estado: '', tipoServicio: '', avion: '', medioPago: '', tipoOperacion: '' })
  const [reporte, setReporte] = useState(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [exportando, setExportando] = useState('')

  const cargar = useCallback(async () => {
    setCargando(true)
    setError('')
    try { setReporte(await getReporte(tipo, filtros)) }
    catch (err) { setReporte(null); setError(err.message || 'No fue posible cargar el reporte.') }
    finally { setCargando(false) }
  }, [tipo, filtros])

  useEffect(() => {
    const timer = window.setTimeout(cargar, 0)
    return () => window.clearTimeout(timer)
  }, [cargar])

  const cambiarFiltro = (evento) => setFiltros((actual) => ({ ...actual, [evento.target.name]: evento.target.value }))

  const descargar = async (formato) => {
    setExportando(formato); setError('')
    try {
      const archivo = await exportarReporte(tipo, formato, filtros)
      const url = URL.createObjectURL(archivo)
      const link = document.createElement('a')
      link.href = url
      link.download = `CharterFlight_${tipo}_${new Date().toISOString().slice(0, 10)}.${formato === 'excel' ? 'xlsx' : 'pdf'}`
      document.body.appendChild(link); link.click(); link.remove(); URL.revokeObjectURL(url)
    } catch (err) { setError(err.message || `No fue posible generar ${formato.toUpperCase()}.`) }
    finally { setExportando('') }
  }

  const resumen = reporte?.resumen || {}
  const etiquetasResumen = {
    totalClientes: 'Clientes registrados', totalVuelos: 'Vuelos programados', totalReservas: 'Reservas',
    pasajerosReservados: 'Pasajeros reservados', totalSolicitudes: 'Solicitudes', pasajeros: 'Pasajeros', carga: 'Carga',
    cantidadMovimientos: 'Pagos y movimientos consultados', movimientosConfirmados: 'Pagos confirmados o recibidos', operaciones: 'Operaciones cobrables', pagosAnteriores: 'Pagos de reservas existentes',
    valorAcordado: 'Valor acordado registrado', recibidoEnPeriodo: 'Recibido en el período', saldoPendienteActual: 'Saldo pendiente actual',
  }
  const metricas = Object.entries(resumen).filter(([clave, valor]) => clave !== 'porMedioPago' && (valor === null || typeof valor !== 'object') && etiquetasResumen[clave])

  return (
    <div className="min-h-screen bg-[#0D0D0D] px-4 py-8 text-[#F5F2EA] md:px-8">
      <main className="mx-auto max-w-7xl space-y-7">
        <header className="flex flex-wrap items-end justify-between gap-4">
          <div><p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-[#A32646]">Datos del sistema</p><h1 className="font-serif text-3xl">Reportes</h1><p className="mt-2 text-sm text-white/50">Consultas reales de Charter Flight. Los importes solo se muestran cuando están registrados.</p></div>
          <button type="button" onClick={cargar} disabled={cargando} className="rounded-lg border border-white/15 px-4 py-2 text-xs font-bold uppercase tracking-widest hover:border-[#C9A227] hover:text-[#C9A227]">Actualizar datos</button>
        </header>

        <nav className="flex flex-wrap gap-2" aria-label="Tipo de reporte">
          {tiposReporte.map((item) => <button key={item.id} type="button" onClick={() => setTipo(item.id)} className={`rounded-full border px-4 py-2 text-xs font-bold uppercase tracking-wider transition ${tipo === item.id ? 'border-[#C9A227] bg-[#C9A227] text-[#0D0D0D]' : 'border-white/15 text-white/65 hover:border-[#A32646] hover:text-white'}`}>{item.titulo}</button>)}
        </nav>

        <section className="rounded-2xl border border-white/10 bg-[#171717] p-5">
          <h2 className="mb-4 text-xs font-bold uppercase tracking-widest text-[#C9A227]">Filtros</h2>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {tipo !== 'clientes' && <label><span className={labelClass}>{tipo === 'financiero' ? 'Fecha inicial del pago' : 'Fecha inicial'}</span><input className={inputClass} type="date" name="fechaInicio" value={filtros.fechaInicio} onChange={cambiarFiltro} /></label>}
            {tipo !== 'clientes' && <label><span className={labelClass}>{tipo === 'financiero' ? 'Fecha final del pago' : 'Fecha final'}</span><input className={inputClass} type="date" name="fechaFin" value={filtros.fechaFin} onChange={cambiarFiltro} /></label>}
            {tipo !== 'clientes' && <label><span className={labelClass}>Estado</span><select className={inputClass} name="estado" value={filtros.estado} onChange={cambiarFiltro}><option value="">Todos</option>{(tipo === 'financiero' ? ['Confirmado', 'Rechazado', 'Pagado', 'Pendiente', 'Cancelado'] : tipo === 'vuelos' ? ['programado', 'activo', 'cancelado', 'completado'] : ['Pendiente de revisión', 'En revisión', 'Aprobada', 'Rechazada']).map((estado) => <option key={estado}>{estado}</option>)}</select></label>}
            {(tipo === 'solicitudes' || tipo === 'financiero') && <label><span className={labelClass}>Tipo de servicio</span><select className={inputClass} name="tipoServicio" value={filtros.tipoServicio} onChange={cambiarFiltro}><option value="">Todos</option>{['Chárter privado', 'Corporativo', 'Transporte especializado'].map((servicio) => <option key={servicio}>{servicio}</option>)}</select></label>}
            {(tipo === 'solicitudes' || tipo === 'financiero') && <label><span className={labelClass}>Tipo de avión</span><select className={inputClass} name="avion" value={filtros.avion} onChange={cambiarFiltro}><option value="">Todos</option>{aeronaves.map((avion) => <option key={avion}>{avion}</option>)}</select></label>}
            {tipo === 'financiero' && <label><span className={labelClass}>Medio de pago</span><select className={inputClass} name="medioPago" value={filtros.medioPago} onChange={cambiarFiltro}><option value="">Todos</option>{medios.map((medio) => <option key={medio}>{medio}</option>)}</select></label>}
            {tipo === 'financiero' && <label><span className={labelClass}>Tipo de operación</span><select className={inputClass} name="tipoOperacion" value={filtros.tipoOperacion} onChange={cambiarFiltro}><option value="">Todos</option><option value="VUELO_PROGRAMADO">Vuelo programado</option><option value="SOLICITUD_ESPECIAL">Solicitud especial</option></select></label>}
          </div>
          <p className="mt-4 text-[11px] text-white/40">Los filtros actualizan los indicadores, la tabla y las exportaciones. En el informe financiero, las fechas limitan los movimientos; los saldos muestran el estado actual de cada operación consultada.</p>
        </section>

        {error && <p role="alert" className="rounded-lg border border-[#A32646]/60 bg-[#A32646]/15 p-4 text-sm text-red-200">{error}</p>}

        {metricas.length > 0 && <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {metricas.map(([clave, valor]) => <article key={clave} className="rounded-xl border border-white/10 bg-[#171717] p-5"><p className="text-[10px] font-bold uppercase tracking-widest text-white/45">{etiquetasResumen[clave]}</p><p className="mt-2 font-serif text-2xl text-[#C9A227]">{['valorAcordado', 'recibidoEnPeriodo', 'saldoPendienteActual'].includes(clave) ? dinero(valor) : Number(valor).toLocaleString('es-CO')}</p></article>)}
        </section>}

        {tipo === 'financiero' && resumen.porMedioPago && <section className="rounded-xl border border-white/10 bg-[#171717] p-5"><h2 className="mb-3 text-xs font-bold uppercase tracking-widest text-[#C9A227]">Recibido por medio de pago</h2><div className="flex flex-wrap gap-x-7 gap-y-2 text-sm">{Object.entries(resumen.porMedioPago).length ? Object.entries(resumen.porMedioPago).map(([medio, total]) => <span key={medio}>{medio}: <b>{dinero(total)}</b></span>) : <span className="text-white/50">No hay pagos confirmados en este resultado.</span>}</div></section>}

        <section className="overflow-hidden rounded-2xl border border-white/10 bg-[#171717]">
          <div className="flex flex-wrap items-center justify-between gap-3 border-b border-white/10 p-5"><div><h2 className="font-serif text-xl">{reporte?.titulo || 'Resultados'}</h2><p className="mt-1 text-xs text-white/45">{reporte?.generado ? `Consultado: ${String(reporte.generado).replace('T', ' ').slice(0, 19)}` : ''}</p></div><div className="flex gap-2"><button type="button" onClick={() => descargar('excel')} disabled={cargando || exportando !== '' || !reporte?.filas?.length} className="rounded-lg border border-[#C9A227]/50 px-4 py-2 text-xs font-bold uppercase tracking-widest text-[#C9A227] hover:bg-[#C9A227]/10 disabled:opacity-40">{exportando === 'excel' ? 'Generando…' : 'Excel .xlsx'}</button><button type="button" onClick={() => descargar('pdf')} disabled={cargando || exportando !== '' || !reporte?.filas?.length} className="rounded-lg border border-[#A32646] px-4 py-2 text-xs font-bold uppercase tracking-widest hover:bg-[#A32646]/20 disabled:opacity-40">{exportando === 'pdf' ? 'Generando…' : 'PDF'}</button></div></div>
          {cargando ? <p className="p-8 text-sm text-white/50">Consultando la base de datos…</p> : !reporte?.filas?.length ? <p className="p-8 text-sm text-white/50">No hay datos para mostrar con los filtros seleccionados.</p> : <div className="overflow-x-auto"><table className="w-full min-w-[760px] text-left text-sm"><thead className="bg-[#0D0D0D] text-[10px] uppercase tracking-wider text-white/45"><tr>{reporte.columnas.map((columna) => <th key={columna.clave} className="px-4 py-3">{columna.titulo}</th>)}</tr></thead><tbody>{reporte.filas.map((fila, i) => <tr key={fila.idMovimiento ?? fila.idSolicitud ?? fila.idVuelo ?? fila.idCliente ?? i} className="border-t border-white/5 hover:bg-white/[0.02]">{reporte.columnas.map((columna) => <td key={columna.clave} className="max-w-[320px] px-4 py-3">{['valorTotal', 'monto', 'totalPagadoActual', 'saldoPendiente'].includes(columna.clave) ? (fila[columna.clave] == null ? 'No disponible' : dinero(fila[columna.clave])) : mostrar(fila[columna.clave])}</td>)}</tr>)}</tbody></table></div>}
        </section>
      </main>
    </div>
  )
}
