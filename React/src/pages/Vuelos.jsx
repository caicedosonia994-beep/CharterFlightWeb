/**
 * Vuelos - Página de gestión de vuelos.
 *
 * Formulario para registrar vuelos charter con código, origen, destino,
 * fecha, hora, tipo, capacidad y estado.
 * Preparado para conectar con el backend cuando exista el endpoint.
 */
import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import {
  getVuelos,
  getVuelo,
  createVuelo,
  updateVuelo,
  deleteVuelo,
} from '../services/api'

export default function Vuelos() {
  const [formData, setFormData] = useState({
    codigo: '',
    origen: '',
    destino: '',
    fecha: '',
    hora: '',
    tipo: '',
    capacidad: '',
    estado: 'programado',
  })
  const [vuelos, setVuelos] = useState([])
  const [loading, setLoading] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState(null)

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const loadVuelos = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await getVuelos()
      setVuelos(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadVuelos()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)

    const payload = {
      ...formData,
      capacidad:
        formData.capacidad === '' ? '' : parseInt(formData.capacidad, 10),
    }

    try {
      if (editingId) {
        await updateVuelo(editingId, payload)
      } else {
        await createVuelo(payload)
      }
      setFormData({
        codigo: '',
        origen: '',
        destino: '',
        fecha: '',
        hora: '',
        tipo: '',
        capacidad: '',
        estado: 'programado',
      })
      setEditingId(null)
      await loadVuelos()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleEdit = async (id) => {
    setError(null)
    try {
      const data = await getVuelo(id)
      const vuelo = Array.isArray(data) ? data[0] : data
      setFormData({
        codigo: vuelo.codigo || '',
        origen: vuelo.origen || '',
        destino: vuelo.destino || '',
        fecha: vuelo.fecha || '',
        hora: vuelo.hora || '',
        tipo: vuelo.tipo || '',
        capacidad: vuelo.capacidad !== undefined ? vuelo.capacidad : '',
        estado: vuelo.estado || 'programado',
      })
      setEditingId(vuelo.idVuelo || id)
    } catch (err) {
      setError(err.message)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este vuelo?')) return
    setError(null)
    try {
      await deleteVuelo(id)
      await loadVuelos()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleClear = () => {
    setFormData({
      codigo: '',
      origen: '',
      destino: '',
      fecha: '',
      hora: '',
      tipo: '',
      capacidad: '',
      estado: 'programado',
    })
    setEditingId(null)
    setError(null)
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 flex items-center justify-center p-6 py-12">
        <div className="w-full max-w-3xl bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
          <div className="bg-vinotinto/30 px-8 py-6 border-b border-white/5 flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-serif text-dorado">Gestión de Vuelos</h1>
              <p className="text-gray-400 text-sm mt-1">Configuración de rutas y disponibilidad de flota.</p>
            </div>
            <i className="fa-solid fa-plane-departure text-3xl text-dorado/20"></i>
          </div>

          <form onSubmit={handleSubmit} className="p-8 space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="codigo">Código</label>
                <input type="text" id="codigo" name="codigo" value={formData.codigo} onChange={handleChange} placeholder="Ej. CH-742" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="origen">Origen</label>
                <input type="text" id="origen" name="origen" value={formData.origen} onChange={handleChange} placeholder="Ej. Madrid (MAD)" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="destino">Destino</label>
                <input type="text" id="destino" name="destino" value={formData.destino} onChange={handleChange} placeholder="Ej. Ibiza (IBZ)" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="fecha">Fecha de Salida</label>
                <input type="date" id="fecha" name="fecha" value={formData.fecha} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400" />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="hora">Hora de Salida</label>
                <input type="time" id="hora" name="hora" value={formData.hora} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400" />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="tipo">Tipo de Vuelo</label>
                <select id="tipo" name="tipo" value={formData.tipo} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                  <option value="">Seleccionar...</option>
                  <option value="nacional">Nacional</option>
                  <option value="internacional">Internacional</option>
                  <option value="transcontinental">Transcontinental</option>
                </select>
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="capacidad">Capacidad (Pax)</label>
                <input type="number" id="capacidad" name="capacidad" value={formData.capacidad} onChange={handleChange} placeholder="Ej. 12" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="estado">Estado</label>
                <select id="estado" name="estado" value={formData.estado} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                  <option value="programado">Programado</option>
                  <option value="confirmado">Confirmado</option>
                  <option value="en_vuelo">En Vuelo</option>
                  <option value="cancelado">Cancelado</option>
                </select>
              </div>
            </div>

            <div className="pt-6 flex flex-col md:flex-row gap-4">
              <button type="submit" className="flex-1 bg-vinotinto text-white border border-vinotinto hover:bg-vinotinto/80 py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs transition-colors flex items-center justify-center space-x-2">
                <i className={editingId ? "fa-solid fa-save" : "fa-solid fa-plus"}></i>
                <span>{editingId ? 'Actualizar' : 'Registrar'} Vuelo</span>
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
            <h2 className="text-xl font-serif text-dorado mb-4">Listado de Vuelos</h2>

            {loading && <p className="text-gray-400">Cargando vuelos...</p>}

            {!loading && vuelos.length === 0 && (
              <p className="text-gray-400">No hay vuelos registrados.</p>
            )}

            {!loading && vuelos.length > 0 && (
              <div className="overflow-x-auto">
                <table className="w-full text-sm text-left text-gray-300">
                  <thead>
                    <tr className="border-b border-white/10">
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Código</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Origen</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Destino</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Fecha</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Hora</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Tipo</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Capacidad</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Estado</th>
                      <th className="pb-3 text-xs font-bold uppercase tracking-widest text-gray-400">Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {vuelos.map((vuelo) => {
                      const vueloId = vuelo.idVuelo
                      return (
                        <tr key={vueloId} className="border-b border-white/5">
                          <td className="py-3">{vuelo.codigo}</td>
                          <td className="py-3">{vuelo.origen}</td>
                          <td className="py-3">{vuelo.destino}</td>
                          <td className="py-3">{vuelo.fecha}</td>
                          <td className="py-3">{vuelo.hora}</td>
                          <td className="py-3">{vuelo.tipo}</td>
                          <td className="py-3">{vuelo.capacidad}</td>
                          <td className="py-3">{vuelo.estado}</td>
                          <td className="py-3 space-x-2">
                            <button
                              onClick={() => handleEdit(vueloId)}
                              className="px-3 py-1.5 text-xs font-bold uppercase tracking-widest text-dorado border border-dorado/30 rounded-lg hover:bg-dorado/10 transition-colors"
                            >
                              Editar
                            </button>
                            <button
                              onClick={() => handleDelete(vueloId)}
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
