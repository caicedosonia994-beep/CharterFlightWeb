/**
 * TestimonialCard - Tarjeta de testimonio de cliente.
 *
 * Muestra avatar, nombre, rol y quote de un testimonio.
 *
 * @param {string} image - URL de la imagen de avatar
 * @param {string} name - Nombre completo del cliente
 * @param {string} role - Cargo o empresa del cliente
 * @param {string} quote - Texto del testimonio
 */
export default function TestimonialCard({ image, name, role, quote }) {
  return (
    <div className="min-w-[300px] md:min-w-[450px] snap-start bg-darkbg p-10 rounded-2xl border border-white/5">
      <div className="flex items-center space-x-4 mb-6">
        <img className="w-14 h-14 rounded-full object-cover grayscale" src={image} alt={name} />
        <div>
          <h4 className="font-bold">{name}</h4>
          <p className="text-xs text-dorado uppercase tracking-widest">{role}</p>
        </div>
      </div>
      <p className="text-gray-400 font-light italic">"{quote}"</p>
    </div>
  )
}
