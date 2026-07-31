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
    <title>Registro de Clientes | Charter Flight</title>
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
            <a href="${pageContext.request.contextPath}/ClienteServlet?accion=listar" class="text-dorado">Clientes</a>
            <a href="${pageContext.request.contextPath}/jsp/flightmanagement.jsp" class="opacity-60 hover:opacity-100 transition-opacity">Vuelos</a>
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
        <div class="w-full max-w-2xl bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
            <div class="bg-vinotinto/30 px-8 py-6 border-b border-white/5">
                <h1 class="text-2xl font-serif text-dorado">Registro de Nuevo Cliente</h1>
                <p class="text-gray-400 text-sm mt-1">Complete los datos para dar de alta a un nuevo pasajero VIP.</p>
            </div>
            
            <form id="clientForm" class="p-8 space-y-6"
                  action="${pageContext.request.contextPath}/ClienteServlet" method="post">
                <c:if test="${not empty errores}">
                    <div class="mx-8 mt-4 p-4 bg-red-500/10 border border-red-500/30 rounded-lg">
                        <ul class="text-sm text-red-400 space-y-1">
                            <c:forEach var="error" items="${errores}">
                                <li><i class="fa-solid fa-circle-exclamation mr-2"></i><c:out value="${error}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                <c:if test="${not empty cliente and cliente.idCliente > 0}">
                    <input type="hidden" name="accion" value="actualizar">
                    <input type="hidden" name="idCliente" value="${cliente.idCliente}">
                </c:if>
                <c:if test="${empty cliente or cliente.idCliente <= 0}">
                    <input type="hidden" name="accion" value="registrar">
                </c:if>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Nombre -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="nombre">Nombre</label>
                        <input type="text" id="nombre" name="nombre" value="${cliente.nombre}" placeholder="Ej. Juan" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                    <!-- Apellido -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="apellido">Apellido</label>
                        <input type="text" id="apellido" name="apellido" value="${cliente.apellido}" placeholder="Ej. Pérez" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Documento -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="documento">Documento / Pasaporte</label>
                        <input type="text" id="documento" name="documento" value="${cliente.documento}" placeholder="Ej. A12345678" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                    <!-- Teléfono -->
                    <div class="space-y-2">
                        <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="telefono">Teléfono</label>
                        <input type="tel" id="telefono" name="telefono" value="${cliente.telefono}" placeholder="Ej. +34 600 000 000" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                    </div>
                </div>

                <!-- Email -->
                <div class="space-y-2">
                    <label class="text-xs font-bold uppercase tracking-widest text-gray-400" for="email">Correo Electrónico</label>
                    <input type="email" id="email" name="correo" value="${cliente.correo}" placeholder="juan.perez@ejemplo.com" class="w-full bg-darkbg border border-white/10 rounded-lg px-4 py-3 text-sm focus:outline-none focus:border-dorado transition-colors">
                </div>

                <div class="pt-6 flex flex-col md:flex-row gap-4">
                    <button type="submit" class="flex-1 bg-dorado text-darkbg py-3.5 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white transition-colors flex items-center justify-center space-x-2">
                        <i class="fa-solid fa-user-plus"></i>
                        <span>Registrar Cliente</span>
                    </button>
                    <button type="reset" class="px-8 py-3.5 border border-white/10 rounded-lg font-bold uppercase tracking-widest text-xs hover:bg-white/5 transition-colors">
                        Limpiar
                    </button>
                </div>
                
                <div class="text-center">
                    <a href="${pageContext.request.contextPath}/ClienteServlet?accion=listar" class="text-xs text-gray-500 hover:text-dorado transition-colors inline-flex items-center space-x-2">
                        <i class="fa-solid fa-arrow-left"></i>
                        <span>Regresar al Inicio</span>
                    </a>
                </div>
            </form>
        </div>
    </main>

    <footer class="py-8 px-6 text-center text-xs text-gray-600 border-t border-white/5">
        <p>&copy; 2026 Charter Flight Systems. Gestión de Aviación Ejecutiva.</p>
    </footer>

</body>
</html>