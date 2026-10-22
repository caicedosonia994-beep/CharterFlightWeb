package com.charterflight.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.charterflight.dao.SolicitudEspecialDAO;
import com.charterflight.modelo.SolicitudEspecial;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/solicitudes-especiales/*")
public class SolicitudEspecialApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> CAMPOS_EDITABLES = Set.of(
            "nombreContacto", "correoContacto", "telefonoContacto", "tipo", "origen", "destino",
            "fechaReq", "tipoServicio", "avionSolicitado", "observaciones", "tipoMercancia", "descripcionCarga",
            "motivoViaje", "pasajerosEstimados", "estado");
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "Pendiente de revisión", "En revisión", "Aprobada", "Rechazada");

    private final SolicitudEspecialDAO dao = new SolicitudEspecialDAO();
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
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            escribirJson(response, HttpServletResponse.SC_OK, dao.listar());
            return;
        }

        Integer id = obtenerId(pathInfo);
        if (id == null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "ID inválido"));
            return;
        }
        SolicitudEspecial solicitud = dao.buscarPorId(id);
        if (solicitud == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Solicitud no encontrada"));
        } else {
            escribirJson(response, HttpServletResponse.SC_OK, solicitud);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        SolicitudEspecial solicitud;
        try {
            solicitud = objectMapper.readValue(request.getReader(), SolicitudEspecial.class);
        } catch (Exception e) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "El cuerpo JSON no es válido"));
            return;
        }

        String error = validar(solicitud);
        if (error != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", error));
            return;
        }
        solicitud.setEstado("Pendiente de revisión");
        int filas = dao.insertar(solicitud);
        if (filas > 0) {
            escribirJson(response, HttpServletResponse.SC_CREATED, solicitud);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "No fue posible registrar la solicitud"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer id = obtenerId(request.getPathInfo());
        if (id == null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "ID inválido o requerido"));
            return;
        }

        SolicitudEspecial existente = dao.buscarPorId(id);
        if (existente == null) {
            escribirJson(response, HttpServletResponse.SC_NOT_FOUND, Map.of("error", "Solicitud no encontrada"));
            return;
        }

        Map<String, Object> cuerpo;
        try {
            cuerpo = objectMapper.readValue(request.getReader(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        } catch (Exception e) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "El cuerpo JSON no es válido"));
            return;
        }
        if (cuerpo == null || cuerpo.isEmpty()) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "El cuerpo JSON no puede estar vacío"));
            return;
        }
        if (!CAMPOS_EDITABLES.containsAll(cuerpo.keySet())) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "La solicitud contiene campos no permitidos"));
            return;
        }

        // Mantiene compatible el PUT existente que solo cambia el estado.
        if (cuerpo.size() == 1 && cuerpo.containsKey("estado")) {
            Object valorEstado = cuerpo.get("estado");
            if (!(valorEstado instanceof String) || !ESTADOS_VALIDOS.contains(((String) valorEstado).trim())) {
                escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", "Estado no permitido"));
                return;
            }
            String estado = ((String) valorEstado).trim();
            int filas = dao.actualizarEstado(id, estado);
            if (filas > 0) {
                existente.setEstado(estado);
                escribirJson(response, HttpServletResponse.SC_OK, existente);
            } else {
                escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        Map.of("error", "No fue posible actualizar el estado"));
            }
            return;
        }

        try {
            aplicarCamposPresentes(existente, cuerpo);
        } catch (IllegalArgumentException e) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", e.getMessage()));
            return;
        }
        if (cuerpo.containsKey("telefonoContacto") && !vacio(existente.getTelefonoContacto())
                && !existente.getTelefonoContacto().matches("[0-9]{1,30}")) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("error", "El teléfono actualizado debe contener solo dígitos"));
            return;
        }
        String error = validar(existente);
        if (error != null) {
            escribirJson(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("error", error));
            return;
        }

        int filas = dao.actualizar(existente);
        if (filas > 0) {
            SolicitudEspecial actualizada = dao.buscarPorId(id);
            escribirJson(response, HttpServletResponse.SC_OK, actualizada == null ? existente : actualizada);
        } else {
            escribirJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("error", "No fue posible actualizar la solicitud"));
        }
    }

    private void aplicarCamposPresentes(SolicitudEspecial s, Map<String, Object> datos) {
        s.setNombreContacto(cadena(datos, "nombreContacto", s.getNombreContacto()));
        s.setCorreoContacto(cadena(datos, "correoContacto", s.getCorreoContacto()));
        s.setTelefonoContacto(cadena(datos, "telefonoContacto", s.getTelefonoContacto()));
        s.setTipo(cadena(datos, "tipo", s.getTipo()));
        s.setOrigen(cadena(datos, "origen", s.getOrigen()));
        s.setDestino(cadena(datos, "destino", s.getDestino()));
        s.setFechaReq(cadena(datos, "fechaReq", s.getFechaReq()));
        s.setTipoServicio(cadena(datos, "tipoServicio", s.getTipoServicio()));
        s.setAvionSolicitado(cadena(datos, "avionSolicitado", s.getAvionSolicitado()));
        s.setObservaciones(cadena(datos, "observaciones", s.getObservaciones()));
        s.setTipoMercancia(cadena(datos, "tipoMercancia", s.getTipoMercancia()));
        s.setDescripcionCarga(cadena(datos, "descripcionCarga", s.getDescripcionCarga()));
        s.setMotivoViaje(cadena(datos, "motivoViaje", s.getMotivoViaje()));
        if (datos.containsKey("pasajerosEstimados")) {
            Object valor = datos.get("pasajerosEstimados");
            if (valor == null) {
                s.setPasajerosEstimados(null);
            } else if (valor instanceof Number && ((Number) valor).doubleValue() == ((Number) valor).intValue()) {
                s.setPasajerosEstimados(((Number) valor).intValue());
            } else {
                throw new IllegalArgumentException("pasajerosEstimados debe ser un número entero o null");
            }
        }
        if (datos.containsKey("estado")) {
            Object valor = datos.get("estado");
            if (!(valor instanceof String) || !ESTADOS_VALIDOS.contains(((String) valor).trim())) {
                throw new IllegalArgumentException("Estado no permitido");
            }
            s.setEstado(((String) valor).trim());
        }
    }

    private String cadena(Map<String, Object> datos, String campo, String actual) {
        if (!datos.containsKey(campo)) return actual;
        Object valor = datos.get(campo);
        if (valor == null) return null;
        if (!(valor instanceof String)) throw new IllegalArgumentException(campo + " debe ser texto");
        return ((String) valor).trim();
    }

    private Integer obtenerId(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) return null;
        String[] partes = pathInfo.split("/");
        if (partes.length != 2) return null;
        try {
            int id = Integer.parseInt(partes[1]);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String validar(SolicitudEspecial s) {
        if (s == null) return "La solicitud es obligatoria";
        if (vacio(s.getNombreContacto())) return "El nombre de contacto es obligatorio";
        if (s.getNombreContacto().length() > 100) return "El nombre de contacto admite hasta 100 caracteres";
        if (vacio(s.getCorreoContacto()) || !s.getCorreoContacto().contains("@")) return "Ingresa un correo electrónico válido";
        if (s.getCorreoContacto().length() > 100) return "El correo admite hasta 100 caracteres";
        if (vacio(s.getTelefonoContacto())) return "El teléfono es obligatorio";
        if (s.getTelefonoContacto().length() > 30) return "El teléfono admite hasta 30 caracteres";
        if (vacio(s.getTipo())) return "El tipo de solicitud es obligatorio";
        if (s.getTipo().length() > 20) return "El tipo de solicitud admite hasta 20 caracteres";
        if (vacio(s.getOrigen()) || s.getOrigen().length() < 2 || s.getOrigen().length() > 100) return "El origen debe tener entre 2 y 100 caracteres";
        if (vacio(s.getDestino()) || s.getDestino().length() < 2 || s.getDestino().length() > 100) return "El destino debe tener entre 2 y 100 caracteres";
        if (vacio(s.getFechaReq()) || !fechaValida(s.getFechaReq())) return "La fecha requerida debe tener formato AAAA-MM-DD";
        if (vacio(s.getTipoServicio())) return "El tipo de servicio es obligatorio";
        if (s.getTipoServicio().length() > 30) return "El tipo de servicio admite hasta 30 caracteres";
        if (s.getAvionSolicitado() != null && !Set.of("Beechcraft 1900D", "LET L-410", "Piper Navajo").contains(s.getAvionSolicitado())) return "El avión solicitado no es válido";
        if (longitudMayor(s.getTipoMercancia(), 100)) return "El tipo de mercancía admite hasta 100 caracteres";
        if (longitudMayor(s.getMotivoViaje(), 200)) return "El motivo del viaje admite hasta 200 caracteres";
        if (bytesMayor(s.getObservaciones(), 65535) || bytesMayor(s.getDescripcionCarga(), 65535)) {
            return "Las observaciones y la descripción de carga no pueden superar el límite de texto permitido";
        }
        if (s.getPasajerosEstimados() != null && s.getPasajerosEstimados() < 1) return "Los pasajeros estimados deben ser al menos 1";
        return null;
    }

    private boolean fechaValida(String valor) {
        try {
            if (!valor.matches("\\d{4}-\\d{2}-\\d{2}")) return false;
            LocalDate.parse(valor);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean longitudMayor(String valor, int maximo) {
        return valor != null && valor.length() > maximo;
    }

    private boolean bytesMayor(String valor, int maximo) {
        return valor != null && valor.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > maximo;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
