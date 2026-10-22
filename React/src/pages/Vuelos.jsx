import { NavLink, Route, Routes } from 'react-router-dom'
import VuelosProgramados from './VuelosProgramados'
import SolicitudesEspeciales from './SolicitudesEspeciales'

export default function Vuelos() {
  return (
    <section className="min-h-screen bg-[#0D0D0D] px-4 py-10 text-[#F5F2EA] md:px-8">
      <div className="mx-auto max-w-7xl">
        <header className="mb-8 flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
          <div>
            <p className="mb-2 text-xs font-bold uppercase tracking-[0.25em] text-[#C9A227]">Charter Flight</p>
            <h1 className="font-serif text-3xl md:text-4xl">Gestión de Vuelos</h1>
            <p className="mt-2 text-sm text-white/60">Administra vuelos programados y nuevas solicitudes chárter.</p>
          </div>
          <nav aria-label="Secciones de vuelos" className="flex w-fit max-w-full gap-1 rounded-xl border border-white/10 bg-[#171717] p-1">
            <NavLink end to="/vuelos" className={({ isActive }) => `rounded-lg px-4 py-3 text-xs font-bold uppercase tracking-wider transition-colors md:px-6 ${isActive ? 'bg-[#C9A227] text-[#0D0D0D]' : 'text-white/65 hover:text-white'}`}>
              <i className="fa-solid fa-plane mr-2" aria-hidden="true"></i>Vuelos programados
            </NavLink>
            <NavLink to="/vuelos/solicitudes-especiales" className={({ isActive }) => `rounded-lg px-4 py-3 text-xs font-bold uppercase tracking-wider transition-colors md:px-6 ${isActive ? 'bg-[#C9A227] text-[#0D0D0D]' : 'text-white/65 hover:text-white'}`}>
              <i className="fa-solid fa-paper-plane mr-2" aria-hidden="true"></i>Solicitudes especiales
            </NavLink>
          </nav>
        </header>
        <Routes>
          <Route index element={<VuelosProgramados />} />
          <Route path="solicitudes-especiales" element={<SolicitudesEspeciales />} />
        </Routes>
      </div>
    </section>
  )
}
