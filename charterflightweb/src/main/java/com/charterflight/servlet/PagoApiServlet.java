package com.charterflight.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.charterflight.dao.PagoDAO;
import com.charterflight.modelo.Pago;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/pagos/*")
public class PagoApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final PagoDAO pagoDAO = new PagoDAO();
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
            List<Pago> pagos = pagoDAO.listar();
            escribirJson(response, HttpServletResponse.SC_OK, pagos);
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
                Pago pago = pagoDAO.buscarPorId(id);
                if (pago == null) {
                    escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                            Map.of("error", "Pago no encontrado"));
                } else {
                    escribirJson(response, HttpServletResponse.SC_OK, pago);
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
        Pago pago = objectMapper.readValue(request.getReader(), Pago.class);

        String errorValidacion = validarCamposObligatorios(pago);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        if (pagoDAO.existeNumeroFactura(pago.getNumeroFactura())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El número de factura ya está registrado"));
            return;
        }

        boolean ok = pagoDAO.insertar(pago);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_CREATED, pago);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al crear pago"));
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

        Pago pago = objectMapper.readValue(request.getReader(), Pago.class);
        pago.setIdPago(id);

        String errorValidacion = validarCamposObligatorios(pago);
        if (errorValidacion != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", errorValidacion));
            return;
        }

        Pago existente = pagoDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Pago no encontrado"));
            return;
        }

        if (pagoDAO.existeNumeroFacturaExcepto(pago.getNumeroFactura(), id)) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El número de factura ya está registrado por otro pago"));
            return;
        }

        boolean ok = pagoDAO.actualizar(pago);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK, pago);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al actualizar pago"));
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

        Pago existente = pagoDAO.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Map.of("error", "Pago no encontrado"));
            return;
        }

        boolean ok = pagoDAO.eliminar(id);
        if (ok) {
            escribirJson(response, HttpServletResponse.SC_OK,
                    Map.of("message", "Pago eliminado correctamente"));
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "Error al eliminar pago"));
        }
    }

    private String validarCamposObligatorios(Pago pago) {
        if (pago.getIdReserva() <= 0) {
            return "El campo idReserva es obligatorio";
        }
        if (pago.getIdCliente() <= 0) {
            return "El campo idCliente es obligatorio";
        }
        if (pago.getIdVuelo() <= 0) {
            return "El campo idVuelo es obligatorio";
        }
        if (pago.getMonto() <= 0) {
            return "El monto debe ser un número positivo";
        }
        if (pago.getEstado() == null || pago.getEstado().trim().isEmpty()) {
            return "El campo estado es obligatorio";
        }
        if (pago.getFechaPago() == null || pago.getFechaPago().trim().isEmpty()) {
            return "El campo fechaPago es obligatorio";
        }
        if (pago.getNumeroFactura() == null || pago.getNumeroFactura().trim().isEmpty()) {
            return "El campo numeroFactura es obligatorio";
        }
        return null;
    }
}