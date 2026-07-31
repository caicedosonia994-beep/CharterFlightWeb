<!DOCTYPE html>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    <title>Lista de Clientes | Charter Flight</title>
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
    <main class="flex-1 p-6 lg:p-12">
        <div class="max-w-7xl mx-auto">
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
                <div>
                    <h1 class="text-3xl font-serif text-white">Base de Datos de Clientes</h1>
                    <p class="text-gray-400 text-sm mt-1">Gestión de perfiles VIP y registros históricos.</p>
                </div>
                <div class="flex flex-col sm:flex-row w-full md:w-auto gap-4">
                        <form action="${pageContext.request.contextPath}/ClienteServlet" method="get" class="relative group">
                            <input type="hidden" name="accion" value="buscar">
                            <i class="fa-solid fa-magnifying-glass absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-dorado transition-colors"></i>
                            <input type="text" name="termino" value="${termino}" placeholder="Buscar cliente..." class="bg-surface border border-white/10 rounded-full pl-11 pr-6 py-2.5 text-sm w-full sm:w-64 focus:outline-none focus:border-dorado transition-all">
                        </form>
                    <a href="${pageContext.request.contextPath}/ClienteServlet?accion=editar&id=0" class="bg-dorado text-darkbg px-6 py-2.5 rounded-full text-xs font-bold uppercase tracking-widest hover:bg-white transition-colors text-center">
                        Nuevo Cliente
                    </a>
                </div>
            </div>

            <!-- Mensajes Flash -->
            <c:if test="${not empty mensaje_texto}">
                <div class="mb-6 p-4 rounded-lg border flex items-center ${mensaje_tipo == 'success' ? 'bg-green-500/10 border-green-500/30 text-green-400' : 'bg-red-500/10 border-red-500/30 text-red-400'}">
                    <i class="fa-solid ${mensaje_tipo == 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'} mr-3"></i>
                    <c:out value="${mensaje_texto}" />
                </div>
            </c:if>

            <!-- Client Table -->
            <div class="bg-surface rounded-2xl border border-white/5 shadow-2xl overflow-hidden">
                <div class="overflow-x-auto">
                    <table class="w-full text-left border-collapse">
                        <thead>
                            <tr class="bg-vinotinto/20 border-b border-white/5">
                                <th class="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">ID</th>
                                <th class="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Nombre Completo</th>
                                <th class="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Documento</th>
                                <th class="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Teléfono</th>
                                <th class="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado">Email</th>
                                <th class="px-6 py-4 text-xs font-bold uppercase tracking-widest text-dorado text-right">Acciones</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-white/5">
                            <c:if test="${empty clientes}">
                                <tr>
                                    <td colspan="6" class="px-6 py-8 text-center text-gray-500">No hay clientes registrados.</td>
                                </tr>
                            </c:if>
                            <c:forEach var="cliente" items="${clientes}">
                                <tr class="hover:bg-white/[0.02] transition-colors">
                                    <td class="px-6 py-4 text-sm font-mono text-gray-500">CF-${cliente.idCliente}</td>
                                    <td class="px-6 py-4">
                                        <div class="flex items-center space-x-3">
                                            <div class="w-8 h-8 rounded-full bg-vinotinto/40 flex items-center justify-center text-[10px] font-bold border border-dorado/20">${fn:substring(cliente.nombre, 0, 1)}${fn:substring(cliente.apellido, 0, 1)}</div>
                                            <span class="text-sm font-medium">${cliente.nombre} ${cliente.apellido}</span>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 text-sm text-gray-400">${cliente.documento}</td>
                                    <td class="px-6 py-4 text-sm text-gray-400">${cliente.telefono}</td>
                                    <td class="px-6 py-4 text-sm text-gray-400">${cliente.correo}</td>
                                    <td class="px-6 py-4 text-right space-x-2">
                                        <a href="${pageContext.request.contextPath}/ClienteServlet?accion=editar&id=${cliente.idCliente}" class="p-2 text-gray-500 hover:text-dorado transition-colors" title="Editar"><i class="fa-solid fa-pen-to-square"></i></a>
                                        <a href="${pageContext.request.contextPath}/ClienteServlet?accion=eliminar&id=${cliente.idCliente}" class="p-2 text-gray-500 hover:text-red-500 transition-colors" title="Eliminar"><i class="fa-solid fa-trash-can"></i></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- Pagination -->
                <div class="px-6 py-4 bg-vinotinto/10 border-t border-white/5 flex justify-between items-center text-xs text-gray-500">
                    <span>Mostrando ${fn:length(clientes)} de ${fn:length(clientes)} clientes</span>
                    <div class="flex space-x-2">
                        <button class="px-3 py-1 border border-white/10 rounded hover:bg-white/5 transition-colors">Anterior</button>
                        <button class="px-3 py-1 bg-dorado text-darkbg rounded font-bold transition-colors">1</button>
                        <button class="px-3 py-1 border border-white/10 rounded hover:bg-white/5 transition-colors">2</button>
                        <button class="px-3 py-1 border border-white/10 rounded hover:bg-white/5 transition-colors">Siguiente</button>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <footer class="py-8 px-6 text-center text-xs text-gray-600 border-t border-white/5">
        <p>&copy; 2026 Charter Flight Systems. Gestión de Aviación Ejecutiva.</p>
    </footer>

</body>
</html>