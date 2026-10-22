package com.charterflight.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.charterflight.conexion.Conexion;

public class ReportesDAO {
    public Map<String, Object> consultar(String tipo, Map<String, String> filtros) throws SQLException {
        List<Map<String, Object>> filas;
        Map<String, Object> resumen = new LinkedHashMap<>();
        List<Map<String, String>> columnas;
        switch (tipo) {
            case "clientes":
                filas = consultarClientes();
                resumen.put("totalClientes", filas.size());
                columnas = columnas("idCliente:ID cliente", "nombre:Nombre", "apellido:Apellido", "correo:Correo");
                break;
            case "vuelos":
                filas = consultarVuelos(filtros);
                resumen.put("totalVuelos", filas.size());
                resumen.put("totalReservas", filas.stream().mapToInt(f -> ((Number) f.get("reservas")).intValue()).sum());
                resumen.put("pasajerosReservados", filas.stream().mapToInt(f -> ((Number) f.get("pasajerosReservados")).intValue()).sum());
                columnas = columnas("idVuelo:ID vuelo", "codigo:Código", "origen:Origen", "destino:Destino", "fecha:Fecha", "hora:Hora", "tipo:Tipo de vuelo", "avionAsignado:Avión asignado", "capacidad:Capacidad", "reservas:Reservas", "pasajerosReservados:Pasajeros reservados", "estado:Estado");
                break;
            case "solicitudes":
                filas = consultarSolicitudes(filtros);
                resumen.put("totalSolicitudes", filas.size());
                resumen.put("pasajeros", filas.stream().filter(f -> "Pasajeros".equals(f.get("tipo"))).count());
                resumen.put("carga", filas.stream().filter(f -> "Carga".equals(f.get("tipo"))).count());
                columnas = columnas("idSolicitud:ID solicitud", "tipo:Modalidad", "tipoServicio:Servicio", "origen:Origen", "destino:Destino", "fechaReq:Fecha requerida", "avionSolicitado:Avión solicitado", "estado:Estado");
                break;
            case "financiero":
                filas = consultarFinanciero(filtros, resumen);
                columnas = columnas("idMovimiento:ID movimiento", "idOperacion:ID operación", "tipoOperacion:Tipo de operación", "referenciaOperacion:Vuelo o solicitud", "origen:Origen", "destino:Destino", "fechaServicio:Fecha del servicio", "avionSolicitado:Avión solicitado", "valorTotal:Valor acordado", "monto:Monto del movimiento", "totalPagadoActual:Total recibido", "saldoPendiente:Saldo pendiente", "fechaPago:Fecha de pago", "medioPago:Medio de pago", "estado:Estado del movimiento");
                break;
            default:
                throw new IllegalArgumentException("Tipo de reporte no válido.");
        }
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", tipo);
        resultado.put("titulo", titulo(tipo));
        resultado.put("generado", new Timestamp(System.currentTimeMillis()));
        resultado.put("filtros", filtros);
        resultado.put("columnas", columnas);
        resultado.put("resumen", resumen);
        resultado.put("filas", filas);
        return resultado;
    }

    private List<Map<String, Object>> consultarClientes() throws SQLException {
        return query("SELECT id_cliente AS idCliente, nombre, apellido, correo FROM clientes ORDER BY apellido, nombre", List.of());
    }

    private List<Map<String, Object>> consultarVuelos(Map<String, String> filtros) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT v.id_vuelo AS idVuelo, v.codigo, v.origen, v.destino, v.fecha, v.hora, v.tipo, NULL AS avionAsignado, v.capacidad, v.estado, COUNT(r.id_reserva) AS reservas, COALESCE(SUM(r.pasajeros), 0) AS pasajerosReservados FROM vuelos v LEFT JOIN reservas r ON r.id_vuelo = v.id_vuelo WHERE 1=1");
        List<Object> params = new ArrayList<>();
        addDateFilters(sql, params, "v.fecha", filtros);
        if (present(filtros.get("estado"))) { sql.append(" AND v.estado = ?"); params.add(filtros.get("estado")); }
        sql.append(" GROUP BY v.id_vuelo ORDER BY v.fecha DESC, v.hora DESC");
        return query(sql.toString(), params);
    }

    private List<Map<String, Object>> consultarSolicitudes(Map<String, String> filtros) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id_solicitud AS idSolicitud, tipo, tipo_servicio AS tipoServicio, origen, destino, fecha_req AS fechaReq, avion_solicitado AS avionSolicitado, estado FROM solicitudes_especiales WHERE 1=1");
        List<Object> params = new ArrayList<>();
        addDateFilters(sql, params, "fecha_req", filtros);
        if (present(filtros.get("estado"))) { sql.append(" AND estado = ?"); params.add(filtros.get("estado")); }
        if (present(filtros.get("tipoServicio"))) { sql.append(" AND tipo_servicio = ?"); params.add(filtros.get("tipoServicio")); }
        if (present(filtros.get("avion"))) { sql.append(" AND avion_solicitado = ?"); params.add(filtros.get("avion")); }
        sql.append(" ORDER BY fecha_req DESC, id_solicitud DESC");
        return query(sql.toString(), params);
    }

    private List<Map<String, Object>> consultarFinanciero(Map<String, String> filtros, Map<String, Object> resumen) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT m.id_movimiento AS idMovimiento, op.id_operacion AS idOperacion, op.tipo_operacion AS tipoOperacion, op.valor_total AS valorTotal, m.monto, m.fecha_pago AS fechaPago, m.medio_pago AS medioPago, m.estado, m.referencia_transaccion AS referenciaTransaccion, COALESCE(v.codigo, CONCAT('Solicitud #', s.id_solicitud)) AS referenciaOperacion, COALESCE(v.origen, s.origen) AS origen, COALESCE(v.destino, s.destino) AS destino, COALESCE(v.fecha, s.fecha_req) AS fechaServicio, s.avion_solicitado AS avionSolicitado, (SELECT COALESCE(SUM(mc.monto), 0) FROM movimientos_pago mc WHERE mc.id_operacion = op.id_operacion AND mc.estado='Confirmado') AS totalPagadoActual FROM movimientos_pago m JOIN operaciones_cobrables op ON op.id_operacion = m.id_operacion LEFT JOIN vuelos v ON v.id_vuelo = op.id_vuelo LEFT JOIN solicitudes_especiales s ON s.id_solicitud = op.id_solicitud WHERE 1=1");
        List<Object> params = new ArrayList<>();
        addDateFilters(sql, params, "DATE(m.fecha_pago)", filtros);
        if (present(filtros.get("estado"))) { sql.append(" AND m.estado = ?"); params.add(filtros.get("estado")); }
        if (present(filtros.get("medioPago"))) { sql.append(" AND m.medio_pago = ?"); params.add(filtros.get("medioPago")); }
        if (present(filtros.get("tipoOperacion"))) { sql.append(" AND op.tipo_operacion = ?"); params.add(filtros.get("tipoOperacion")); }
        if (present(filtros.get("tipoServicio"))) { sql.append(" AND s.tipo_servicio = ?"); params.add(filtros.get("tipoServicio")); }
        if (present(filtros.get("avion"))) { sql.append(" AND s.avion_solicitado = ?"); params.add(filtros.get("avion")); }
        sql.append(" ORDER BY m.fecha_pago DESC, m.id_movimiento DESC");
        List<Map<String, Object>> filas = query(sql.toString(), params);
        StringBuilder legacySql = new StringBuilder("SELECT CONCAT('PAG-', p.id_pago) AS idMovimiento, NULL AS idOperacion, 'VUELO_PROGRAMADO' AS tipoOperacion, NULL AS valorTotal, p.monto, p.fecha_pago AS fechaPago, NULL AS medioPago, p.estado, p.numero_factura AS referenciaTransaccion, CONCAT(v.codigo, ' / Factura ', p.numero_factura) AS referenciaOperacion, v.origen, v.destino, v.fecha AS fechaServicio, NULL AS avionSolicitado, CASE WHEN p.estado='Pagado' THEN p.monto ELSE 0 END AS totalPagadoActual FROM pagos p JOIN reservas r ON r.id_reserva=p.id_reserva JOIN vuelos v ON v.id_vuelo=p.id_vuelo WHERE 1=1");
        List<Object> legacyParams = new ArrayList<>();
        addDateFilters(legacySql, legacyParams, "p.fecha_pago", filtros);
        if (present(filtros.get("estado"))) { legacySql.append(" AND p.estado = ?"); legacyParams.add(filtros.get("estado")); }
        if (present(filtros.get("medioPago")) || present(filtros.get("tipoServicio")) || present(filtros.get("avion"))
                || (present(filtros.get("tipoOperacion")) && !"VUELO_PROGRAMADO".equals(filtros.get("tipoOperacion")))) {
            legacySql.append(" AND 1=0");
        }
        filas.addAll(query(legacySql.toString(), legacyParams));
        filas.sort((a, b) -> String.valueOf(b.get("fechaPago")).compareTo(String.valueOf(a.get("fechaPago"))));
        BigDecimal recibidoPeriodo = BigDecimal.ZERO;
        BigDecimal valorAcordado = BigDecimal.ZERO;
        BigDecimal saldoPendiente = BigDecimal.ZERO;
        boolean hayTotalesRegistrados = false;
        Map<String, BigDecimal[]> operaciones = new LinkedHashMap<>();
        Map<String, BigDecimal> porMedio = new LinkedHashMap<>();
        long confirmados = 0;
        for (Map<String, Object> fila : filas) {
            Object idOperacion = fila.get("idOperacion");
            BigDecimal total = (BigDecimal) fila.get("valorTotal");
            BigDecimal actual = (BigDecimal) fila.get("totalPagadoActual");
            BigDecimal monto = (BigDecimal) fila.get("monto");
            if ("Confirmado".equals(fila.get("estado")) || "Pagado".equals(fila.get("estado"))) {
                recibidoPeriodo = recibidoPeriodo.add(monto);
                confirmados++;
                String medio = (String) fila.get("medioPago");
                porMedio.merge(medio == null ? "No registrado" : medio, monto, BigDecimal::add);
            }
            String claveOperacion = idOperacion == null ? "LEGACY-" + fila.get("idMovimiento") : "OP-" + idOperacion;
            if (!operaciones.containsKey(claveOperacion)) operaciones.put(claveOperacion, new BigDecimal[] { total, actual });
            fila.put("saldoPendiente", total == null ? null : total.subtract(actual).max(BigDecimal.ZERO));
            fila.put("totalPagadoActual", actual);
        }
        for (BigDecimal[] valores : operaciones.values()) {
            if (valores[0] != null) {
                hayTotalesRegistrados = true;
                valorAcordado = valorAcordado.add(valores[0]);
                saldoPendiente = saldoPendiente.add(valores[0].subtract(valores[1]).max(BigDecimal.ZERO));
            }
        }
        resumen.put("cantidadMovimientos", filas.size());
        resumen.put("movimientosConfirmados", confirmados);
        resumen.put("operaciones", operaciones.keySet().stream().filter(k -> !k.startsWith("LEGACY-")).count());
        resumen.put("pagosAnteriores", filas.stream().filter(f -> f.get("idMovimiento").toString().startsWith("PAG-")).count());
        resumen.put("valorAcordado", hayTotalesRegistrados ? valorAcordado : null);
        resumen.put("recibidoEnPeriodo", recibidoPeriodo);
        resumen.put("saldoPendienteActual", hayTotalesRegistrados ? saldoPendiente : null);
        resumen.put("porMedioPago", porMedio);
        return filas;
    }

    private List<Map<String, Object>> query(String sql, List<Object> parametros) throws SQLException {
        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.size(); i++) ps.setObject(i + 1, parametros.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                List<Map<String, Object>> filas = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) fila.put(meta.getColumnLabel(i), rs.getObject(i));
                    filas.add(fila);
                }
                return filas;
            }
        }
    }

    private void addDateFilters(StringBuilder sql, List<Object> params, String columna, Map<String, String> filtros) {
        if (present(filtros.get("fechaInicio"))) { sql.append(" AND ").append(columna).append(" >= ?"); params.add(filtros.get("fechaInicio")); }
        if (present(filtros.get("fechaFin"))) { sql.append(" AND ").append(columna).append(" <= ?"); params.add(filtros.get("fechaFin")); }
    }

    private List<Map<String, String>> columnas(String... definiciones) {
        List<Map<String, String>> resultado = new ArrayList<>();
        for (String definicion : definiciones) {
            String[] partes = definicion.split(":", 2);
            Map<String, String> columna = new LinkedHashMap<>();
            columna.put("clave", partes[0]);
            columna.put("titulo", partes[1]);
            resultado.add(columna);
        }
        return resultado;
    }

    private String titulo(String tipo) {
        switch (tipo) {
            case "clientes": return "Reporte de clientes";
            case "vuelos": return "Reporte de vuelos programados";
            case "solicitudes": return "Reporte de solicitudes especiales";
            default: return "Reporte financiero";
        }
    }

    private boolean present(String valor) { return valor != null && !valor.trim().isEmpty(); }
}
