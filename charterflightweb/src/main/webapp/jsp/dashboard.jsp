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
    <title>Dashboard Ejecutivo | Charter Flight</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.plot.ly/plotly-3.1.1.min.js"></script>
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
    <style>
        .stat-card {
            background: linear-gradient(145deg, #1a050a 0%, #0b0b0b 100%);
            border: 1px solid rgba(212, 175, 55, 0.05);
        }
    </style>
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
            <a href="${pageContext.request.contextPath}/jsp/bookingmanagement.jsp" class="opacity-60 hover:opacity-100 transition-opacity">Reservas</a>
            <a href="#" class="opacity-60 hover:opacity-100 transition-opacity">Pagos</a>
            <a href="#" class="text-dorado">Reportes</a>
        </div>
        <div class="flex items-center space-x-4">
            <button class="text-gray-400 hover:text-white"><i class="fa-solid fa-bell"></i></button>
            <div class="w-8 h-8 rounded-full bg-vinotinto border border-dorado/30 flex items-center justify-center text-[10px] font-bold">AD</div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 p-6 lg:p-12">
        <div class="max-w-7xl mx-auto">
            <header class="mb-12 flex justify-between items-end">
                <div>
                    <h1 class="text-3xl font-serif text-white">Panel de Control Ejecutivo</h1>
                    <p class="text-gray-400 text-sm mt-1">Visión general del rendimiento operativo y financiero.</p>
                </div>
                <div class="text-right">
                    <p class="text-xs font-bold uppercase tracking-widest text-dorado mb-1">Mayo 2026</p>
                    <p class="text-gray-500 text-xs">Actualizado hace 5 min</p>
                </div>
            </header>

            <!-- Stats Grid -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
                <!-- Stat Card 1 -->
                <div class="stat-card p-6 rounded-2xl">
                    <div class="flex justify-between items-start mb-4">
                        <div class="p-2 bg-vinotinto/30 rounded-lg border border-vinotinto/50">
                            <i class="fa-solid fa-users text-dorado"></i>
                        </div>
                        <span class="text-xs text-green-500 font-bold">+12%</span>
                    </div>
                    <p class="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Clientes Registrados</p>
                    <h3 class="text-3xl font-serif">1,248</h3>
                </div>
                <!-- Stat Card 2 -->
                <div class="stat-card p-6 rounded-2xl">
                    <div class="flex justify-between items-start mb-4">
                        <div class="p-2 bg-vinotinto/30 rounded-lg border border-vinotinto/50">
                            <i class="fa-solid fa-plane text-dorado"></i>
                        </div>
                        <span class="text-xs text-dorado font-bold">8 Activos</span>
                    </div>
                    <p class="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Vuelos Disponibles</p>
                    <h3 class="text-3xl font-serif">32</h3>
                </div>
                <!-- Stat Card 3 -->
                <div class="stat-card p-6 rounded-2xl">
                    <div class="flex justify-between items-start mb-4">
                        <div class="p-2 bg-vinotinto/30 rounded-lg border border-vinotinto/50">
                            <i class="fa-solid fa-calendar-check text-dorado"></i>
                        </div>
                        <span class="text-xs text-green-500 font-bold">94% Ocup.</span>
                    </div>
                    <p class="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Reservas Activas</p>
                    <h3 class="text-3xl font-serif">86</h3>
                </div>
                <!-- Stat Card 4 -->
                <div class="stat-card p-6 rounded-2xl">
                    <div class="flex justify-between items-start mb-4">
                        <div class="p-2 bg-vinotinto/30 rounded-lg border border-vinotinto/50">
                            <i class="fa-solid fa-sack-dollar text-dorado"></i>
                        </div>
                        <span class="text-xs text-green-500 font-bold">+18.5%</span>
                    </div>
                    <p class="text-xs font-bold uppercase tracking-widest text-gray-500 mb-1">Ingresos Totales</p>
                    <h3 class="text-3xl font-serif">$2.4M</h3>
                </div>
            </div>

            <!-- Charts Section -->
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <!-- Revenue Chart -->
                <div class="lg:col-span-2 bg-surface p-8 rounded-2xl border border-white/5">
                    <h4 class="text-lg font-serif mb-6 text-dorado">Evolución de Ingresos</h4>
                    <div id="revenueChart" class="h-[400px]"></div>
                </div>

                <!-- Recent Activity -->
                <div class="bg-surface p-8 rounded-2xl border border-white/5">
                    <h4 class="text-lg font-serif mb-6 text-dorado">Actividad Reciente</h4>
                    <div class="space-y-6">
                        <div class="flex items-center space-x-4">
                            <div class="w-2 h-2 rounded-full bg-green-500"></div>
                            <div class="flex-1">
                                <p class="text-sm font-medium">Nueva Reserva Confirmada</p>
                                <p class="text-xs text-gray-500">Alejandro Vega - Vuelo CH-742</p>
                            </div>
                            <span class="text-[10px] text-gray-600">2 min</span>
                        </div>
                        <div class="flex items-center space-x-4">
                            <div class="w-2 h-2 rounded-full bg-dorado"></div>
                            <div class="flex-1">
                                <p class="text-sm font-medium">Vuelo Programado</p>
                                <p class="text-xs text-gray-500">Ruta MAD -> IBZ registrada</p>
                            </div>
                            <span class="text-[10px] text-gray-600">45 min</span>
                        </div>
                        <div class="flex items-center space-x-4">
                            <div class="w-2 h-2 rounded-full bg-vinotinto"></div>
                            <div class="flex-1">
                                <p class="text-sm font-medium">Pago Recibido</p>
                                <p class="text-xs text-gray-500">Ref: INV-88229 - $12,500</p>
                            </div>
                            <span class="text-[10px] text-gray-600">2h</span>
                        </div>
                        <div class="flex items-center space-x-4">
                            <div class="w-2 h-2 rounded-full bg-gray-600"></div>
                            <div class="flex-1">
                                <p class="text-sm font-medium">Cliente Actualizado</p>
                                <p class="text-xs text-gray-500">Preferencias de catering VR</p>
                            </div>
                            <span class="text-[10px] text-gray-600">5h</span>
                        </div>
                    </div>
                    <button class="w-full mt-8 py-3 border border-white/10 rounded-lg text-xs font-bold uppercase tracking-widest hover:bg-white/5 transition-colors">
                        Ver todo el historial
                    </button>
                </div>
            </div>
        </div>
    </main>

    <footer class="py-8 px-6 text-center text-xs text-gray-600 border-t border-white/5">
        <p>&copy; 2026 Charter Flight Systems. Gestión de Aviación Ejecutiva.</p>
    </footer>

    <script>
        window.addEventListener('load', function() {
            try {
                const data = [{
                    x: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
                    y: [120, 150, 140, 190, 240, 210],
                    type: 'scatter',
                    mode: 'lines+markers',
                    name: 'Vuelos Realizados',
                    line: { color: '#D4AF37', width: 3, shape: 'spline' },
                    marker: { size: 8, color: '#D4AF37' },
                    fill: 'tozeroy',
                    fillcolor: 'rgba(212, 175, 55, 0.05)'
                }, {
                    x: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
                    y: [80, 110, 100, 150, 200, 180],
                    type: 'bar',
                    name: 'Proyección',
                    marker: { color: '#5A0F24' },
                    opacity: 0.6
                }];

                const layout = {
                    paper_bgcolor: 'rgba(0,0,0,0)',
                    plot_bgcolor: 'rgba(0,0,0,0)',
                    margin: { t: 20, r: 20, b: 40, l: 40 },
                    font: { family: 'Inter, sans-serif', color: '#9ca3af', size: 10 },
                    showlegend: true,
                    legend: { orientation: 'h', x: 0, y: 1.1 },
                    xaxis: { gridcolor: 'rgba(255,255,255,0.05)' },
                    yaxis: { gridcolor: 'rgba(255,255,255,0.05)' }
                };

                const config = { responsive: true, displayModeBar: false, displaylogo: false };

                Plotly.newPlot('revenueChart', data, layout, config);
            } catch (e) {
                console.error(e);
            }
        });
    </script>

</body>
</html>