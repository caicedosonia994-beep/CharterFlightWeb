package com.charterflight.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.charterflight.dao.OperacionCobrableDAO;
import com.charterflight.dao.ReportesDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class BackofficeApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> MEDIOS = Set.of("Transferencia bancaria", "Tarjeta débito", "Tarjeta de crédito", "Efectivo", "Nequi", "Daviplata", "PSE");
    private final ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final OperacionCobrableDAO operaciones = new OperacionCobrableDAO();
    private final ReportesDAO reportes = new ReportesDAO();

    private void json(HttpServletResponse response, int status, Object cuerpo) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), cuerpo);
    }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            if (request.getRequestURI().contains("/api/reportes/")) {
                atenderReporte(request, response);
                return;
            }
            String path = request.getPathInfo();
            if (path == null || "/".equals(path)) { json(response, 200, operaciones.listar()); return; }
            Integer id = idDe(path);
            if (id == null || path.split("/").length != 2) { json(response, 400, Map.of("error", "ID de operación inválido")); return; }
            Map<String, Object> resultado = operaciones.buscar(id, true);
            if (resultado == null) json(response, 404, Map.of("error", "Operación de pago no encontrada"));
            else json(response, 200, resultado);
        } catch (IllegalArgumentException e) { json(response, 400, Map.of("error", e.getMessage())); }
        catch (SQLException e) { e.printStackTrace(); json(response, 500, Map.of("error", "No fue posible consultar la información")); }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            Map<String, Object> cuerpo = leerCuerpo(request);
            String path = request.getPathInfo();
            if (path == null || "/".equals(path)) {
                String tipo = texto(cuerpo, "tipoOperacion", true);
                Integer idVuelo = enteroOpcional(cuerpo, "idVuelo");
                Integer idSolicitud = enteroOpcional(cuerpo, "idSolicitud");
                BigDecimal total = decimalOpcional(cuerpo, "valorTotal");
                if (total != null) validarMonto(total, true);
                if ("VUELO_PROGRAMADO".equals(tipo) && (idVuelo == null || idSolicitud != null)
                        || "SOLICITUD_ESPECIAL".equals(tipo) && (idSolicitud == null || idVuelo != null)
                        || !Set.of("VUELO_PROGRAMADO", "SOLICITUD_ESPECIAL").contains(tipo)) {
                    json(response, 400, Map.of("error", "Selecciona un vuelo programado o una solicitud especial válida.")); return;
                }
                int id = operaciones.crear(tipo, idVuelo, idSolicitud, total);
                json(response, 201, operaciones.buscar(id, true));
                return;
            }
            String[] partes = path.split("/");
            Integer id = idDe(path);
            if (partes.length == 3 && "movimientos".equals(partes[2]) && id != null) {
                BigDecimal monto = decimalOpcional(cuerpo, "monto");
                validarMonto(monto, true);
                String medio = texto(cuerpo, "medioPago", true);
                if (!MEDIOS.contains(medio)) { json(response, 400, Map.of("error", "El medio de pago no es válido.")); return; }
                String estado = texto(cuerpo, "estado", true);
                if (!Set.of("Confirmado", "Rechazado").contains(estado)) { json(response, 400, Map.of("error", "El estado del movimiento no es válido.")); return; }
                String referencia = texto(cuerpo, "referenciaTransaccion", false);
                if (referencia != null && referencia.length() > 100) { json(response, 400, Map.of("error", "La referencia admite hasta 100 caracteres.")); return; }
                String clave = texto(cuerpo, "claveIdempotencia", true);
                if (clave.length() > 64) { json(response, 400, Map.of("error", "La clave de idempotencia admite hasta 64 caracteres.")); return; }
                Timestamp fecha = fechaMovimiento(texto(cuerpo, "fechaPago", true));
                Map<String, Object> nuevo = operaciones.registrarMovimiento(id, monto, fecha, medio, referencia, estado, clave);
                if (nuevo == null) { json(response, 404, Map.of("error", "Operación de pago no encontrada")); return; }
                json(response, 201, Map.of("movimiento", nuevo, "operacion", operaciones.buscar(id, true)));
                return;
            }
            json(response, 400, Map.of("error", "Ruta de operación no válida."));
        } catch (IllegalArgumentException e) { json(response, 400, Map.of("error", e.getMessage())); }
        catch (SQLException e) {
            e.printStackTrace();
            int status = e.getSQLState() != null && e.getSQLState().startsWith("23") ? 409 : 500;
            json(response, status, Map.of("error", status == 409 ? "La operación o clave ya existe, o la referencia no es válida." : "No fue posible guardar la información."));
        }
    }

    @Override protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            Integer id = idDe(request.getPathInfo());
            if (id == null || request.getPathInfo().split("/").length != 2) { json(response, 400, Map.of("error", "ID de operación inválido")); return; }
            Map<String, Object> cuerpo = leerCuerpo(request);
            BigDecimal total = decimalOpcional(cuerpo, "valorTotal");
            if (total != null) validarMonto(total, true);
            if (!cuerpo.containsKey("valorTotal")) { json(response, 400, Map.of("error", "Debes enviar valorTotal.")); return; }
            if (!operaciones.actualizarTotal(id, total)) { json(response, 404, Map.of("error", "Operación de pago no encontrada")); return; }
            json(response, 200, operaciones.buscar(id, true));
        } catch (IllegalArgumentException e) { json(response, 400, Map.of("error", e.getMessage())); }
        catch (SQLException e) { e.printStackTrace(); json(response, 500, Map.of("error", "No fue posible actualizar el valor total.")); }
    }

    private void atenderReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String prefijo = request.getContextPath() + "/api/reportes/";
        String ruta = request.getRequestURI().substring(prefijo.length());
        String[] partes = ruta.split("/");
        if (partes.length < 1 || partes.length > 2) { json(response, 404, Map.of("error", "Reporte no encontrado.")); return; }
        String tipo = partes[0];
        try {
            Map<String, String> filtros = filtros(request);
            Map<String, Object> datos = reportes.consultar(tipo, filtros);
            if (partes.length == 1) { json(response, 200, datos); return; }
            String formato = partes[1];
            String nombre = "CharterFlight_" + tipo + "_" + LocalDate.now();
            if ("excel".equals(formato)) {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".xlsx\"");
                escribirExcel(response, datos);
            } else if ("pdf".equals(formato)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + ".pdf\"");
                response.getOutputStream().write(generarPdf(datos));
            } else json(response, 404, Map.of("error", "Formato de exportación no válido."));
        } catch (IllegalArgumentException e) { json(response, 400, Map.of("error", e.getMessage())); }
        catch (SQLException e) { e.printStackTrace(); json(response, 500, Map.of("error", "No fue posible consultar el reporte.")); }
    }

    private Map<String, String> filtros(HttpServletRequest request) {
        Map<String, String> filtros = new LinkedHashMap<>();
        for (String clave : List.of("fechaInicio", "fechaFin", "estado", "tipoServicio", "avion", "medioPago", "tipoOperacion")) {
            String valor = request.getParameter(clave);
            if (valor != null && !valor.isBlank()) filtros.put(clave, valor.trim());
        }
        for (String clave : List.of("fechaInicio", "fechaFin")) {
            String valor = filtros.get(clave);
            if (valor != null) try { LocalDate.parse(valor); } catch (DateTimeParseException e) { throw new IllegalArgumentException("La fecha del filtro debe tener formato AAAA-MM-DD."); }
        }
        if (filtros.containsKey("avion") && !Set.of("Beechcraft 1900D", "LET L-410", "Piper Navajo").contains(filtros.get("avion"))) throw new IllegalArgumentException("El avión del filtro no es válido.");
        return filtros;
    }

    private Map<String, Object> leerCuerpo(HttpServletRequest request) throws IOException {
        try { return mapper.readValue(request.getReader(), mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)); }
        catch (Exception e) { throw new IllegalArgumentException("El cuerpo JSON no es válido."); }
    }

    private String texto(Map<String, Object> mapa, String clave, boolean requerido) {
        Object valor = mapa.get(clave);
        if (valor == null) { if (requerido) throw new IllegalArgumentException("El campo " + clave + " es obligatorio."); return null; }
        if (!(valor instanceof String)) throw new IllegalArgumentException("El campo " + clave + " debe ser texto.");
        String texto = ((String) valor).trim();
        if (requerido && texto.isEmpty()) throw new IllegalArgumentException("El campo " + clave + " es obligatorio.");
        return texto.isEmpty() ? null : texto;
    }

    private Integer enteroOpcional(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        if (valor == null || "".equals(valor)) return null;
        try { int id = new BigDecimal(valor.toString()).intValueExact(); if (id <= 0) throw new ArithmeticException(); return id; }
        catch (Exception e) { throw new IllegalArgumentException("El identificador " + clave + " no es válido."); }
    }

    private BigDecimal decimalOpcional(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        if (valor == null || "".equals(valor)) return null;
        try { return new BigDecimal(valor.toString()); }
        catch (Exception e) { throw new IllegalArgumentException("El campo " + clave + " debe ser un importe numérico."); }
    }

    private void validarMonto(BigDecimal monto, boolean debeSerPositivo) {
        if (monto == null) throw new IllegalArgumentException("El monto es obligatorio.");
        try { monto = monto.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException e) { throw new IllegalArgumentException("El importe admite hasta dos decimales."); }
        if (monto.precision() > 10 || (debeSerPositivo ? monto.signum() <= 0 : monto.signum() < 0)) throw new IllegalArgumentException(debeSerPositivo ? "El monto debe ser mayor que cero y no superar el límite permitido." : "El valor total no puede ser negativo ni superar el límite permitido.");
    }

    private Timestamp fechaMovimiento(String valor) {
        try { return Timestamp.valueOf(LocalDateTime.parse(valor.replace(' ', 'T'))); }
        catch (Exception e) { throw new IllegalArgumentException("La fecha del movimiento no es válida."); }
    }

    private Integer idDe(String path) {
        if (path == null) return null;
        String[] partes = path.split("/");
        if (partes.length < 2) return null;
        try { int id = Integer.parseInt(partes[1]); return id > 0 ? id : null; }
        catch (NumberFormatException e) { return null; }
    }

    @SuppressWarnings("unchecked")
    private void escribirExcel(HttpServletResponse response, Map<String, Object> datos) throws IOException {
        List<Map<String, String>> columnas = (List<Map<String, String>>) datos.get("columnas");
        List<Map<String, Object>> filas = (List<Map<String, Object>>) datos.get("filas");
        try (ZipOutputStream zip = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            agregar(zip, "[Content_Types].xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>");
            agregar(zip, "_rels/.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");
            agregar(zip, "xl/workbook.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"Reporte\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            agregar(zip, "xl/_rels/workbook.xml.rels", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>");
            StringBuilder sheet = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
            int row = 1;
            sheet.append("<row r=\"").append(row).append("\"><c r=\"A1\" t=\"inlineStr\"><is><t>").append(xml(datos.get("titulo").toString())).append("</t></is></c></row>");
            row++;
            sheet.append("<row r=\"").append(row).append("\"><c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>Generado: ").append(xml(datos.get("generado").toString())).append("</t></is></c></row>");
            row++;
            Map<String, String> filtros = (Map<String, String>) datos.get("filtros");
            if (!filtros.isEmpty()) {
                sheet.append("<row r=\"").append(row).append("\"><c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>Filtros aplicados: ").append(xml(filtros.toString())).append("</t></is></c></row>");
                row++;
            }
            Map<String, Object> resumen = (Map<String, Object>) datos.get("resumen");
            for (Map.Entry<String, Object> entrada : resumen.entrySet()) {
                Object valor = entrada.getValue();
                if (valor instanceof Map) {
                    for (Map.Entry<?, ?> detalle : ((Map<?, ?>) valor).entrySet()) {
                        row++;
                        sheet.append("<row r=\"").append(row).append("\">").append(textCell("A" + row, etiquetaResumen(entrada.getKey()) + " - " + detalle.getKey()));
                        Object monto = detalle.getValue();
                        sheet.append(numberCell("B" + row, monto)).append("</row>");
                    }
                } else {
                    row++;
                    sheet.append("<row r=\"").append(row).append("\">").append(textCell("A" + row, etiquetaResumen(entrada.getKey())));
                    sheet.append(numberCell("B" + row, valor)).append("</row>");
                }
            }
            row++;
            sheet.append("<row r=\"").append(row).append("\">");
            for (int c = 0; c < columnas.size(); c++) sheet.append(textCell(columna(c) + row, columnas.get(c).get("titulo")));
            sheet.append("</row>");
            for (Map<String, Object> fila : filas) {
                row++;
                sheet.append("<row r=\"").append(row).append("\">");
                for (int c = 0; c < columnas.size(); c++) {
                    Object valor = fila.get(columnas.get(c).get("clave"));
                    String ref = columna(c) + row;
                    if (valor instanceof Number) sheet.append("<c r=\"").append(ref).append("\"><v>").append(valor.toString()).append("</v></c>");
                    else sheet.append(textCell(ref, valor == null ? "" : valor.toString()));
                }
                sheet.append("</row>");
            }
            sheet.append("</sheetData></worksheet>");
            agregar(zip, "xl/worksheets/sheet1.xml", sheet.toString());
            zip.finish();
        }
    }

    private String textCell(String ref, String valor) { return "<c r=\"" + ref + "\" t=\"inlineStr\"><is><t xml:space=\"preserve\">" + xml(valor) + "</t></is></c>"; }
    private String numberCell(String ref, Object valor) { return valor instanceof Number ? "<c r=\"" + ref + "\"><v>" + valor + "</v></c>" : textCell(ref, valor == null ? "No registrado" : valor.toString()); }
    private String columna(int index) { StringBuilder value = new StringBuilder(); for (int n = index + 1; n > 0; n = (n - 1) / 26) value.insert(0, (char) ('A' + (n - 1) % 26)); return value.toString(); }
    private String xml(String value) { return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;"); }
    private void agregar(ZipOutputStream zip, String nombre, String contenido) throws IOException { zip.putNextEntry(new ZipEntry(nombre)); zip.write(contenido.getBytes(StandardCharsets.UTF_8)); zip.closeEntry(); }

    @SuppressWarnings("unchecked")
    private byte[] generarPdf(Map<String, Object> datos) throws IOException {
        List<Map<String, String>> columnas = (List<Map<String, String>>) datos.get("columnas");
        List<Map<String, Object>> filas = (List<Map<String, Object>>) datos.get("filas");
        List<String> lineas = new ArrayList<>();
        lineas.add(datos.get("titulo").toString());
        lineas.add("Generado: " + datos.get("generado"));
        Map<String, Object> filtros = (Map<String, Object>) datos.get("filtros");
        if (!filtros.isEmpty()) lineas.add("Filtros: " + filtros);
        lineas.add("");
        Map<String, Object> resumen = (Map<String, Object>) datos.get("resumen");
        for (Map.Entry<String, Object> entrada : resumen.entrySet()) {
            if (entrada.getValue() instanceof Map) {
                for (Map.Entry<?, ?> detalle : ((Map<?, ?>) entrada.getValue()).entrySet()) lineas.add(etiquetaResumen(entrada.getKey()) + " - " + detalle.getKey() + ": " + detalle.getValue());
            } else lineas.add(etiquetaResumen(entrada.getKey()) + ": " + valorTexto(entrada.getValue()));
        }
        lineas.add("");
        lineas.add(String.join(" | ", columnas.stream().map(c -> c.get("titulo")).toList()));
        lineas.add("--------------------------------------------------------------------------------");
        for (Map<String, Object> fila : filas) {
            String texto = String.join(" | ", columnas.stream().map(c -> valorTexto(fila.get(c.get("clave")))).toList());
            for (int inicio = 0; inicio < texto.length(); inicio += 140) lineas.add(texto.substring(inicio, Math.min(inicio + 140, texto.length())));
        }
        String titulo = datos.get("titulo").toString();
        String encabezadoTabla = String.join(" | ", columnas.stream().map(c -> c.get("titulo")).toList());
        List<List<String>> paginas = new ArrayList<>();
        int inicio = 0;
        int indicePagina = 0;
        while (inicio < lineas.size()) {
            List<String> pagina = new ArrayList<>();
            if (indicePagina > 0) {
                pagina.add(titulo + " (continuación)");
                pagina.add(encabezadoTabla);
                pagina.add("--------------------------------------------------------------------------------");
            }
            int limite = inicio + (indicePagina == 0 ? 42 : 39);
            pagina.addAll(lineas.subList(inicio, Math.min(limite, lineas.size())));
            paginas.add(pagina);
            inicio = limite;
            indicePagina++;
        }
        if (paginas.isEmpty()) paginas.add(List.of(datos.get("titulo").toString()));
        int cantidadObjetos = 3 + paginas.size() * 2;
        List<byte[]> objetos = new ArrayList<>();
        objetos.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        StringBuilder refs = new StringBuilder();
        for (int i = 0; i < paginas.size(); i++) refs.append(4 + i * 2).append(" 0 R ");
        objetos.add(ascii("<< /Type /Pages /Kids [" + refs + "] /Count " + paginas.size() + " >>"));
        objetos.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        for (int i = 0; i < paginas.size(); i++) {
            int pageId = 4 + i * 2;
            int streamId = pageId + 1;
            byte[] contenido = contenidoPdf(paginas.get(i));
            objetos.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 3 0 R >> >> /Contents " + streamId + " 0 R >>"));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(ascii("<< /Length " + contenido.length + " >>\nstream\n"));
            stream.write(contenido);
            stream.write(ascii("\nendstream"));
            objetos.add(stream.toByteArray());
        }
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write(ascii("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n"));
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < cantidadObjetos; i++) {
            offsets.add(pdf.size());
            pdf.write(ascii((i + 1) + " 0 obj\n"));
            pdf.write(objetos.get(i));
            pdf.write(ascii("\nendobj\n"));
        }
        int xref = pdf.size();
        pdf.write(ascii("xref\n0 " + (cantidadObjetos + 1) + "\n0000000000 65535 f \n"));
        for (Integer offset : offsets) pdf.write(ascii(String.format("%010d 00000 n \n", offset)));
        pdf.write(ascii("trailer\n<< /Size " + (cantidadObjetos + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF"));
        return pdf.toByteArray();
    }

    private byte[] contenidoPdf(List<String> lineas) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int y = 560;
        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            byte[] texto = linea.getBytes(Charset.forName("windows-1252"));
            StringBuilder hex = new StringBuilder();
            for (byte b : texto) hex.append(String.format("%02X", b & 0xff));
            if (i == 0) output.write(ascii("0.788 0.635 0.153 rg BT /F1 13 Tf 28 " + y + " Td <" + hex + "> Tj ET\n0.486 0.137 0.220 RG 2 w 28 540 m 814 540 l S\n"));
            else output.write(ascii("0.12 0.12 0.12 rg BT /F1 7 Tf 28 " + y + " Td <" + hex + "> Tj ET\n"));
            y -= 12;
        }
        return output.toByteArray();
    }
    private String valorTexto(Object valor) { return valor == null ? "No disponible" : valor.toString().replace('\n', ' ').replace('\r', ' '); }
    private String etiquetaResumen(String clave) {
        switch (clave) {
            case "totalClientes": return "Clientes registrados";
            case "totalVuelos": return "Vuelos programados";
            case "totalReservas": return "Reservas registradas";
            case "pasajerosReservados": return "Pasajeros reservados";
            case "totalSolicitudes": return "Solicitudes especiales";
            case "pasajeros": return "Solicitudes de pasajeros";
            case "carga": return "Solicitudes de carga";
            case "cantidadMovimientos": return "Pagos y movimientos consultados";
            case "movimientosConfirmados": return "Pagos confirmados o recibidos";
            case "operaciones": return "Operaciones cobrables";
            case "pagosAnteriores": return "Pagos de reservas existentes";
            case "valorAcordado": return "Valor acordado registrado";
            case "recibidoEnPeriodo": return "Dinero recibido en el período";
            case "saldoPendienteActual": return "Saldo pendiente actual";
            case "porMedioPago": return "Recibido por medio de pago";
            default: return clave;
        }
    }
    private byte[] ascii(String valor) { return valor.getBytes(StandardCharsets.ISO_8859_1); }
}
