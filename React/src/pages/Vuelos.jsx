/**
 * Vuelos - Página de gestión de vuelos.
 *
 * Formulario para registrar vuelos charter con código, origen, destino,
 * fecha, hora, tipo, capacidad y estado.
 * Preparado para conectar con el backend cuando exista el endpoint.
 */
import { useState } from 'react'
import { Link } from 'react-router-dom'

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

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    alert('Funcionalidad disponible cuando el backend implemente el endpoint de vuelos.')
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
                <i className="fa-solid fa-plus"></i>
                <span>Registrar Vuelo</span>
              </button>
              <button type="reset" onClick={() => setFormData({ codigo: '', origen: '', destino: '', fecha: '', hora: '', tipo: '', capacidad: '', estado: 'programado' })} className="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors">
                Limpiar
              </button>
              <Link to="/" className="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors text-center">
                Cancelar
              </Link>
            </div>
          </form>
        </div>
      </main>
    </div>
  )
}
