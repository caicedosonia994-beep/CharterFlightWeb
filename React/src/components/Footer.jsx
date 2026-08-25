/**
 * Footer - Pie de página principal.
 *
 * Contiene enlaces de navegación organizados por secciones:
 * Plataforma, Empresa y Legal. Incluye copyright y redes sociales.
 */
import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <footer id="contacto" className="bg-darkbg border-t border-white/5 pt-32 pb-12 px-6">
      <div className="max-w-7xl mx-auto">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-12 mb-20">
          <div className="lg:col-span-2">
            <span className="text-2xl font-serif tracking-tight block mb-6">
              Charter<span className="text-dorado">.</span>Flight
            </span>
            <p className="text-gray-500 font-light max-w-sm">
              La plataforma líder para la gestión integral de operaciones de aviación charter de lujo.
            </p>
          </div>
          <div>
            <h4 className="text-xs font-bold uppercase tracking-widest mb-6">Plataforma</h4>
            <ul className="space-y-4 text-sm text-gray-400 font-light">
              <li><Link to="/clientes" className="hover:text-dorado">Clientes</Link></li>
              <li><Link to="/vuelos" className="hover:text-dorado">Vuelos</Link></li>
              <li><Link to="/reservas" className="hover:text-dorado">Reservas</Link></li>
              <li><Link to="/pagos" className="hover:text-dorado">Pagos</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="text-xs font-bold uppercase tracking-widest mb-6">Empresa</h4>
            <ul className="space-y-4 text-sm text-gray-400 font-light">
              <li><Link to="/nosotros" className="hover:text-dorado">Nosotros</Link></li>
              <li><a href="#" className="hover:text-dorado">Carreras</a></li>
              <li><a href="#" className="hover:text-dorado">Prensa</a></li>
              <li><Link to="/contacto" className="hover:text-dorado">Contacto</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="text-xs font-bold uppercase tracking-widest mb-6">Legal</h4>
            <ul className="space-y-4 text-sm text-gray-400 font-light">
              <li><a href="#" className="hover:text-dorado">Privacidad</a></li>
              <li><a href="#" className="hover:text-dorado">Términos</a></li>
              <li><a href="#" className="hover:text-dorado">Seguridad</a></li>
            </ul>
          </div>
        </div>
        <div className="border-t border-white/5 pt-12 flex flex-col md:flex-row justify-between items-center text-xs text-gray-600">
          <p>&copy; 2026 Charter Flight Systems. Todos los derechos reservados.</p>
          <div className="flex space-x-6 mt-4 md:mt-0">
            <a href="#" className="hover:text-white">Instagram</a>
            <a href="#" className="hover:text-white">LinkedIn</a>
            <a href="#" className="hover:text-white">Twitter</a>
          </div>
        </div>
      </div>
    </footer>
  )
}
