/**
 * PricingCard - Tarjeta de plan operativo.
 *
 * Muestra nombre del tier, precio, descripción, lista de features
 * y botón de acción. El plan "popular" se resalta visualmente.
 *
 * @param {string} tier - Nombre del plan (Starter, Executive, Enterprise)
 * @param {string} price - Precio del plan
 * @param {string} period - Periodo de facturación (/mes, etc.)
 * @param {string} description - Descripción del plan
 * @param {string[]} features - Lista de características incluidas
 * @param {string} buttonText - Texto del botón
 * @param {string} buttonHref - Enlace del botón
 * @param {boolean} popular - Si el plan es el destacado/popular
 */
export default function PricingCard({ tier, price, period, description, features, buttonText, buttonHref, popular }) {
  return (
    <div className={`p-10 rounded-2xl border transition-colors ${popular ? 'border-dorado/40 bg-vinotinto/20' : 'border-white/5 hover:border-dorado/30'}`}>
      {popular && (
        <span className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-dorado text-darkbg px-4 py-1 rounded-full text-[10px] font-bold uppercase tracking-widest">
          Popular
        </span>
      )}
      <p className="text-xs text-dorado uppercase tracking-widest mb-4">{tier}</p>
      <h3 className="text-3xl font-light mb-2">
        {price}
        {period && <span className="text-sm text-gray-500">{period}</span>}
      </h3>
      <p className="text-gray-500 text-sm mb-8">{description}</p>
      <ul className="space-y-3 text-sm text-gray-300 mb-10">
        {features.map((feature, index) => (
          <li key={index}>
            <i className="fa-solid fa-check text-dorado mr-2"></i>
            {feature}
          </li>
        ))}
      </ul>
      <a
        href={buttonHref}
        className={`block w-full py-3 rounded-full text-center text-xs font-bold uppercase tracking-widest transition-all ${
          popular
            ? 'bg-dorado text-darkbg hover:bg-white'
            : 'border border-white/20 hover:bg-white hover:text-darkbg'
        }`}
      >
        {buttonText}
      </a>
    </div>
  )
}
