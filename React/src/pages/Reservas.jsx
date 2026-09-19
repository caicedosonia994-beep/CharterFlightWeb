/**
 * Reservas - Página de gestión de reservas.
 *
 * Formulario para vincular clientes con vuelos programados.
 * Incluye selección de cliente, vuelo, pasajeros, fecha y estado.
 * Conecta con el backend mediante los endpoints /api/clientes, /api/vuelos y /api/reservas.
 */
import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import {
  getClientes,
  getVuelos,
  getReservas,
  getReserva,
  createReserva,
  updateReserva,
  deleteReserva,
} from '../services/api'

export default function Reservas() {
  const [formData, setFormData] = useState({
    cliente: '',
    vuelo: '',
    pasajeros: '1',
    fechaReserva: '',
    estado: 'pendiente',
  })
  const [clientes, setClientes] = useState([])
  const [vuelos, setVuelos] = useState([])
  const [reservas, setReservas] = useState([])
  const [loading, setLoading] = useState(false)
  const [loadingOptions, setLoadingOptions] = useState(true)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState(null)

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const loadData = async () => {
    setLoadingOptions(true)
    setError(null)
    try {
      const [clientesData, vuelosData, reservasData] = await Promise.all([
        getClientes(),
        getVuelos(),
        getReservas(),
      ])
      setClientes(clientesData)
      setVuelos(vuelosData)
      setReservas(reservasData)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingOptions(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)

    const payload = {
      idCliente: formData.cliente ? parseInt(formData.cliente, 10) : 0,
      idVuelo: formData.vuelo ? parseInt(formData.vuelo, 10) : 0,
      pasajeros: formData.pasajeros ? parseInt(formData.pasajeros, 10) : 0,
      fechaReserva: formData.fechaReserva,
      estado: formData.estado,
    }

    try {
      if (editingId) {
        await updateReserva(editingId, payload)
      } else {
        await createReserva(payload)
      }
      setFormData({
        cliente: '',
        vuelo: '',
        pasajeros: '1',
        fechaReserva: '',
        estado: 'pendiente',
      })
      setEditingId(null)
      await loadData()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleEdit = async (id) => {
    setError(null)
    try {
      const data = await getReserva(id)
      const reserva = Array.isArray(data) ? data[0] : data
      setFormData({
        cliente: reserva.idCliente !== undefined ? String(reserva.idCliente) : '',
        vuelo: reserva.idVuelo !== undefined ? String(reserva.idVuelo) : '',
        pasajeros: reserva.pasajeros !== undefined ? String(reserva.pasajeros) : '1',
        fechaReserva: reserva.fechaReserva || '',
        estado: reserva.estado || 'pendiente',
      })
      setEditingId(reserva.idReserva !== undefined ? reserva.idReserva : id)
    } catch (err) {
      setError(err.message)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta reserva?')) return
    setError(null)
    try {
      await deleteReserva(id)
      await loadData()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleClear = () => {
    setFormData({
      cliente: '',
      vuelo: '',
      pasajeros: '1',
      fechaReserva: '',
      estado: 'pendiente',
    })
    setEditingId(null)
    setError(null)
  }

  const formatClienteLabel = (cliente) => {
    if (cliente.nombre && cliente.apellido) {
      return `${cliente.nombre} ${cliente.apellido}`
    }
    if (cliente.nombre) return cliente.nombre
    if (cliente.apellido) return cliente.apellido
    return `Cliente #${cliente.idCliente}`
  }

  const formatVueloLabel = (vuelo) => {
    if (vuelo.codigo && vuelo.origen && vuelo.destino) {
      return `${vuelo.codigo} | ${vuelo.origen} -> ${vuelo.destino}`
    }
    if (vuelo.codigo) return vuelo.codigo
    return `Vuelo #${vuelo.idVuelo}`
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 flex items-center justify-center p-6 py-12">
        <div className="w-full max-w-3xl bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
          <div className="bg-vinotinto/30 px-8 py-6 border-b border-white/5 flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-serif text-dorado">Gestión de Reservas</h1>
              <p className="text-gray-400 text-sm mt-1">Vinculación de clientes VIP con vuelos programados.</p>
            </div>
            <i className="fa-solid fa-calendar-check text-3xl text-dorado/20"></i>
          </div>

          <form onSubmit={handleSubmit} className="p-8 space-y-6">
            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="cliente">Seleccionar Cliente</label>
              <div className="relative">
                <select id="cliente" name="cliente" value={formData.cliente} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                  <option value="">Buscar cliente...</option>
                  {clientes.map((cliente) => (
                    <option key={cliente.idCliente} value={cliente.idCliente}>
                      {formatClienteLabel(cliente)}
                    </option>
                  ))}
                </select>
                <i className="fa-solid fa-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-gray-600 pointer-events-none"></i>
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="vuelo">Seleccionar Vuelo</label>
              <div className="relative">
                <select id="vuelo" name="vuelo" value={formData.vuelo} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                  <option value="">Seleccionar itinerario...</option>
                  {vuelos.map((vuelo) => {
                    const vueloId = vuelo.idVuelo !== undefined ? vuelo.idVuelo : vuelo.id
                    return (
                      <option key={vueloId} value={vueloId}>
                        {formatVueloLabel(vuelo)}
                      </option>
                    )
                  })}
                </select>
                <i className="fa-solid fa-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-gray-600 pointer-events-none"></i>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="pasajeros">Cant. Pasajeros</label>
                <input type="number" id="pasajeros" name="pasajeros" min="1" value={formData.pasajeros} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="fechaReserva">Fecha de Reserva</label>
                <input type="date" id="fechaReserva" name="fechaReserva" value={formData.fechaReserva} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400" />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="estado">Estado</label>
                <select id="estado" name="estado" value={formData.estado} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                  <option value="pendiente">Pendiente de Pago</option>
                  <option value="confirmada">Confirmada</option>
                  <option value="completada">Completada</option>
                  <option value="cancelada">Cancelada</option>
                </select>
              </div>
            </div>

            <div className="pt-6 flex flex-col md:flex-row gap-4">
              <button type="submit" className="flex-1 bg-dorado text-darkbg py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white transition-colors flex items-center justify-center space-x-2">
                <i className="fa-solid fa-check"></i>
                <span>{editingId ? 'Actualizar' : 'Confirmar'} Reserva</span>
              </button>
              <button type="button" onClick={handleClear} className="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors">
                Limpiar
              </button>
              <Link to="/" className="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors text-center">
                Cancelar
              </Link>
            </div>
          </form>

          {error && (
            <div className="px-8 py-4 bg-red-500/10 border-t border-red-500/30">
              <p className="text-red-400 text-sm">{error}</p>
            </div>
          )}

          <div className="px-8 py-6 border-t border-white/5">
            <h2 className="text-xl font-serif text-dorado mb-4">Listado de Reservas</h2>

            {loading && <p className="text-gray-400">Cargando reservas...</p>}

            {!loading && reservas.length === 0 && (
              <p className="text-gray-400">No hay reservas registradas.</p>
            )}

            {!loading && reservas.length > 0 && (
              <div className="overflow-x-auto">
                <table className="w-full text-sm text-left text-gray-300">
                  <thead>
                    <tr className="border-b border-white/10">
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">ID</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Cliente</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Vuelo</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Pasajeros</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Fecha Reserva</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Estado</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reservas.map((reserva) => {
                      const reservaId = reserva.idReserva !== undefined ? reserva.idReserva : reserva.id
                      return (
                        <tr key={reservaId} className="border-b border-white/5">
                          <td className="py-3">{reservaId}</td>
                          <td className="py-3">
                            {reserva.nombreCliente || reserva.idCliente}
                          </td>
                          <td className="py-3">
                            {reserva.codigoVuelo || reserva.idVuelo}
                          </td>
                          <td className="py-3">{reserva.pasajeros}</td>
                          <td className="py-3">{reserva.fechaReserva}</td>
                          <td className="py-3">{reserva.estado}</td>
                          <td className="py-3 space-x-2">
                            <button
                              onClick={() => handleEdit(reservaId)}
                              className="px-3 py-1.5 text-xs font-bold uppercase tracking-widest text-dorado border border-dorado/30 rounded-lg hover:bg-dorado/10 transition-colors"
                            >
                              Editar
                            </button>
                            <button
                              onClick={() => handleDelete(reservaId)}
                              className="px-3 py-1.5 text-xs font-bold uppercase tracking-widest text-red-400 border border-red-500/30 rounded-lg hover:bg-red-500/10 transition-colors"
                            >
                              Eliminar
                            </button>
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  )
}