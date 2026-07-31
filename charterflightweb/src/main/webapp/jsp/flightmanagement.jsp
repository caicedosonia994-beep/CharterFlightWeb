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
    <title>Gestión de Vuelos | Charter Flight</title>
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
                    }
                }
            }
        }
    </script>
</head>
<body class="antialiased font-sans bg-darkbg text-white min-h-screen flex flex-col">

    <!-- Navigation (Shared) -->
    <nav class="w-full z-50 px-6 py-4 flex justify-between items-center bg-darkbg/80 backdrop-blur-md border-b border-white/5">
        <div class="flex items-center space-x-2">
            <a href="${pageContext.request.contextPath}/index.jsp" class="text-xl font-serif tracking-tight">Charter<span class="text-dorado">.</span>Flight</a>
        </div>
        <div class="hidden md:flex space-x-8 text-sm font-medium tracking-wide">
            <a href="${pageContext.request.contextPath}/index.jsp" class="opacity-60 hover:opacity-100 transition-opacity">Inicio</a>
            <a href="${pageContext.request.contextPath}/ClienteServlet?accion=listar" class="opacity-60 hover:opacity-100 transition-opacity">Clientes</a>
            <a href="${pageContext.request.contextPath}/jsp/flightmanagement.jsp" class="text-dorado">Vuelos</a>
            <a href="${pageContext.request.contextPath}/jsp/bookingmanagement.jsp" class="opacity-60 hover:opacity-100 transition-opacity">Reservas</a>
            <a href="#" class="opacity-60 hover:opacity-100 transition-opacity">Pagos</a>
            <a href="#" class="opacity-60 hover:opacity-100 transition-opacity">Reportes</a>
        </div>
        <div class="flex items-center space-x-4">
            <button class="text-gray-400 hover:text-white"><i class="fa-solid fa-bell"></i></button>
            <div class="w-8 h-8 rounded-full bg-vinotinto border border-dorado/30 flex items-center justify-center text-[10px] font-bold">AD</div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 flex items-center justify-center p-6 py-12">
        <div class="w-full max-w-3xl bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
            <div class="bg-vinotinto/30 px-8 py-6 border-b border-white/5 flex justify-between items-center">
                <div>
                    <h1 class="text-2xl font-serif text-dorado">Gestión de Vuelos</h1>
                    <p class="text-gray-400 text-sm mt-1">Configuración de rutas y disponibilidad de flota.</p>
                </div>
                <i class="fa-solid fa-plane-departure text-3xl text-dorado/20"></i>
            </div>
            
            <form id="flightForm" class="p-8 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <!-- Código del Vuelo -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="codigo">Código</label>
                        <input type="text" id="codigo" placeholder="Ej. CH-742" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                    <!-- Origen -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="origen">Origen</label>
                        <input type="text" id="origen" placeholder="Ej. Madrid (MAD)" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                    <!-- Destino -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="destino">Destino</label>
                        <input type="text" id="destino" placeholder="Ej. Ibiza (IBZ)" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Fecha -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="fecha">Fecha de Salida</label>
                        <input type="date" id="fecha" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400">
                    </div>
                    <!-- Hora -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="hora">Hora de Salida</label>
                        <input type="time" id="hora" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400">
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <!-- Tipo de Vuelo -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="tipo">Tipo de Vuelo</label>
                        <select id="tipo" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                            <option value="">Seleccionar...</option>
                            <option value="nacional">Nacional</option>
                            <option value="internacional">Internacional</option>
                            <option value="transcontinental">Transcontinental</option>
                        </select>
                    </div>
                    <!-- Capacidad -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="capacidad">Capacidad (Pax)</label>
                        <input type="number" id="capacidad" placeholder="Ej. 12" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                    <!-- Estado -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="estado">Estado</label>
                        <select id="estado" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                            <option value="programado">Programado</option>
                            <option value="confirmado">Confirmado</option>
                            <option value="en_vuelo">En Vuelo</option>
                            <option value="cancelado">Cancelado</option>
                        </select>
                    </div>
                </div>

                <div class="pt-6 flex flex-col md:flex-row gap-4">
                    <button type="submit" class="flex-1 bg-vinotinto text-white border border-vinotinto hover:bg-vinotinto/80 py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs transition-colors flex items-center justify-center space-x-2">
                        <i class="fa-solid fa-plus"></i>
                        <span>Registrar Vuelo</span>
                    </button>
                    <button type="reset" class="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors">
                        Limpiar
                    </button>
                </div>
            </form>
        </div>
    </main>

    <footer class="py-8 px-6 text-center text-xs text-gray-600 border-t border-white/5">
        <p>&copy; 2026 Charter Flight Systems. Gestión de Aviación Ejecutiva.</p>
    </footer>

</body>
</html>