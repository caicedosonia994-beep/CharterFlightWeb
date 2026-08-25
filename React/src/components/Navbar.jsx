/**
 * Navbar - Barra de navegación principal fija.
 *
 * Muestra el logo de Charter Flight, enlaces de navegación principales
 * y un botón de acceso. Utiliza React Router para la navegación interna.
 */
import { Link } from 'react-router-dom'

export default function Navbar() {
  return (
    <nav className="fixed top-0 w-full z-50 px-6 py-4 flex justify-between items-center bg-darkbg/80 backdrop-blur-md border-b border-white/5 text-white">
      <div className="flex items-center space-x-2">
        <Link to="/" className="text-xl font-serif tracking-tight">
          Charter<span className="text-dorado">.</span>Flight
        </Link>
      </div>

      <div className="hidden md:flex space-x-8 text-sm font-medium tracking-wide">
        <Link to="/" className="nav-link relative">Inicio</Link>
        <Link to="/clientes" className="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Clientes</Link>
        <Link to="/vuelos" className="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Vuelos</Link>
        <Link to="/reservas" className="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Reservas</Link>
        <Link to="/pagos" className="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Pagos</Link>
        <Link to="/reportes" className="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Reportes</Link>
        <Link to="/contacto" className="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Contacto</Link>
      </div>

      <button className="bg-dorado text-darkbg px-6 py-2 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-white transition-colors">
        Acceso
      </button>
    </nav>
  )
}
