package com.charterflight.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.charterflight.dao.VueloDAO;
import com.charterflight.modelo.Vuelo;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/vuelos/*")
public class VueloApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final VueloDAO vueloDAO = new VueloDAO();
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
            List<Vuelo> vuelos = vueloDAO.listar();
            escribirJson(response, HttpServletResponse.SC_OK, vuelos);
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
                Vuelo vuelo = vueloDAO.buscarPorId(id);
                if (vuelo == null) {
                    escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                            Map.of("error", "Vuelo no encontrado"));
                } else {
                    escribirJson(response, HttpServletResponse.SC_OK, vuelo);
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
        Vuelo vuelo = objectMapper.readValue(request.getReader(), Vuelo.class);

        String errorValidacion = validarCamposObligatorios(vuelo);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        if (vueloDAO.existeCodigo(vuelo.getCodigo())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El código ya está registrado"));
            return;
        }

        boolean ok = vueloDAO.insertar(vuelo);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_CREATED, vuelo);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al crear vuelo"));
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

        Vuelo vuelo = objectMapper.readValue(request.getReader(), Vuelo.class);
        vuelo.setIdVuelo(id);

        String errorValidacion = validarCamposObligatorios(vuelo);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        Vuelo existente = vueloDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Vuelo no encontrado"));
            return;
        }

        if (vueloDAO.existeCodigoExcepto(vuelo.getCodigo(), id)) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El código ya está registrado por otro vuelo"));
            return;
        }

        boolean ok = vueloDAO.actualizar(vuelo);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK, vuelo);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al actualizar vuelo"));
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

        Vuelo existente = vueloDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Vuelo no encontrado"));
            return;
        }

        boolean ok = vueloDAO.eliminar(id);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK,
                    Map.of("message", "Vuelo eliminado correctamente"));
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al eliminar vuelo"));
        }
    }

    private String validarCamposObligatorios(Vuelo vuelo) {
        if (vuelo.getCodigo() == null || vuelo.getCodigo().trim().isEmpty()) {
            return "El campo código es obligatorio";
        }
        if (vuelo.getOrigen() == null || vuelo.getOrigen().trim().isEmpty()) {
            return "El campo origen es obligatorio";
        }
        if (vuelo.getDestino() == null || vuelo.getDestino().trim().isEmpty()) {
            return "El campo destino es obligatorio";
        }
        if (vuelo.getFecha() == null || vuelo.getFecha().trim().isEmpty()) {
            return "El campo fecha es obligatorio";
        }
        if (vuelo.getHora() == null || vuelo.getHora().trim().isEmpty()) {
            return "El campo hora es obligatorio";
        }
        if (vuelo.getTipo() == null || vuelo.getTipo().trim().isEmpty()) {
            return "El campo tipo es obligatorio";
        }
        if (vuelo.getCapacidad() <= 0) {
            return "La capacidad debe ser un número positivo";
        }
        if (vuelo.getEstado() == null || vuelo.getEstado().trim().isEmpty()) {
            return "El campo estado es obligatorio";
        }
        return null;
    }
}
