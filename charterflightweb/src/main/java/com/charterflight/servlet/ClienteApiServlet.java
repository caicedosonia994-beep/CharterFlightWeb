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

        String errorValidacion = validarCliente(cliente);
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

        String errorValidacion = validarCliente(cliente);
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

    private static final int MIN_LONGITUD_TEXTO = 2;
    private static final int MAX_LONGITUD_NOMBRE = 100;
    private static final int MAX_LONGITUD_APELLIDO = 100;
    private static final int MIN_LONGITUD_DOCUMENTO = 5;
    private static final int MAX_LONGITUD_DOCUMENTO = 50;
    private static final int MIN_LONGITUD_TELEFONO = 7;
    private static final int MAX_LONGITUD_TELEFONO = 30;
    private static final int MIN_LONGITUD_CORREO = 5;
    private static final int MAX_LONGITUD_CORREO = 100;
    private static final String REGEX_NOMBRE = "^[A-Za-zÀ-ÿ\\s\\'-]+$";
    private static final String REGEX_TELEFONO = "^[0-9+\\s\\-()]+$";
    private static final String REGEX_CORREO = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$";

    private String validarCliente(Cliente cliente) {
        String nombre = cliente.getNombre();
        String apellido = cliente.getApellido();
        String documento = cliente.getDocumento();
        String telefono = cliente.getTelefono();
        String correo = cliente.getCorreo();

        if (nombre == null || nombre.trim().isEmpty()) {
            return "El campo nombre es obligatorio";
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            return "El campo apellido es obligatorio";
        }
        if (documento == null || documento.trim().isEmpty()) {
            return "El campo documento es obligatorio";
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            return "El campo telefono es obligatorio";
        }
        if (correo == null || correo.trim().isEmpty()) {
            return "El campo correo es obligatorio";
        }

        if (nombre.trim().length() < MIN_LONGITUD_TEXTO || nombre.trim().length() > MAX_LONGITUD_NOMBRE) {
            return "El nombre debe tener entre " + MIN_LONGITUD_TEXTO + " y " + MAX_LONGITUD_NOMBRE + " caracteres";
        }
        if (apellido.trim().length() < MIN_LONGITUD_TEXTO || apellido.trim().length() > MAX_LONGITUD_APELLIDO) {
            return "El apellido debe tener entre " + MIN_LONGITUD_TEXTO + " y " + MAX_LONGITUD_APELLIDO + " caracteres";
        }
        if (documento.trim().length() < MIN_LONGITUD_DOCUMENTO || documento.trim().length() > MAX_LONGITUD_DOCUMENTO) {
            return "El documento debe tener entre " + MIN_LONGITUD_DOCUMENTO + " y " + MAX_LONGITUD_DOCUMENTO + " caracteres";
        }
        if (telefono.trim().length() < MIN_LONGITUD_TELEFONO || telefono.trim().length() > MAX_LONGITUD_TELEFONO) {
            return "El telefono debe tener entre " + MIN_LONGITUD_TELEFONO + " y " + MAX_LONGITUD_TELEFONO + " caracteres";
        }
        if (!telefono.matches(".*\\d.*")) {
            return "El telefono debe contener al menos un numero";
        }
        if (!telefono.trim().matches(REGEX_TELEFONO)) {
            return "El telefono solo debe contener numeros, espacios, + o -";
        }
        if (correo.trim().length() < MIN_LONGITUD_CORREO || correo.trim().length() > MAX_LONGITUD_CORREO) {
            return "El correo debe tener entre " + MIN_LONGITUD_CORREO + " y " + MAX_LONGITUD_CORREO + " caracteres";
        }
        if (!correo.trim().matches(REGEX_CORREO)) {
            return "El correo electronico no tiene un formato valido";
        }
        if (!nombre.trim().matches(REGEX_NOMBRE)) {
            return "El nombre solo debe contener letras, espacios o apostrofos";
        }
        if (!apellido.trim().matches(REGEX_NOMBRE)) {
            return "El apellido solo debe contener letras, espacios o apostrofos";
        }

        return null;
    }
}
