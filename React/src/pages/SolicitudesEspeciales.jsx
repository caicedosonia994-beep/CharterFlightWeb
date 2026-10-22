import { useCallback, useEffect, useState } from 'react'
import { createSolicitudEspecial, getSolicitudesEspeciales, updateSolicitudEspecial } from '../services/api'

const estados = ['Pendiente de revisión', 'En revisión', 'Aprobada', 'Rechazada']
const valoresIniciales = {
  nombreContacto: '', correoContacto: '', telefonoContacto: '', tipo: 'Pasajeros',
  origen: '', destino: '', fechaReq: '', tipoServicio: '', observaciones: '',
  avionSolicitado: '',
  tipoMercancia: '', descripcionCarga: '', motivoViaje: '', pasajerosEstimados: '',
}
const inputClass = 'w-full rounded-lg border border-white/10 bg-[#0D0D0D] px-4 py-3 text-sm text-[#F5F2EA] outline-none transition placeholder:text-white/30 focus:border-[#C9A227]'
const labelClass = 'mb-2 block text-xs font-bold uppercase tracking-widest text-white/60'
const estadosCarga = ['Carga', 'Mercancía', 'Carga aérea']
const idDe = (solicitud) => solicitud.idSistema ?? solicitud.idSolicitud
const MAX_LUGAR = 100
const MAX_TELEFONO = 15
const MAX_OBSERVACIONES = 500

function fechaLocal() {
  const ahora = new Date()
  return `${ahora.getFullYear()}-${String(ahora.getMonth() + 1).padStart(2, '0')}-${String(ahora.getDate()).padStart(2, '0')}`
}

function fechaValida(valor) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(valor)) return false
  const fecha = new Date(`${valor}T00:00:00`)
  return !Number.isNaN(fecha.getTime()) && fecha.toISOString().slice(0, 10) === valor
}

function Campo({ etiqueta, nombre, valor, onChange, tipo = 'text', requerido = false, ...props }) {
  return (
    <label>
      <span className={labelClass}>{etiqueta}{requerido ? ' *' : ''}</span>
      <input className={inputClass} name={nombre} type={tipo} value={valor} onChange={onChange} required={requerido} {...props} />
    </label>
  )
}

export default function SolicitudesEspeciales() {
  const [formulario, setFormulario] = useState(valoresIniciales)
  const [solicitudes, setSolicitudes] = useState([])
  const [cargando, setCargando] = useState(true)
  const [enviando, setEnviando] = useState(false)
  const [editandoId, setEditandoId] = useState(null)
  const [actualizando, setActualizando] = useState(null)
  const [errorLista, setErrorLista] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [errorFormulario, setErrorFormulario] = useState('')
  const [erroresCampo, setErroresCampo] = useState({})
  const [avisoObservaciones, setAvisoObservaciones] = useState('')

  const cargarSolicitudes = useCallback(async () => {
    setCargando(true)
    setErrorLista('')
    try {
      const datos = await getSolicitudesEspeciales()
      setSolicitudes(Array.isArray(datos) ? datos : [])
    } catch (error) {
      setErrorLista(error.message || 'No fue posible cargar las solicitudes.')
    } finally {
      setCargando(false)
    }
  }, [])

  useEffect(() => {
    let activa = true
    getSolicitudesEspeciales()
      .then((datos) => { if (activa) setSolicitudes(Array.isArray(datos) ? datos : []) })
      .catch((error) => { if (activa) setErrorLista(error.message || 'No fue posible cargar las solicitudes.') })
      .finally(() => { if (activa) setCargando(false) })
    return () => { activa = false }
  }, [])

  const cambiarCampo = (evento) => {
    const { name, value } = evento.target
    if (name === 'tipo') {
      setFormulario((actual) => ({
        ...actual,
        tipo: value,
        ...(value === 'Pasajeros'
          ? { tipoMercancia: '', descripcionCarga: '' }
          : { pasajerosEstimados: '', motivoViaje: '' }),
      }))
      setMensaje('')
      return
    }
    if (name === 'telefonoContacto') {
      const digitos = value.replace(/\D/g, '').slice(0, MAX_TELEFONO)
      setFormulario((actual) => ({ ...actual, telefonoContacto: digitos }))
      setErroresCampo((actual) => ({ ...actual, telefonoContacto: '' }))
      setMensaje('')
      return
    }
    if (name === 'observaciones' && value.length >= MAX_OBSERVACIONES) {
      setAvisoObservaciones(`Alcanzaste el límite de ${MAX_OBSERVACIONES} caracteres.`)
    } else if (name === 'observaciones') {
      setAvisoObservaciones('')
    }
    if (name === 'origen' || name === 'destino') {
      setErroresCampo((actual) => ({ ...actual, [name]: '' }))
    }
    setFormulario((actual) => ({ ...actual, [name]: value }))
    setMensaje('')
  }

  const bloquearCaracterNoNumerico = (evento) => {
    if (evento.data && /\D/.test(evento.data)) {
      evento.preventDefault()
      setErroresCampo((actual) => ({ ...actual, telefonoContacto: 'El teléfono solo debe contener números.' }))
    }
  }

  const bloquearPegadoTelefono = (evento) => {
    const pegado = evento.clipboardData.getData('text')
    if (!/^\d+$/.test(pegado)) {
      evento.preventDefault()
      setErroresCampo((actual) => ({ ...actual, telefonoContacto: 'El teléfono solo debe contener números.' }))
    } else if (pegado.length > MAX_TELEFONO - formulario.telefonoContacto.length) {
      evento.preventDefault()
      setErroresCampo((actual) => ({ ...actual, telefonoContacto: `El teléfono admite hasta ${MAX_TELEFONO} dígitos.` }))
    }
  }

  const registrar = async (evento) => {
    evento.preventDefault()
    setMensaje('')
    setErrorFormulario('')
    setErroresCampo({})
    const lugaresInvalidos = ['origen', 'destino'].filter((campo) => {
      const texto = formulario[campo].trim()
      return texto.length < 2 || texto.length > MAX_LUGAR
    })
    if (lugaresInvalidos.length) {
      const errores = Object.fromEntries(lugaresInvalidos.map((campo) => [campo, `${campo === 'origen' ? 'El origen' : 'El destino'} debe tener entre 2 y ${MAX_LUGAR} caracteres.`]))
      setErroresCampo(errores)
      setErrorFormulario('Revisa el origen y el destino antes de continuar.')
      return
    }
    if (!/^\d{1,15}$/.test(formulario.telefonoContacto)) {
      setErroresCampo({ telefonoContacto: 'Ingresa un teléfono válido de hasta 15 dígitos.' })
      setErrorFormulario('Revisa el número de teléfono antes de continuar.')
      return
    }
    if (formulario.observaciones.length > MAX_OBSERVACIONES) {
      setErrorFormulario(`Las observaciones no pueden superar ${MAX_OBSERVACIONES} caracteres.`)
      return
    }
    if (!fechaValida(formulario.fechaReq)) {
      setErrorFormulario('Selecciona una fecha requerida válida.')
      return
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formulario.correoContacto.trim())) {
      setErrorFormulario('Ingresa un correo electrónico válido.')
      return
    }
    if (formulario.tipo === 'Pasajeros' && (!formulario.pasajerosEstimados || Number(formulario.pasajerosEstimados) < 1)) {
      setErrorFormulario('Indica una cantidad válida de pasajeros.')
      return
    }
    if (estadosCarga.includes(formulario.tipo) && (!formulario.tipoMercancia.trim() || !formulario.descripcionCarga.trim())) {
      setErrorFormulario('Completa el tipo de mercancía y la descripción de la carga.')
      return
    }

    const datos = {
      nombreContacto: formulario.nombreContacto.trim(),
      correoContacto: formulario.correoContacto.trim(),
      telefonoContacto: formulario.telefonoContacto.trim(),
      tipo: formulario.tipo,
      origen: formulario.origen.trim(),
      destino: formulario.destino.trim(),
      fechaReq: formulario.fechaReq,
      tipoServicio: formulario.tipoServicio,
      avionSolicitado: formulario.avionSolicitado || null,
      observaciones: formulario.observaciones.trim(),
      tipoMercancia: formulario.tipoMercancia.trim(),
      descripcionCarga: formulario.descripcionCarga.trim(),
      motivoViaje: formulario.motivoViaje.trim(),
      pasajerosEstimados: formulario.pasajerosEstimados ? Number(formulario.pasajerosEstimados) : null,
    }

    setEnviando(true)
    try {
      if (editandoId !== null) {
        const actualizada = await updateSolicitudEspecial(editandoId, datos)
        setSolicitudes((actuales) => actuales.map((item) => idDe(item) === editandoId ? actualizada : item))
        setMensaje('Solicitud actualizada correctamente.')
      } else {
        await createSolicitudEspecial(datos)
        setMensaje('Registrado correctamente.')
      }
      setFormulario(valoresIniciales)
      setEditandoId(null)
      setAvisoObservaciones('')
      await cargarSolicitudes()
    } catch (error) {
      setErrorFormulario(error.message || 'No fue posible registrar la solicitud.')
    } finally {
      setEnviando(false)
    }
  }

  const editarSolicitud = (solicitud) => {
    setEditandoId(idDe(solicitud))
    setFormulario({
      nombreContacto: solicitud.nombreContacto || '',
      correoContacto: solicitud.correoContacto || '',
      telefonoContacto: (solicitud.telefonoContacto || '').replace(/\D/g, '').slice(0, MAX_TELEFONO),
      tipo: solicitud.tipo || 'Pasajeros',
      origen: solicitud.origen || '',
      destino: solicitud.destino || '',
      fechaReq: (solicitud.fechaReq || '').slice(0, 10),
      tipoServicio: solicitud.tipoServicio || '',
      avionSolicitado: solicitud.avionSolicitado || '',
      observaciones: solicitud.observaciones || '',
      tipoMercancia: solicitud.tipoMercancia || '',
      descripcionCarga: solicitud.descripcionCarga || '',
      motivoViaje: solicitud.motivoViaje || '',
      pasajerosEstimados: solicitud.pasajerosEstimados ?? '',
    })
    setMensaje('')
    setErrorFormulario('')
    setErroresCampo({})
    setAvisoObservaciones('')
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setFormulario(valoresIniciales)
    setMensaje('')
    setErrorFormulario('')
    setErroresCampo({})
    setAvisoObservaciones('')
  }

  const cambiarEstado = async (solicitud, evento) => {
    const id = idDe(solicitud)
    const nuevoEstado = evento.target.value
    if (id == null || nuevoEstado === solicitud.estado) return
    setActualizando(id)
    setErrorLista('')
    try {
      const actualizada = await updateSolicitudEspecial(id, { estado: nuevoEstado })
      setSolicitudes((actuales) => actuales.map((item) => idDe(item) === id ? { ...item, estado: actualizada?.estado || nuevoEstado } : item))
    } catch (error) {
      setErrorLista(error.message || 'No fue posible actualizar el estado.')
    } finally {
      setActualizando(null)
    }
  }

  const esCarga = estadosCarga.includes(formulario.tipo)

  return (
    <div className="space-y-8 pb-12">
      <section className="overflow-hidden rounded-2xl border border-white/10 bg-[#171717] shadow-2xl">
        <div className="border-b border-white/10 px-6 py-6 md:px-8">
          <p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-[#C9A227]">{editandoId === null ? 'Nueva operación' : `Editando solicitud #${editandoId}`}</p>
          <h2 className="font-serif text-2xl text-[#F5F2EA]">{editandoId === null ? 'Solicitar un vuelo chárter' : 'Editar solicitud especial'}</h2>
          <p className="mt-2 text-sm text-white/55">Comparte los datos del viaje y nuestro equipo preparará una propuesta.</p>
        </div>
        <form onSubmit={registrar} className="space-y-7 p-6 md:p-8">
          <fieldset disabled={enviando} className="space-y-7">
            <div>
              <legend className="mb-4 text-sm font-semibold text-[#C9A227]">Modalidad del servicio</legend>
              <div className="grid gap-3 sm:grid-cols-2">
                {['Pasajeros', 'Carga'].map((tipo) => (
                  <label key={tipo} className={`flex cursor-pointer items-center gap-3 rounded-xl border p-4 transition-colors ${formulario.tipo === tipo ? 'border-[#C9A227]/70 bg-[#C9A227]/10' : 'border-white/10 bg-[#0D0D0D]'}`}>
                    <input type="radio" name="tipo" value={tipo} checked={formulario.tipo === tipo} onChange={cambiarCampo} className="accent-[#C9A227]" />
                    <span className="text-sm font-semibold">{tipo === 'Pasajeros' ? 'Vuelo de pasajeros' : 'Transporte de carga'}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <h3 className="mb-4 text-sm font-semibold text-[#C9A227]">Datos de contacto</h3>
              <div className="grid gap-4 md:grid-cols-3">
                <Campo etiqueta="Nombre de contacto" nombre="nombreContacto" valor={formulario.nombreContacto} onChange={cambiarCampo} maxLength={100} autoComplete="name" requerido />
                <Campo etiqueta="Correo electrónico" nombre="correoContacto" valor={formulario.correoContacto} onChange={cambiarCampo} tipo="email" maxLength={100} autoComplete="email" requerido />
                <div>
                  <Campo etiqueta="Teléfono" nombre="telefonoContacto" valor={formulario.telefonoContacto} onChange={cambiarCampo} onBeforeInput={bloquearCaracterNoNumerico} onPaste={bloquearPegadoTelefono} tipo="tel" inputMode="numeric" maxLength={MAX_TELEFONO} autoComplete="tel" requerido />
                  {erroresCampo.telefonoContacto && <p className="mt-1 text-xs text-red-300">{erroresCampo.telefonoContacto}</p>}
                </div>
              </div>
            </div>

            <div>
              <h3 className="mb-4 text-sm font-semibold text-[#C9A227]">Detalles del itinerario</h3>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <div>
                  <Campo etiqueta="Origen" nombre="origen" valor={formulario.origen} onChange={cambiarCampo} minLength={2} maxLength={MAX_LUGAR} requerido />
                  {erroresCampo.origen && <p className="mt-1 text-xs text-red-300">{erroresCampo.origen}</p>}
                </div>
                <div>
                  <Campo etiqueta="Destino" nombre="destino" valor={formulario.destino} onChange={cambiarCampo} minLength={2} maxLength={MAX_LUGAR} requerido />
                  {erroresCampo.destino && <p className="mt-1 text-xs text-red-300">{erroresCampo.destino}</p>}
                </div>
                <Campo etiqueta="Fecha requerida" nombre="fechaReq" valor={formulario.fechaReq} onChange={cambiarCampo} tipo="date" min={fechaLocal()} requerido />
                <label>
                  <span className={labelClass}>Tipo de servicio *</span>
                  <select className={inputClass} name="tipoServicio" value={formulario.tipoServicio} onChange={cambiarCampo} required>
                    <option value="">Seleccionar</option>
                    <option value="Chárter privado">Chárter privado</option>
                    <option value="Corporativo">Corporativo</option>
                    <option value="Transporte especializado">Transporte especializado</option>
                  </select>
                </label>
                <label>
                  <span className={labelClass}>Avión solicitado</span>
                  <select className={inputClass} name="avionSolicitado" value={formulario.avionSolicitado} onChange={cambiarCampo}>
                    <option value="">Sin avión seleccionado</option>
                    {['Beechcraft 1900D', 'LET L-410', 'Piper Navajo'].map((avion) => <option key={avion} value={avion}>{avion}</option>)}
                  </select>
                </label>
              </div>
            </div>

            {esCarga ? (
              <div className="grid gap-4 md:grid-cols-2">
                <Campo etiqueta="Tipo de mercancía" nombre="tipoMercancia" valor={formulario.tipoMercancia} onChange={cambiarCampo} maxLength={100} requerido />
                <label>
                  <span className={labelClass}>Descripción de carga *</span>
                  <textarea className={inputClass} name="descripcionCarga" value={formulario.descripcionCarga} onChange={cambiarCampo} maxLength={65535} rows="3" required />
                </label>
              </div>
            ) : (
              <div className="grid gap-4 md:grid-cols-2">
                <Campo etiqueta="Pasajeros estimados" nombre="pasajerosEstimados" valor={formulario.pasajerosEstimados} onChange={cambiarCampo} tipo="number" min="1" step="1" requerido />
                <Campo etiqueta="Motivo del viaje" nombre="motivoViaje" valor={formulario.motivoViaje} onChange={cambiarCampo} maxLength={200} />
              </div>
            )}

            <label className="block">
              <span className={labelClass}>Observaciones</span>
              <textarea className={inputClass} name="observaciones" value={formulario.observaciones} onChange={cambiarCampo} maxLength={MAX_OBSERVACIONES} rows="3" placeholder="Horarios, equipaje u otros requerimientos" />
              <span className="mt-1 block text-right text-xs text-white/45">{formulario.observaciones.length}/{MAX_OBSERVACIONES}</span>
              {avisoObservaciones && <span role="status" className="mt-1 block text-xs text-[#C9A227]">{avisoObservaciones}</span>}
            </label>
          </fieldset>

          {mensaje && <p role="status" className="rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">{mensaje}</p>}
          {errorFormulario && <p role="alert" className="rounded-lg border border-[#A32646]/60 bg-[#A32646]/15 px-4 py-3 text-sm text-red-200">{errorFormulario}</p>}
          <div className="flex flex-wrap gap-3">
            <button disabled={enviando} type="submit" className="inline-flex items-center justify-center gap-2 rounded-lg bg-[#C9A227] px-6 py-3.5 text-xs font-bold uppercase tracking-widest text-[#0D0D0D] transition hover:bg-[#F5F2EA] disabled:cursor-wait disabled:opacity-60">
              <i className={`fa-solid ${editandoId === null ? 'fa-paper-plane' : 'fa-floppy-disk'}`} aria-hidden="true"></i>{enviando ? 'Guardando…' : editandoId === null ? 'Enviar solicitud' : 'Guardar cambios'}
            </button>
            {editandoId !== null && <button disabled={enviando} type="button" onClick={cancelarEdicion} className="rounded-lg border border-white/15 px-6 py-3.5 text-xs font-bold uppercase tracking-widest text-white/75 transition hover:border-[#C9A227] hover:text-[#C9A227] disabled:opacity-50">Cancelar edición</button>}
          </div>
        </form>
      </section>

      <section className="overflow-hidden rounded-2xl border border-white/10 bg-[#171717]">
        <div className="flex flex-col gap-3 border-b border-white/10 px-6 py-6 sm:flex-row sm:items-center sm:justify-between md:px-8">
          <div>
            <p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-[#C9A227]">Seguimiento</p>
            <h2 className="font-serif text-2xl">Solicitudes recibidas</h2>
          </div>
          <button type="button" onClick={cargarSolicitudes} disabled={cargando} className="rounded-lg border border-white/15 px-4 py-2 text-xs font-bold uppercase tracking-widest text-white/75 hover:border-[#C9A227] hover:text-[#C9A227] disabled:opacity-50">
            <i className="fa-solid fa-rotate-right mr-2" aria-hidden="true"></i>Actualizar lista
          </button>
        </div>
        {errorLista && <p role="alert" className="mx-6 mt-5 rounded-lg border border-[#A32646]/60 bg-[#A32646]/15 px-4 py-3 text-sm text-red-200 md:mx-8">{errorLista}</p>}
        {cargando ? <p className="px-6 py-8 text-sm text-white/60 md:px-8">Cargando solicitudes…</p> : solicitudes.length === 0 ? (
          <p className="px-6 py-8 text-sm text-white/60 md:px-8">Todavía no hay solicitudes registradas.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[900px] text-left text-sm">
              <thead className="bg-[#0D0D0D] text-[10px] uppercase tracking-widest text-white/50">
                <tr>{['ID', 'Contacto', 'Modalidad', 'Ruta', 'Fecha requerida', 'Servicio', 'Avión solicitado', 'Estado', 'Acciones'].map((titulo) => <th key={titulo} className="px-5 py-4 font-semibold">{titulo}</th>)}</tr>
              </thead>
              <tbody>
                {solicitudes.map((solicitud) => {
                  const id = idDe(solicitud)
                  return <tr key={id ?? `${solicitud.correoContacto}-${solicitud.fechaReq}`} className="border-t border-white/5 align-top hover:bg-white/[0.02]">
                    <td className="px-5 py-4 text-[#C9A227]">{id ?? '—'}</td>
                    <td className="px-5 py-4"><div className="font-medium">{solicitud.nombreContacto || '—'}</div><div className="mt-1 text-xs text-white/45">{solicitud.correoContacto}</div></td>
                    <td className="px-5 py-4">{solicitud.tipo || '—'}</td>
                    <td className="px-5 py-4">{solicitud.origen || '—'}<span className="mx-2 text-[#C9A227]">→</span>{solicitud.destino || '—'}</td>
                    <td className="px-5 py-4">{solicitud.fechaReq || '—'}</td>
                    <td className="px-5 py-4">{solicitud.tipoServicio || '—'}</td>
                    <td className="px-5 py-4">{solicitud.avionSolicitado || 'Sin avión asignado'}</td>
                    <td className="px-5 py-4">
                      <select aria-label={`Estado de solicitud ${id}`} className="max-w-[190px] rounded-lg border border-white/15 bg-[#0D0D0D] px-3 py-2 text-xs text-[#F5F2EA] focus:border-[#C9A227] focus:outline-none disabled:opacity-50" value={solicitud.estado || estados[0]} disabled={actualizando === id} onChange={(evento) => cambiarEstado(solicitud, evento)}>
                        {estados.map((estado) => <option key={estado} value={estado}>{estado}</option>)}
                      </select>
                      {actualizando === id && <span className="mt-1 block text-[10px] text-white/45">Guardando…</span>}
                    </td>
                    <td className="px-5 py-4">
                      <button type="button" onClick={() => editarSolicitud(solicitud)} disabled={enviando || actualizando === id} className="rounded-md border border-[#C9A227]/40 px-3 py-2 text-xs font-bold uppercase tracking-wider text-[#C9A227] transition hover:bg-[#C9A227]/10 disabled:opacity-50">Editar</button>
                    </td>
                  </tr>
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  )
}
