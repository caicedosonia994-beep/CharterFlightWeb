package com.charterflight.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.charterflight.dao.ReservaDAO;
import com.charterflight.modelo.Reserva;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/reservas/*")
public class ReservaApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ReservaDAO reservaDAO = new ReservaDAO();
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
            List<Reserva> reservas = reservaDAO.listar();
            escribirJson(response, HttpServletResponse.SC_OK, reservas);
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
                Reserva reserva = reservaDAO.buscarPorId(id);
                if (reserva == null) {
                    escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                            Map.of("error", "Reserva no encontrada"));
                } else {
                    escribirJson(response, HttpServletResponse.SC_OK, reserva);
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
        Reserva reserva = objectMapper.readValue(request.getReader(), Reserva.class);

        String errorValidacion = validarCamposObligatorios(reserva);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        if (!reservaDAO.existeCliente(reserva.getIdCliente())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El cliente no existe"));
            return;
        }

        if (!reservaDAO.existeVuelo(reserva.getIdVuelo())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El vuelo no existe"));
            return;
        }

        boolean ok = reservaDAO.insertar(reserva);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_CREATED, reserva);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al crear reserva"));
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

        Reserva reserva = objectMapper.readValue(request.getReader(), Reserva.class);
        reserva.setIdReserva(id);

        String errorValidacion = validarCamposObligatorios(reserva);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        Reserva existente = reservaDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Reserva no encontrada"));
            return;
        }

        if (!reservaDAO.existeCliente(reserva.getIdCliente())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El cliente no existe"));
            return;
        }

        if (!reservaDAO.existeVuelo(reserva.getIdVuelo())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El vuelo no existe"));
            return;
        }

        boolean ok = reservaDAO.actualizar(reserva);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK, reserva);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al actualizar reserva"));
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

        Reserva existente = reservaDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Reserva no encontrada"));
            return;
        }

        boolean ok = reservaDAO.eliminar(id);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK,
                    Map.of("message", "Reserva eliminada correctamente"));
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al eliminar reserva"));
        }
    }

    private String validarCamposObligatorios(Reserva reserva) {
        if (reserva.getIdCliente() <= 0) {
            return "El campo idCliente es obligatorio";
        }
        if (reserva.getIdVuelo() <= 0) {
            return "El campo idVuelo es obligatorio";
        }
        if (reserva.getPasajeros() <= 0) {
            return "La cantidad de pasajeros debe ser mayor que 0";
        }
        if (reserva.getFechaReserva() == null || reserva.getFechaReserva().trim().isEmpty()) {
            return "El campo fechaReserva es obligatorio";
        }
        if (reserva.getEstado() == null || reserva.getEstado().trim().isEmpty()) {
            return "El campo estado es obligatorio";
        }
        return null;
    }
}