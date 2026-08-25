import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import Home from './pages/Home'
import Clientes from './pages/Clientes'
import ClienteForm from './pages/ClienteForm'
import Vuelos from './pages/Vuelos'
import Reservas from './pages/Reservas'
import Pagos from './pages/Pagos'
import Reportes from './pages/Reportes'
import Contacto from './pages/Contacto'

export default function App() {
  return (
    <div className="app">
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/clientes" element={<Clientes />} />
          <Route path="/clientes/nuevo" element={<ClienteForm id="0" />} />
          <Route path="/clientes/editar/:id" element={<ClienteForm />} />
          <Route path="/vuelos" element={<Vuelos />} />
          <Route path="/reservas" element={<Reservas />} />
          <Route path="/pagos" element={<Pagos />} />
          <Route path="/reportes" element={<Reportes />} />
          <Route path="/contacto" element={<Contacto />} />
        </Routes>
      </main>
      <Footer />
    </div>
  )
}
