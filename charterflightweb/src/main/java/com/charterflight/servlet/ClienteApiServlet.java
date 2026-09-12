package com.charterflight.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.charterflight.dao.ClienteDAO;
import com.charterflight.modelo.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/clientes/*")
public class ClienteApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void escribirJson(HttpServletResponse response, int status, Object cuerpo) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), cuerpo);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Cliente> clientes = clienteDAO.listar();
            escribirJson(response, HttpServletResponse.SC_OK, clientes);
        } else {
            String[] parts = pathInfo.split("/");
            if (parts.length >= 2) {
                int id;
                try {
                    id = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                            Map.of("error", "ID no numérico"));
                    return;
                }
                Cliente cliente = clienteDAO.buscarPorId(id);
                if (cliente == null) {
                    escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                            Map.of("error", "Cliente no encontrado"));
                } else {
                    escribirJson(response, HttpServletResponse.SC_OK, cliente);
                }
            } else {
                escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("error", "ID inválido"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        Cliente cliente = objectMapper.readValue(request.getReader(), Cliente.class);

        String errorValidacion = validarCamposObligatorios(cliente);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        if (clienteDAO.existeDocumento(cliente.getDocumento())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El documento ya está registrado"));
            return;
        }

        boolean ok = clienteDAO.insertar(cliente);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_CREATED, cliente);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al crear cliente"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID requerido"));
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID inválido"));
            return;
        }
        int id;
        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID no numérico"));
            return;
        }

        Cliente cliente = objectMapper.readValue(request.getReader(), Cliente.class);
        cliente.setIdCliente(id);

        String errorValidacion = validarCamposObligatorios(cliente);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        Cliente existente = clienteDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Cliente no encontrado"));
            return;
        }

        if (clienteDAO.existeDocumentoExcepto(cliente.getDocumento(), id)) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El documento ya está registrado por otro cliente"));
            return;
        }

        boolean ok = clienteDAO.actualizar(cliente);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK, cliente);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al actualizar cliente"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID requerido"));
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID inválido"));
            return;
        }
        int id;
        try {
            id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "ID no numérico"));
            return;
        }

        Cliente existente = clienteDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Cliente no encontrado"));
            return;
        }

        boolean ok = clienteDAO.eliminar(id);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK,
                    Map.of("message", "Cliente eliminado correctamente"));
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al eliminar cliente"));
        }
    }

    private String validarCamposObligatorios(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            return "El campo nombre es obligatorio";
        }
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            return "El campo apellido es obligatorio";
        }
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            return "El campo documento es obligatorio";
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            return "El campo telefono es obligatorio";
        }
        if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
            return "El campo correo es obligatorio";
        }
        return null;
    }
}
