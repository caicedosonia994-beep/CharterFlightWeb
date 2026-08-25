package com.charterflight.servlet;

import java.io.IOException;
import java.util.List;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Cliente> clientes = clienteDAO.listar();
            objectMapper.writeValue(response.getWriter(), clientes);
        } else {
            String[] parts = pathInfo.split("/");
            if (parts.length >= 2) {
                int id = Integer.parseInt(parts[1]);
                Cliente cliente = clienteDAO.buscarPorId(id);
                if (cliente == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "Cliente no encontrado"));
                } else {
                    objectMapper.writeValue(response.getWriter(), cliente);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "ID inválido"));
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
        boolean ok = clienteDAO.insertar(cliente);
        if (ok) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), cliente);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "Error al crear cliente"));
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "ID requerido"));
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "ID inválido"));
            return;
        }
        int id = Integer.parseInt(parts[1]);
        Cliente cliente = objectMapper.readValue(request.getReader(), Cliente.class);
        cliente.setIdCliente(id);
        boolean ok = clienteDAO.actualizar(cliente);
        if (ok) {
            objectMapper.writeValue(response.getWriter(), cliente);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "Error al actualizar cliente"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "ID requerido"));
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "ID inválido"));
            return;
        }
        int id = Integer.parseInt(parts[1]);
        boolean ok = clienteDAO.eliminar(id);
        if (ok) {
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "Cliente eliminado correctamente"));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "Error al eliminar cliente"));
        }
    }
}
