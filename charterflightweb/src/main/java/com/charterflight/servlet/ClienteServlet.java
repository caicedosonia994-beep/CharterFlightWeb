package com.charterflight.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.charterflight.dao.ClienteDAO;
import com.charterflight.modelo.Cliente;

@WebServlet("/ClienteServlet")
public class ClienteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ClienteDAO clienteDAO;

    @Override
    public void init() throws ServletException {
        clienteDAO = new ClienteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarClientes(request, response);
                break;
            case "buscar":
                buscarClientes(request, response);
                break;
            case "eliminar":
                eliminarCliente(request, response);
                break;
            case "editar":
                mostrarFormularioEdicion(request, response);
                break;
            default:
                listarClientes(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "registrar":
                registrarCliente(request, response);
                break;
            case "actualizar":
                actualizarCliente(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/ClienteServlet?accion=listar");
                break;
        }
    }

    private void listarClientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Cliente> clientes = clienteDAO.listar();
        request.setAttribute("clientes", clientes);
        colocarMensajeFlash(request);
        request.getRequestDispatcher("/jsp/listaclientes.jsp").forward(request, response);
    }

    private void buscarClientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String termino = request.getParameter("termino");
        if (termino == null || termino.trim().isEmpty()) {
            listarClientes(request, response);
            return;
        }
        List<Cliente> clientes = clienteDAO.buscar(termino.trim());
        request.setAttribute("clientes", clientes);
        request.setAttribute("termino", termino.trim());
        colocarMensajeFlash(request);
        request.getRequestDispatcher("/jsp/listaclientes.jsp").forward(request, response);
    }

    private void registrarCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String documento = request.getParameter("documento");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");

        List<String> errores = validarCampos(nombre, apellido, documento, telefono, correo, false, 0);

        if (!errores.isEmpty()) {
            request.setAttribute("errores", errores);
            request.setAttribute("cliente", new Cliente(nombre, apellido, documento, telefono, correo));
            request.getRequestDispatcher("/jsp/registrarclientes.jsp").forward(request, response);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre.trim());
        cliente.setApellido(apellido.trim());
        cliente.setDocumento(documento.trim());
        cliente.setTelefono(telefono.trim());
        cliente.setCorreo(correo.trim());

        boolean ok = clienteDAO.insertar(cliente);
        if (ok) {
            setMensajeFlash(request, "success", "Cliente registrado correctamente.");
        } else {
            setMensajeFlash(request, "error", "Error al registrar el cliente. Verifique la conexión a la base de datos.");
        }
        response.sendRedirect(request.getContextPath() + "/ClienteServlet?accion=listar");
    }

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = Integer.parseInt(request.getParameter("id"));
        if (idCliente == 0) {
            request.getRequestDispatcher("/jsp/registrarclientes.jsp").forward(request, response);
            return;
        }
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            setMensajeFlash(request, "error", "Cliente no encontrado.");
            response.sendRedirect(request.getContextPath() + "/ClienteServlet?accion=listar");
            return;
        }
        request.setAttribute("cliente", cliente);
        request.getRequestDispatcher("/jsp/registrarclientes.jsp").forward(request, response);
    }

    private void actualizarCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String documento = request.getParameter("documento");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");

        List<String> errores = validarCampos(nombre, apellido, documento, telefono, correo, true, idCliente);

        if (!errores.isEmpty()) {
            request.setAttribute("errores", errores);
            request.setAttribute("cliente", new Cliente(idCliente, nombre, apellido, documento, telefono, correo));
            request.getRequestDispatcher("/jsp/registrarclientes.jsp").forward(request, response);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setNombre(nombre.trim());
        cliente.setApellido(apellido.trim());
        cliente.setDocumento(documento.trim());
        cliente.setTelefono(telefono.trim());
        cliente.setCorreo(correo.trim());

        boolean ok = clienteDAO.actualizar(cliente);
        if (ok) {
            setMensajeFlash(request, "success", "Cliente actualizado correctamente.");
        } else {
            setMensajeFlash(request, "error", "Error al actualizar el cliente.");
        }
        response.sendRedirect(request.getContextPath() + "/ClienteServlet?accion=listar");
    }

    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idCliente = Integer.parseInt(request.getParameter("id"));
        boolean ok = clienteDAO.eliminar(idCliente);
        if (ok) {
            setMensajeFlash(request, "success", "Cliente eliminado correctamente.");
        } else {
            setMensajeFlash(request, "error", "Error al eliminar el cliente.");
        }
        response.sendRedirect(request.getContextPath() + "/ClienteServlet?accion=listar");
    }

    private List<String> validarCampos(String nombre, String apellido, String documento,
                                       String telefono, String correo, boolean esEdicion, int idCliente) {
        List<String> errores = new ArrayList<>();

        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add("El nombre es obligatorio.");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            errores.add("El apellido es obligatorio.");
        }
        if (documento == null || documento.trim().isEmpty()) {
            errores.add("El documento es obligatorio.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            errores.add("El teléfono es obligatorio.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            errores.add("El correo electrónico es obligatorio.");
        }

        if (correo != null && !correo.trim().isEmpty()) {
            if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[\\w.-]+$")) {
                errores.add("El correo electrónico no tiene un formato válido.");
            }
        }

        if (telefono != null && !telefono.trim().isEmpty()) {
            if (!telefono.matches("^[+0-9\\s\\-]+$")) {
                errores.add("El teléfono solo debe contener números, espacios, '+' o '-'.");
            }
        }

        if (documento != null && !documento.trim().isEmpty()) {
            if (esEdicion) {
                if (clienteDAO.existeDocumentoExcepto(documento.trim(), idCliente)) {
                    errores.add("Ya existe un cliente con ese documento.");
                }
            } else {
                if (clienteDAO.existeDocumento(documento.trim())) {
                    errores.add("Ya existe un cliente con ese documento.");
                }
            }
        }

        return errores;
    }

    private void setMensajeFlash(HttpServletRequest request, String tipo, String mensaje) {
        HttpSession session = request.getSession();
        session.setAttribute("mensaje_tipo", tipo);
        session.setAttribute("mensaje_texto", mensaje);
    }

    private void colocarMensajeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String tipo = (String) session.getAttribute("mensaje_tipo");
        String texto = (String) session.getAttribute("mensaje_texto");
        if (tipo != null && texto != null) {
            request.setAttribute("mensaje_tipo", tipo);
            request.setAttribute("mensaje_texto", texto);
            session.removeAttribute("mensaje_tipo");
            session.removeAttribute("mensaje_texto");
        }
    }
}
