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
    <title>Gestión de Reservas | Charter Flight</title>
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
            <a href="${pageContext.request.contextPath}/jsp/flightmanagement.jsp" class="opacity-60 hover:opacity-100 transition-opacity">Vuelos</a>
            <a href="${pageContext.request.contextPath}/jsp/bookingmanagement.jsp" class="text-dorado">Reservas</a>
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
                    <h1 class="text-2xl font-serif text-dorado">Gestión de Reservas</h1>
                    <p class="text-gray-400 text-sm mt-1">Vinculación de clientes VIP con vuelos programados.</p>
                </div>
                <i class="fa-solid fa-calendar-check text-3xl text-dorado/20"></i>
            </div>
            
            <form id="bookingForm" class="p-8 space-y-6">
                <!-- Seleccionar Cliente -->
                <div class="space-y-2">
                    <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="cliente">Seleccionar Cliente</label>
                    <div class="relative">
                        <select id="cliente" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                            <option value="">Buscar cliente...</option>
                            <option value="1">Alejandro Vega (PAS-88223311)</option>
                            <option value="2">Valentina Ríos (PAS-55446677)</option>
                            <option value="3">Roberto Castillo (PAS-99887766)</option>
                        </select>
                        <i class="fa-solid fa-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-gray-600 pointer-events-none"></i>
                    </div>
                </div>

                <!-- Seleccionar Vuelo -->
                <div class="space-y-2">
                    <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="vuelo">Seleccionar Vuelo</label>
                    <div class="relative">
                        <select id="vuelo" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                            <option value="">Seleccionar itinerario...</option>
                            <option value="v1">CH-742 | Madrid (MAD) -> Ibiza (IBZ) | 24 May</option>
                            <option value="v2">CH-910 | Barcelona (BCN) -> Dubái (DXB) | 26 May</option>
                            <option value="v3">CH-115 | Londres (LHR) -> Niza (NCE) | 28 May</option>
                        </select>
                        <i class="fa-solid fa-chevron-down absolute right-4 top-1/2 -translate-y-1/2 text-gray-600 pointer-events-none"></i>
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <!-- Pasajeros -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="pasajeros">Cant. Pasajeros</label>
                        <input type="number" id="pasajeros" min="1" placeholder="Ej. 4" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                    <!-- Fecha Reserva -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="fechaReserva">Fecha de Reserva</label>
                        <input type="date" id="fechaReserva" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors text-gray-400">
                    </div>
                    <!-- Estado Reserva -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="estado">Estado</label>
                        <select id="estado" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors appearance-none">
                            <option value="pendiente">Pendiente de Pago</option>
                            <option value="confirmada">Confirmada</option>
                            <option value="completada">Completada</option>
                            <option value="cancelada">Cancelada</option>
                        </select>
                    </div>
                </div>

                <div class="pt-6 flex flex-col md:flex-row gap-4">
                    <button type="submit" class="flex-1 bg-dorado text-darkbg py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white transition-colors flex items-center justify-center space-x-2">
                        <i class="fa-solid fa-check"></i>
                        <span>Confirmar Reserva</span>
                    </button>
                    <button type="reset" class="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors">
                        Limpiar
                    </button>
                </div>
            </form>
        </div>
    </main>

    <footer class="py-8 px-6 text-center text-xs text-gray-600 border-t border-white/5">>
        <p>&copy; 2026 Charter Flight Systems. Gestión de Aviación Ejecutiva.</p>
    </footer

</body>
</html>