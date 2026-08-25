/**
 * Reservas - Página de gestión de reservas.
 *
 * Formulario para vincular clientes con vuelos programados.
 * Incluye selección de cliente, vuelo, pasajeros, fecha y estado.
 * Preparado para conectar con el backend cuando exista el endpoint.
 */
import { useState } from 'react'
import { Link } from 'react-router-dom'

export default function Reservas() {
  const [formData, setFormData] = useState({
    cliente: '',
    vuelo: '',
    pasajeros: '1',
    fechaReserva: '',
    estado: 'pendiente',
  })

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    alert('Funcionalidad disponible cuando el backend implemente el endpoint de reservas.')
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
                  <option value="1">Alejandro Vega</option>
                  <option value="2">Valentina Ríos</option>
                  <option value="3">Roberto Castillo</option>
                </select>
                <i className="fa-solid fa-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-gray-600 pointer-events-none"></i>
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="vuelo">Seleccionar Vuelo</label>
              <div className="relative">
                <select id="vuelo" name="vuelo" value={formData.vuelo} onChange={handleChange} className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                  <option value="">Seleccionar itinerario...</option>
                  <option value="v1">CH-742 | Madrid (MAD) -&gt; Ibiza (IBZ)</option>
                  <option value="v2">CH-910 | Barcelona (BCN) -&gt; Dubái (DXB)</option>
                  <option value="v3">CH-115 | Londres (LHR) -&gt; Niza (NCE)</option>
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
                <span>Confirmar Reserva</span>
              </button>
              <button type="reset" onClick={() => setFormData({ cliente: '', vuelo: '', pasajeros: '1', fechaReserva: '', estado: 'pendiente' })} className="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors">
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
