/**
 * Contacto - Página de contacto.
 *
 * Pantalla informativa con datos de contacto y formulario visual.
 * Preparada para conectar con el backend cuando exista el endpoint.
 */
import { useState } from 'react'

export default function Contacto() {
  const [formData, setFormData] = useState({
    nombre: '',
    correo: '',
    mensaje: '',
  })

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    alert('Funcionalidad disponible cuando el backend implemente el endpoint de contacto.')
  }

  return (
    <div className="min-h-screen bg-darkbg text-white">
      <main className="flex-1 p-6 lg:p-12">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h1 className="text-4xl md:text-5xl font-serif mb-6">Contacto</h1>
            <p className="text-gray-400 text-lg font-light max-w-2xl mx-auto">
              ¿Tienes preguntas? Nuestro equipo está listo para ayudarte.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
            <div className="bg-surface rounded-2xl border border-white/5 p-8">
              <h2 className="text-2xl font-serif text-dorado mb-6">Envíanos un mensaje</h2>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="space-y-2">
                  <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="nombre">Nombre</label>
                  <input type="text" id="nombre" name="nombre" value={formData.nombre} onChange={handleChange} placeholder="Tu nombre" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
                </div>
                <div className="space-y-2">
                  <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="correo">Correo</label>
                  <input type="email" id="correo" name="correo" value={formData.correo} onChange={handleChange} placeholder="tu@email.com" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors" required />
                </div>
                <div className="space-y-2">
                  <label className="text-xs font-bold uppercase tracking-widest text-gray-400" htmlFor="mensaje">Mensaje</label>
                  <textarea id="mensaje" name="mensaje" value={formData.mensaje} onChange={handleChange} rows="5" placeholder="¿En qué podemos ayudarte?" className="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors resize-none" required></textarea>
                </div>
                <button type="submit" className="w-full bg-dorado text-darkbg py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white transition-colors">
                  Enviar mensaje
                </button>
              </form>
            </div>

            <div className="space-y-8">
              <div className="bg-surface rounded-2xl border border-white/5 p-8">
                <h3 className="text-lg font-serif text-dorado mb-4">Oficina Principal</h3>
                <div className="space-y-4 text-sm text-gray-400">
                  <div className="flex items-start space-x-3">
                    <i className="fa-solid fa-location-dot text-dorado mt-1"></i>
                    <p>Bogotá, Colombia<br />Centro Empresarial Torre Charter, Piso 15</p>
                  </div>
                  <div className="flex items-start space-x-3">
                    <i className="fa-solid fa-phone text-dorado mt-1"></i>
                    <p>+57 300 123 4567</p>
                  </div>
                  <div className="flex items-start space-x-3">
                    <i className="fa-solid fa-envelope text-dorado mt-1"></i>
                    <p>info@charterflight.com</p>
                  </div>
                </div>
              </div>

              <div className="bg-surface rounded-2xl border border-white/5 p-8">
                <h3 className="text-lg font-serif text-dorado mb-4">Horario de Atención</h3>
                <div className="space-y-2 text-sm text-gray-400">
                  <div className="flex justify-between">
                    <span>Lunes - Viernes</span>
                    <span className="text-white">08:00 - 18:00</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Sábados</span>
                    <span className="text-white">09:00 - 14:00</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Domingos</span>
                    <span className="text-gray-500">Cerrado</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
