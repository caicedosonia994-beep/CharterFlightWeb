<!DOCTYPE html>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <script>
        window.FontAwesomeConfig = {
          autoReplaceSvg: 'nest'
        };
      </script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/js/all.min.js" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Charter Flight | Sistema de Reservas</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Inter:wght@300;400;500;700&display=swap" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        dorado: '#D4AF37',
                        vinotinto: '#5A0F24',
                        darkbg: '#0B0B0B',
                        surface: '#160409',
                    },
                    fontFamily: {
                        sans: ['Inter', 'sans-serif'],
                        serif: ['Playfair Display', 'serif'],
                    },
                    keyframes: {
                        scroll: {
                            '0%': { transform: 'translateX(0)' },
                            '100%': { transform: 'translateX(-50%)' },
                        }
                    },
                    animation: {
                        scroll: 'scroll 20s linear infinite',
                    }
                }
            }
        }
    </script>
    <style>
        .nav-link::after {
            content: '';
            position: absolute;
            bottom: -2px;
            left: 0;
            width: 0;
            height: 1px;
            background-color: #D4AF37;
            transition: width 0.3s ease;
        }
        .nav-link:hover::after {
            width: 100%;
        }
        .hide-scrollbar::-webkit-scrollbar {
            display: none;
        }
        .hide-scrollbar {
            -ms-overflow-style: none;
            scrollbar-width: none;
        }
    </style>
</head>
<body class="antialiased font-sans bg-darkbg text-white overflow-x-hidden">

    <!-- Navigation -->
    <nav class="fixed top-0 w-full z-50 px-6 py-4 flex justify-between items-center bg-darkbg/80 backdrop-blur-md border-b border-white/5">
        <div class="flex items-center space-x-2">
            <span class="text-xl font-serif tracking-tight">Charter<span class="text-dorado">.</span>Flight</span>
        </div>

        <div class="hidden md:flex space-x-8 text-sm font-medium tracking-wide">
            <a href="#" class="nav-link relative">Inicio</a>
            <a href="${pageContext.request.contextPath}/ClienteServlet?accion=listar" class="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Clientes</a>
            <a href="#vuelos" class="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Vuelos</a>
            <a href="#reservas" class="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Reservas</a>
            <a href="#pagos" class="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Pagos</a>
            <a href="#reportes" class="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Reportes</a>
            <a href="#contacto" class="nav-link relative opacity-60 hover:opacity-100 transition-opacity">Contacto</a>
        </div>

        <button class="bg-dorado text-darkbg px-6 py-2 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-white transition-colors">
            Acceso
        </button>
    </nav>

    <!-- Hero Section -->
    <header id="inicio" class="relative min-h-screen flex flex-col md:flex-row items-center justify-center px-6 pt-24 overflow-hidden">
        <div class="max-w-4xl w-full z-10">
            <p class="text-dorado text-xs font-bold tracking-[0.2em] mb-6 uppercase">Executive Aviation Suite</p>
            <h1 class="text-6xl md:text-8xl font-light leading-tight mb-8">
                Sistema de Reservas de <span class="font-serif italic">Vuelos Charter</span>
            </h1>
            <p class="text-xl text-gray-400 max-w-xl mb-12 font-light">
                Gestiona clientes, vuelos y reservas de manera rápida, segura y eficiente.
            </p>
            <div class="flex items-center space-x-6">
                <button onclick="window.location.href='${pageContext.request.contextPath}/ClienteServlet?accion=editar&id=0'" class="group relative px-8 py-4 bg-dorado text-darkbg rounded-full font-bold uppercase tracking-widest text-xs overflow-hidden transition-all hover:pr-12">
                    <span class="relative z-10">Registrar Cliente</span>
                    <div class="absolute right-4 top-1/2 -translate-y-1/2 w-1 h-1 bg-darkbg rounded-full opacity-0 group-hover:opacity-100 transition-opacity"></div>
                </button>
                <div class="flex items-center space-x-2 text-xs text-gray-500">
                    <span class="w-2 h-2 bg-dorado rounded-full animate-pulse"></span>
                    <span>Sistema Online</span>
                </div>
            </div>
        </div>

        <!-- Hero Visual -->
        <div class="absolute right-0 top-0 h-full w-1/2 md:w-1/3 -z-0 hidden md:block">
            <div class="relative h-full flex items-center justify-center p-12">
                <div class="relative w-full aspect-video rounded-lg overflow-hidden shadow-2xl ring-1 ring-white/10">
                    <img class="w-full h-full object-cover" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_85571a3fc4_609e3e731b4ce58b.png" alt="cinematic interior of a private charter jet cabin, cream leather seating, warm ambient lighting, ult" />
                </div>
            </div>
        </div>
    </header>

    <!-- Marquee Section -->
    <section class="py-10 border-y border-white/5 bg-vinotinto overflow-hidden">
        <div class="animate-scroll whitespace-nowrap inline-block">
            <span class="text-4xl font-light tracking-[0.5em] text-dorado opacity-80 mx-8">PRIVACY • EFFICIENCY • LUXURY • GLOBAL ACCESS</span>
            <span class="text-4xl font-light tracking-[0.5em] text-dorado opacity-80 mx-8">PRIVACY • EFFICIENCY • LUXURY • GLOBAL ACCESS</span>
        </div>
    </section>

    <!-- Features Grid (reuses Services card pattern) -->
    <section id="clientes" class="py-32 px-6 bg-surface">
        <div class="max-w-7xl mx-auto">
            <div class="flex flex-col md:flex-row justify-between items-end mb-20">
                <div class="max-w-2xl">
                    <h2 class="text-4xl md:text-5xl font-light mb-6">Módulos del sistema</h2>
                    <p class="text-gray-400 text-lg font-light">Todo lo necesario para operar una flota charter con total control y transparencia.</p>
                </div>
                <div class="mt-8 md:mt-0">
                    <a href="#" class="text-sm font-bold uppercase tracking-widest border-b border-dorado pb-1 hover:text-dorado transition-colors">Ver documentación</a>
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <!-- Feature Card 1 -->
                <div class="group relative aspect-square md:aspect-auto md:h-[500px] rounded-2xl overflow-hidden cursor-pointer">
                    <img class="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_9434a080ba_3255e21bd97a8787.png" alt="elegant dashboard UI for managing VIP clients on a tablet held by hand, dark mode, gold accents, mod" />
                    <div class="absolute inset-0 bg-vinotinto/80 opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex flex-col items-center justify-center text-center p-8">
                        <i class="fa-solid fa-users-viewfinder text-4xl text-dorado mb-4"></i>
                        <h3 class="text-2xl font-serif mb-2">Gestión de Clientes</h3>
                        <p class="text-sm text-gray-300">Base de datos centralizada con perfiles VIP y preferencias de vuelo.</p>
                    </div>
                </div>

                <!-- Feature Card 2 -->
                <div class="group relative aspect-square md:aspect-auto md:h-[500px] rounded-2xl overflow-hidden cursor-pointer">
                    <img class="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_068ec847eb_bd22ead26bf22b14.png" alt="flight scheduling calendar interface on a sleek monitor in a dim luxury office, aviation theme, mood" />
                    <div class="absolute inset-0 bg-vinotinto/80 opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex flex-col items-center justify-center text-center p-8">
                        <i class="fa-solid fa-plane-departure text-4xl text-dorado mb-4"></i>
                        <h3 class="text-2xl font-serif mb-2">Control de Vuelos</h3>
                        <p class="text-sm text-gray-300">Planificación de itinerarios y asignación de aeronaves en tiempo real.</p>
                    </div>
                </div>

                <!-- Feature Card 3 -->
                <div class="group relative aspect-square md:aspect-auto md:h-[500px] rounded-2xl overflow-hidden cursor-pointer">
                    <img class="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_3526097861_2bb1f74a85ea3f44.png" alt="secure payment processing screen with gold accents, abstract fintech visualization, dark premium aes" />
                    <div class="absolute inset-0 bg-vinotinto/80 opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex flex-col items-center justify-center text-center p-8">
                        <i class="fa-solid fa-file-invoice-dollar text-4xl text-dorado mb-4"></i>
                        <h3 class="text-2xl font-serif mb-2">Reportes de Pago</h3>
                        <p class="text-sm text-gray-300">Facturación automática y auditoría financiera completa.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Gallery Section (reused verbatim structure) -->
    <section id="vuelos" class="py-32 px-6">
        <div class="max-w-7xl mx-auto">
            <div class="flex justify-between items-end mb-16">
                <h2 class="text-4xl md:text-5xl font-light">Experiencia de bordo</h2>
                <div class="flex space-x-4">
                    <button id="prevGallery" class="w-12 h-12 rounded-full border border-white/20 flex items-center justify-center hover:bg-white hover:text-darkbg transition-all"><i class="fa-solid fa-arrow-left"></i></button>
                    <button id="nextGallery" class="w-12 h-12 rounded-full border border-white/20 flex items-center justify-center hover:bg-white hover:text-darkbg transition-all"><i class="fa-solid fa-arrow-right"></i></button>
                </div>
            </div>

            <div id="galleryScroll" class="flex space-x-6 overflow-x-auto hide-scrollbar pb-12 snap-x">
                <!-- Gallery Item 1 -->
                <div class="min-w-[300px] md:min-w-[400px] snap-start">
                    <div class="aspect-square rounded-lg overflow-hidden mb-6 group">
                        <img class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_43a647a58a_b4978f0588f282c7.png" alt="luxury private jet first class seat with champagne, aerial view, cinematic" />
                    </div>
                    <h3 class="text-xl font-medium mb-2">Suite Privada</h3>
                    <p class="text-gray-500 text-sm">Configuración exclusiva para máximo confort en trayectos largos.</p>
                </div>
                <!-- Gallery Item 2 -->
                <div class="min-w-[300px] md:min-w-[400px] snap-start">
                    <div class="aspect-square rounded-lg overflow-hidden mb-6 group">
                        <img class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_efbe327e19_2cfcc8ee82f7f5ea.png" alt="gourmet meal plating on a private aircraft table, fine dining presentation, warm light" />
                    </div>
                    <h3 class="text-xl font-medium mb-2">Catering Gourmet</h3>
                    <p class="text-gray-500 text-sm">Menús personalizados preparados por chefs de alta cocina.</p>
                </div>
                <!-- Gallery Item 3 -->
                <div class="min-w-[300px] md:min-w-[400px] snap-start">
                    <div class="aspect-square rounded-lg overflow-hidden mb-6 group">
                        <img class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_55b0124664_1432a6150564739c.png" alt="private terminal lounge with leather sofas and floor to ceiling windows overlooking runway, dusk" />
                    </div>
                    <h3 class="text-xl font-medium mb-2">Terminal Exclusiva</h3>
                    <p class="text-gray-500 text-sm">Acceso directo sin salas de espera públicas ni filas.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Testimonials (reused verbatim structure) -->
    <section id="reservas" class="py-32 px-6 bg-surface">
        <div class="max-w-7xl mx-auto">
            <h2 class="text-4xl md:text-5xl font-light mb-16 text-center">Lo que dicen nuestros socios</h2>
            <div id="testimonialScroll" class="flex space-x-6 overflow-x-auto hide-scrollbar snap-x">
                <div class="min-w-[300px] md:min-w-[450px] snap-start bg-darkbg p-10 rounded-2xl border border-white/5">
                    <div class="flex items-center space-x-4 mb-6">
                        <img class="w-14 h-14 rounded-full object-cover grayscale" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_48b9f7b8ae_947ff067105f79a8.png" alt="portrait of a confident businessman in his forties, professional headshot, neutral background" />
                        <div>
                            <h4 class="font-bold">Alejandro Vega</h4>
                            <p class="text-xs text-dorado uppercase tracking-widest">CEO, Grupo Horizonte</p>
                        </div>
                    </div>
                    <p class="text-gray-400 font-light italic">"El módulo de gestión de clientes redujo nuestro tiempo de despacho a la mitad. Interfaz impecable."</p>
                </div>
                <div class="min-w-[300px] md:min-w-[450px] snap-start bg-darkbg p-10 rounded-2xl border border-white/5">
                    <div class="flex items-center space-x-4 mb-6">
                        <img class="w-14 h-14 rounded-full object-cover grayscale" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_e94fe1b082_28d5c6e43377d0d5.png" alt="portrait of an elegant woman executive, professional headshot, neutral background" />
                        <div>
                            <h4 class="font-bold">Valentina Ríos</h4>
                            <p class="text-xs text-dorado uppercase tracking-widest">Directora, Aura Jets</p>
                        </div>
                    </div>
                    <p class="text-gray-400 font-light italic">"La facturación automática y los reportes financieros son exactamente lo que necesitábamos."</p>
                </div>
                <div class="min-w-[300px] md:min-w-[450px] snap-start bg-darkbg p-10 rounded-2xl border border-white/5">
                    <div class="flex items-center space-x-4 mb-6">
                        <img class="w-14 h-14 rounded-full object-cover grayscale" src="https://storage.googleapis.com/uxpilot-auth.appspot.com/gen_07ce7ef9fe_b0aa4646f7ff46d3.png" alt="portrait of a mature aviation professional man, professional headshot, neutral background" />
                        <div>
                            <h4 class="font-bold">Roberto Castillo</h4>
                            <p class="text-xs text-dorado uppercase tracking-widest">Operaciones, SkyLink</p>
                        </div>
                    </div>
                    <p class="text-gray-400 font-light italic">"Control total de la flota desde un solo lugar. El soporte técnico responde al instante."</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Pricing / Tiers (reused card grid) -->
    <section id="pagos" class="py-32 px-6">
        <div class="max-w-7xl mx-auto">
            <div class="text-center mb-20">
                <h2 class="text-4xl md:text-5xl font-light mb-6">Planes operativos</h2>
                <p class="text-gray-400 text-lg font-light max-w-2xl mx-auto">Escalabilidad diseñada para operadores de una aeronave hasta grandes flotas.</p>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <div class="p-10 rounded-2xl border border-white/5 hover:border-dorado/30 transition-colors">
                    <p class="text-xs text-dorado uppercase tracking-widest mb-4">Starter</p>
                    <h3 class="text-3xl font-light mb-2">$499<span class="text-sm text-gray-500">/mes</span></h3>
                    <p class="text-gray-500 text-sm mb-8">Para operadores individuales.</p>
                    <ul class="space-y-3 text-sm text-gray-300 mb-10">
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>1 aeronave</li>
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Gestión de clientes</li>
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Soporte email</li>
                    </ul>
                    <a href="#" class="block w-full py-3 border border-white/20 rounded-full text-center text-xs font-bold uppercase tracking-widest hover:bg-white hover:text-darkbg transition-all">Seleccionar</a>
                </div>
                <div class="p-10 rounded-2xl border border-dorado/40 bg-vinotinto/20 relative">
                    <span class="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-dorado text-darkbg px-4 py-1 rounded-full text-[10px] font-bold uppercase tracking-widest">Popular</span>
                    <p class="text-xs text-dorado uppercase tracking-widest mb-4">Executive</p>
                    <h3 class="text-3xl font-light mb-2">$1,299<span class="text-sm text-gray-500">/mes</span></h3>
                    <p class="text-gray-500 text-sm mb-8">Para flotas en crecimiento.</p>
                    <ul class="space-y-3 text-sm text-gray-300 mb-10">
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Hasta 5 aeronaves</li>
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Todos los módulos</li>
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Soporte prioritario</li>
                    </ul>
                    <a href="#" class="block w-full py-3 bg-dorado text-darkbg rounded-full text-center text-xs font-bold uppercase tracking-widest hover:bg-white transition-all">Seleccionar</a>
                </div>
                <div class="p-10 rounded-2xl border border-white/5 hover:border-dorado/30 transition-colors">
                    <p class="text-xs text-dorado uppercase tracking-widest mb-4">Enterprise</p>
                    <h3 class="text-3xl font-light mb-2">Custom</h3>
                    <p class="text-gray-500 text-sm mb-8">Infraestructura dedicada.</p>
                    <ul class="space-y-3 text-sm text-gray-300 mb-10">
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Aeronaves ilimitadas</li>
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>API & integraciones</li>
                        <li><i class="fa-solid fa-check text-dorado mr-2"></i>Gerente de cuenta</li>
                    </ul>
                    <a href="#" class="block w-full py-3 border border-white/20 rounded-full text-center text-xs font-bold uppercase tracking-widest hover:bg-white hover:text-darkbg transition-all">Contactar</a>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer id="contacto" class="bg-darkbg border-t border-white/5 pt-32 pb-12 px-6">
        <div class="max-w-7xl mx-auto">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-12 mb-20">
                <div class="lg:col-span-2">
                    <span class="text-2xl font-serif tracking-tight block mb-6">Charter<span class="text-dorado">.</span>Flight</span>
                    <p class="text-gray-500 font-light max-w-sm">La plataforma líder para la gestión integral de operaciones de aviación charter de lujo.</p>
                </div>
                <div>
                    <h4 class="text-xs font-bold uppercase tracking-widest mb-6">Plataforma</h4>
                    <ul class="space-y-4 text-sm text-gray-400 font-light">
                        <li><a href="${pageContext.request.contextPath}/ClienteServlet?accion=listar" class="hover:text-dorado">Clientes</a></li>
                        <li><a href="#" class="hover:text-dorado">Vuelos</a></li>
                        <li><a href="#" class="hover:text-dorado">Reservas</a></li>
                        <li><a href="#" class="hover:text-dorado">Pagos</a></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-xs font-bold uppercase tracking-widest mb-6">Empresa</h4>
                    <ul class="space-y-4 text-sm text-gray-400 font-light">
                        <li><a href="#" class="hover:text-dorado">Nosotros</a></li>
                        <li><a href="#" class="hover:text-dorado">Carreras</a></li>
                        <li><a href="#" class="hover:text-dorado">Prensa</a></li>
                        <li><a href="#" class="hover:text-dorado">Contacto</a></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-xs font-bold uppercase tracking-widest mb-6">Legal</h4>
                    <ul class="space-y-4 text-sm text-gray-400 font-light">
                        <li><a href="#" class="hover:text-dorado">Privacidad</a></li>
                        <li><a href="#" class="hover:text-dorado">Términos</a></li>
                        <li><a href="#" class="hover:text-dorado">Seguridad</a></li>
                    </ul>
                </div>
            </div>
            <div class="border-t border-white/5 pt-12 flex flex-col md:flex-row justify-between items-center text-xs text-gray-600">
                <p>&copy; 2026 Charter Flight Systems. Todos los derechos reservados.</p>
                <div class="flex space-x-6 mt-4 md:mt-0">
                    <a href="#" class="hover:text-white">Instagram</a>
                    <a href="#" class="hover:text-white">LinkedIn</a>
                    <a href="#" class="hover:text-white">Twitter</a>
                </div>
            </div>
        </div>
    </footer>

    <script>
        const galleryBtns = () => {
            const container = document.getElementById('galleryScroll');
            document.getElementById('prevGallery').addEventListener('click', () => container.scrollBy({ left: -320, behavior: 'smooth' }));
            document.getElementById('nextGallery').addEventListener('click', () => container.scrollBy({ left: 320, behavior: 'smooth' }));
        };
        window.addEventListener('DOMContentLoaded', galleryBtns);
    </script>
</body>
</html>