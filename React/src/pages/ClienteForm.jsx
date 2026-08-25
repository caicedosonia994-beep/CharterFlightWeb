/**
 * ClienteForm - Formulario de registro y edición de clientes.
 *
 * En modo edición carga los datos del cliente por ID.
 * En modo registro crea un nuevo cliente.
 * Utiliza useState para el formulario y useEffect para carga inicial.
 * Navegación con useNavigate al guardar exitosamente.
 */
import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

export default function ClienteForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    documento: '',
    telefono: '',
    correo: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (id && id !== '0') {
      fetchCliente()
    }
  }, [id])

  const fetchCliente = async () => {
    try {
      setLoading(true)
      const response = await fetch(`/api/clientes/${id}`)
      if (!response.ok) throw new Error('Cliente no encontrado')
      const data = await response.json()
      setFormData(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    const method = id && id !== '0' ? 'PUT' : 'POST'
    const url = id && id !== '0' ? `/api/clientes/${id}` : '/api/clientes'

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      })
      if (!response.ok) {
        const err = await response.json()
        throw new Error(err.message || 'Error al guardar')
      }
      navigate('/clientes')
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <div className="flex-1 flex items-center justify-center text-gray-400">Cargando...</div>

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 flex items-center justify-center p-6 py-12">
        <div className="w-full max-w-2xl bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
          <div className="bg-vinotinto/30 px-8 py-6 border-b border-white/5">
            <h1 className="text-2xl font-serif text-dorado">
              {id && id !== '0' ? 'Editar Cliente' : 'Registro de Nuevo Cliente'}
            </h1>
            <p className="text-gray-400 text-sm mt-1">
              Complete los datos para {id && id !== '0' ? 'actualizar' : 'dar de alta a'} un nuevo pasajero VIP.
            </p>
          </div>

          {error && (
            <div className="mx-8 mt-4 p-4 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="p-8 space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre" value={formData.nombre} onChange={handleChange} placeholder="Ej. Juan" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="apellido">Apellido</label>
                <input type="text" id="apellido" name="apellido" value={formData.apellido} onChange={handleChange} placeholder="Ej. Pérez" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="documento">Documento / Pasaporte</label>
                <input type="text" id="documento" name="documento" value={formData.documento} onChange={handleChange} placeholder="Ej. A12345678" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="telefono">Teléfono</label>
                <input type="tel" id="telefono" name="telefono" value={formData.telefono} onChange={handleChange} placeholder="Ej. +34 600 000 000" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="correo">Correo Electrónico</label>
              <input type="email" id="correo" name="correo" value={formData.correo} onChange={handleChange} placeholder="juan.perez@ejemplo.com" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
            </div>

            <div className="pt-6 flex flex-col md:flex-row gap-4">
              <button type="submit" className="flex-1 bg-dorado text-darkbg py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white transition-colors flex items-center justify-center space-x-2">
                <i className="fa-solid fa-user-plus"></i>
                <span>{id && id !== '0' ? 'Actualizar Cliente' : 'Registrar Cliente'}</span>
              </button>
              <Link to="/clientes" className="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors text-center">
                Cancelar
              </Link>
            </div>
          </form>
        </div>
      </main>
    </div>
  )
}
