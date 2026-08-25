/**
 * Home - Página principal de Charter Flight.
 *
 * Implementa el diseño UX Pilot con secciones:
 * Hero, Marquee, Features Grid, Gallery, Testimonials, Pricing y Reportes.
 * Utiliza componentes reutilizables FeatureCard, TestimonialCard y PricingCard.
 */
import { useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import FeatureCard from '../components/FeatureCard'
import TestimonialCard from '../components/TestimonialCard'
import PricingCard from '../components/PricingCard'

export default function Home() {
  const galleryRef = useRef(null)
  const testimonialRef = useRef(null)

  const scroll = (ref, direction) => {
    if (!ref.current) return
    const amount = direction === 'next' ? 320 : -320
    ref.current.scrollBy({ left: amount, behavior: 'smooth' })
  }

  useEffect(() => {
    const handleGallery = () => {
      const prev = document.getElementById('prevGallery')
      const next = document.getElementById('nextGallery')
      const container = document.getElementById('galleryScroll')
      if (prev && next && container) {
        prev.onclick = () => scroll(galleryRef, 'prev')
        next.onclick = () => scroll(galleryRef, 'next')
      }
    }
    handleGallery()
  }, [])

  return (
    <div className="antialiased font-sans bg-darkbg text-white overflow-x-hidden">
      {/* Hero Section */}
      <header id="inicio" className="relative min-h-screen flex flex-col md:flex-row items-center justify-center px-6 pt-24 overflow-hidden text-white">
        <div className="max-w-4xl w-full z-10">
          <p className="text-dorado text-xs font-bold tracking-[0.2em] mb-6 uppercase">Executive Aviation Suite</p>
          <h1 className="text-6xl md:text-8xl font-light leading-tight mb-8">
            Sistema de Reservas de <span className="font-serif italic">Vuelos Charter</span>
          </h1>
          <p className="text-xl text-gray-400 max-w-xl mb-12 font-light">
            Gestiona clientes, vuelos y reservas de manera rápida, segura y eficiente.
          </p>
            <div className="flex items-center space-x-6">
                <Link to="/clientes/nuevo" className="group relative px-8 py-4 bg-dorado text-darkbg rounded-full font-bold uppercase tracking-widest text-xs overflow-hidden transition-all hover:pr-12">
                  <span className="relative z-10">Registrar Cliente</span>
                  <div className="absolute right-4 top-1/2 -translate-y-1/2 w-1 h-1 bg-darkbg rounded-full opacity-0 group-hover:opacity-100 transition-opacity"></div>
                </Link>
            <div className="flex items-center space-x-2 text-xs text-gray-500">
              <span className="w-2 h-2 bg-dorado rounded-full animate-pulse"></span>
              <span>Sistema Online</span>
            </div>
          </div>
        </div>

        {/* Hero Visual */}
        <div className="absolute right-0 top-0 h-full w-1/2 md:w-1/3 -z-0 hidden md:block">
          <div className="relative h-full flex items-center justify-center p-12">
            <div className="relative w-full aspect-video rounded-lg overflow-hidden shadow-2xl ring-1 ring-white/10">
              <img
                className="w-full h-full object-cover"
                src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_85571a3fc4_609e3e731b4ce58b.png"
                alt="Interior de avión charter privado"
              />
            </div>
          </div>
        </div>
      </header>

      {/* Marquee Section */}
      <section className="py-10 border-y border-white/5 bg-vinotinto overflow-hidden">
        <div className="animate-scroll whitespace-nowrap inline-block">
          <span className="text-4xl font-light tracking-[0.5em] text-dorado opacity-80 mx-8">PRIVACY • EFFICIENCY • LUXURY • GLOBAL ACCESS</span>
          <span className="text-4xl font-light tracking-[0.5em] text-dorado opacity-80 mx-8">PRIVACY • EFFICIENCY • LUXURY • GLOBAL ACCESS</span>
        </div>
      </section>

      {/* Features Grid */}
      <section id="clientes" className="py-32 px-6 bg-surface text-white">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row justify-between items-end mb-20">
            <div className="max-w-2xl">
              <h2 className="text-4xl md:text-5xl font-light mb-6">Módulos del sistema</h2>
              <p className="text-gray-400 text-lg font-light">Todo lo necesario para operar una flota charter con total control y transparencia.</p>
            </div>
            <div className="mt-8 md:mt-0">
              <a href="#" className="text-sm font-bold uppercase tracking-widest border-b border-dorado pb-1 hover:text-dorado transition-colors">Ver documentación</a>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <FeatureCard
              image="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_9434a080ba_3255e21bd97a8787.png"
              icon="fa-users-viewfinder"
              title="Gestión de Clientes"
              description="Base de datos centralizada con perfiles VIP y preferencias de vuelo."
            />
            <FeatureCard
              image="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_068ec847eb_bd22ead26bf22b14.png"
              icon="fa-plane-departure"
              title="Control de Vuelos"
              description="Planificación de itinerarios y asignación de aeronaves en tiempo real."
            />
            <FeatureCard
              image="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_3526097861_2bb1f74a85ea3f44.png"
              icon="fa-file-invoice-dollar"
              title="Reportes de Pago"
              description="Facturación automática y auditoría financiera completa."
            />
          </div>
        </div>
      </section>

      {/* Gallery Section */}
      <section id="vuelos" className="py-32 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="flex justify-between items-end mb-16">
            <h2 className="text-4xl md:text-5xl font-light">Experiencia de bordo</h2>
            <div className="flex space-x-4">
              <button id="prevGallery" className="w-12 h-12 rounded-full border border-white/20 flex items-center justify-center hover:bg-white hover:text-darkbg transition-all">
                <i className="fa-solid fa-arrow-left"></i>
              </button>
              <button id="nextGallery" className="w-12 h-12 rounded-full border border-white/20 flex items-center justify-center hover:bg-white hover:text-darkbg transition-all">
                <i className="fa-solid fa-arrow-right"></i>
              </button>
            </div>
          </div>

          <div id="galleryScroll" ref={galleryRef} className="flex space-x-6 overflow-x-auto hide-scrollbar pb-12 snap-x">
            <div className="min-w-[300px] md:min-w-[400px] snap-start">
              <div className="aspect-square rounded-lg overflow-hidden mb-6 group">
                <img
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                  src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_43a647a58a_b4978f0588f282c7.png"
                  alt="Suite privada"
                />
              </div>
              <h3 className="text-xl font-medium mb-2">Suite Privada</h3>
              <p className="text-gray-500 text-sm">Configuración exclusiva para máximo confort en trayectos largos.</p>
            </div>
            <div className="min-w-[300px] md:min-w-[400px] snap-start">
              <div className="aspect-square rounded-lg overflow-hidden mb-6 group">
                <img
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                  src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_efbe327e19_2cfcc8ee82f7f5ea.png"
                  alt="Catering gourmet"
                />
              </div>
              <h3 className="text-xl font-medium mb-2">Catering Gourmet</h3>
              <p className="text-gray-500 text-sm">Menús personalizados preparados por chefs de alta cocina.</p>
            </div>
            <div className="min-w-[300px] md:min-w-[400px] snap-start">
              <div className="aspect-square rounded-lg overflow-hidden mb-6 group">
                <img
                  className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                  src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_55b0124664_1432a6150564739c.png"
                  alt="Terminal exclusiva"
                />
              </div>
              <h3 className="text-xl font-medium mb-2">Terminal Exclusiva</h3>
              <p className="text-gray-500 text-sm">Acceso directo sin salas de espera públicas ni filas.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials */}
      <section id="reservas" className="py-32 px-6 bg-surface text-white">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-4xl md:text-5xl font-light mb-16 text-center">Lo que dicen nuestros socios</h2>
          <div id="testimonialScroll" ref={testimonialRef} className="flex space-x-6 overflow-x-auto hide-scrollbar snap-x">
            <TestimonialCard
              image="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_48b9f7b8ae_947ff067105f79a8.png"
              name="Alejandro Vega"
              role="CEO, Grupo Horizonte"
              quote="El módulo de gestión de clientes redujo nuestro tiempo de despacho a la mitad. Interfaz impecable."
            />
            <TestimonialCard
              image="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_e94fe1b082_28d5c6e43377d0d5.png"
              name="Valentina Ríos"
              role="Directora, Aura Jets"
              quote="La facturación automática y los reportes financieros son exactamente lo que necesitábamos."
            />
            <TestimonialCard
              image="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_07ce7ef9fe_b0aa4646f7ff46d3.png"
              name="Roberto Castillo"
              role="Operaciones, SkyLink"
              quote="Control total de la flota desde un solo lugar. El soporte técnico responde al instante."
            />
          </div>
        </div>
      </section>

      {/* Pricing / Tiers */}
      <section id="pagos" className="py-32 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-20">
            <h2 className="text-4xl md:text-5xl font-light mb-6">Planes operativos</h2>
            <p className="text-gray-400 text-lg font-light max-w-2xl mx-auto">Escalabilidad diseñada para operadores de una aeronave hasta grandes flotas.</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <PricingCard
              tier="Starter"
              price="$499"
              period="/mes"
              description="Para operadores individuales."
              features={[
                '1 aeronave',
                'Gestión de clientes',
                'Soporte email',
              ]}
              buttonText="Seleccionar"
              buttonHref="#"
              popular={false}
            />
            <PricingCard
              tier="Executive"
              price="$1,299"
              period="/mes"
              description="Para flotas en crecimiento."
              features={[
                'Hasta 5 aeronaves',
                'Todos los módulos',
                'Soporte prioritario',
              ]}
              buttonText="Seleccionar"
              buttonHref="#"
              popular
            />
            <PricingCard
              tier="Enterprise"
              price="Custom"
              period=""
              description="Infraestructura dedicada."
              features={[
                'Aeronaves ilimitadas',
                'API & integraciones',
                'Gerente de cuenta',
              ]}
              buttonText="Contactar"
              buttonHref="#"
              popular={false}
            />
          </div>
        </div>
      </section>

      {/* Reportes teaser */}
      <section id="reportes" className="py-32 px-6 bg-surface text-white">
        <div className="max-w-7xl mx-auto text-center">
          <h2 className="text-4xl md:text-5xl font-light mb-6">Reportes y análisis</h2>
          <p className="text-gray-400 text-lg font-light max-w-2xl mx-auto mb-8">
            Visualiza métricas operativas, financieras y de rendimiento en tiempo real.
          </p>
          <a href="#" className="inline-block border-b border-dorado pb-1 text-sm font-bold uppercase tracking-widest hover:text-dorado transition-colors">
            Ver documentación
          </a>
        </div>
      </section>
    </div>
  )
}
