/**
 * FeatureCard - Tarjeta de módulo del sistema.
 *
 * Muestra una imagen de fondo con overlay al hacer hover,
 * icono, título y descripción del módulo.
 *
 * @param {string} image - URL de la imagen de fondo
 * @param {string} icon - Clase de icono FontAwesome
 * @param {string} title - Título del módulo
 * @param {string} description - Descripción breve del módulo
 */
export default function FeatureCard({ image, icon, title, description }) {
  return (
    <div className="group relative aspect-square md:aspect-auto md:h-[500px] rounded-2xl overflow-hidden cursor-pointer">
      <img
        className="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
        src={image}
        alt={title}
      />
      <div className="absolute inset-0 bg-vinotinto/80 opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex flex-col items-center justify-center text-center p-8">
        <i className={`fa-solid ${icon} text-4xl text-dorado mb-4`}></i>
        <h3 className="text-2xl font-serif mb-2">{title}</h3>
        <p className="text-sm text-gray-300">{description}</p>
      </div>
    </div>
  )
}
