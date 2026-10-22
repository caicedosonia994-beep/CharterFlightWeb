package com.charterflight.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.charterflight.conexion.Conexion;

public class OperacionCobrableDAO {
    private static final String SELECT_OPERACION = "SELECT op.id_operacion AS idOperacion, op.tipo_operacion AS tipoOperacion, op.id_vuelo AS idVuelo, op.id_solicitud AS idSolicitud, op.valor_total AS valorTotal, op.fecha_creacion AS fechaCreacion, COALESCE(v.codigo, CONCAT('Solicitud #', s.id_solicitud)) AS referenciaOperacion, COALESCE(v.origen, s.origen) AS origen, COALESCE(v.destino, s.destino) AS destino, COALESCE(v.fecha, s.fecha_req) AS fechaServicio, COALESCE(s.tipo_servicio, 'Vuelo programado') AS tipoServicio, s.avion_solicitado AS avionSolicitado, s.nombre_contacto AS contacto, (SELECT COALESCE(SUM(m.monto), 0) FROM movimientos_pago m WHERE m.id_operacion = op.id_operacion AND m.estado = 'Confirmado') AS totalPagado, (SELECT COUNT(*) FROM movimientos_pago m WHERE m.id_operacion = op.id_operacion) AS cantidadMovimientos, (SELECT COUNT(*) FROM movimientos_pago m WHERE m.id_operacion = op.id_operacion AND m.estado = 'Rechazado') AS cantidadRechazados FROM operaciones_cobrables op LEFT JOIN vuelos v ON v.id_vuelo = op.id_vuelo LEFT JOIN solicitudes_especiales s ON s.id_solicitud = op.id_solicitud";

    public List<Map<String, Object>> listar() throws SQLException {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_OPERACION + " ORDER BY op.id_operacion DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) resultado.add(mapearOperacion(rs));
        }
        return resultado;
    }

    public Map<String, Object> buscar(int id, boolean incluirMovimientos) throws SQLException {
        Map<String, Object> resultado;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_OPERACION + " WHERE op.id_operacion = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                resultado = mapearOperacion(rs);
            }
        }
        if (incluirMovimientos) {
            try (Connection conn = Conexion.getConnection()) { resultado.put("movimientos", listarMovimientos(conn, id)); }
        }
        return resultado;
    }

    public int crear(String tipo, Integer idVuelo, Integer idSolicitud, BigDecimal valorTotal) throws SQLException {
        String sql = "INSERT INTO operaciones_cobrables (tipo_operacion, id_vuelo, id_solicitud, valor_total) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tipo);
            if (idVuelo == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, idVuelo);
            if (idSolicitud == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, idSolicitud);
            if (valorTotal == null) ps.setNull(4, Types.DECIMAL); else ps.setBigDecimal(4, valorTotal);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No se obtuvo el identificador de la operación");
                return keys.getInt(1);
            }
        }
    }

    public boolean actualizarTotal(int id, BigDecimal valorTotal) throws SQLException {
        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement lock = conn.prepareStatement("SELECT id_operacion FROM operaciones_cobrables WHERE id_operacion = ? FOR UPDATE")) {
                    lock.setInt(1, id);
                    try (ResultSet rs = lock.executeQuery()) { if (!rs.next()) { conn.rollback(); return false; } }
                }
                BigDecimal pagado = BigDecimal.ZERO;
                try (PreparedStatement sum = conn.prepareStatement("SELECT COALESCE(SUM(monto), 0) FROM movimientos_pago WHERE id_operacion = ? AND estado = 'Confirmado'")) {
                    sum.setInt(1, id);
                    try (ResultSet rs = sum.executeQuery()) { if (rs.next()) pagado = rs.getBigDecimal(1); }
                }
                if (valorTotal != null && valorTotal.compareTo(pagado) < 0) {
                    throw new IllegalArgumentException("El valor total no puede ser menor que los pagos confirmados.");
                }
                if (valorTotal == null && pagado.signum() > 0) {
                    throw new IllegalArgumentException("No se puede quitar el valor total mientras existan pagos confirmados.");
                }
                try (PreparedStatement update = conn.prepareStatement("UPDATE operaciones_cobrables SET valor_total = ? WHERE id_operacion = ?")) {
                    if (valorTotal == null) update.setNull(1, Types.DECIMAL); else update.setBigDecimal(1, valorTotal);
                    update.setInt(2, id);
                    update.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException) throw (SQLException) e;
                if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
                throw new SQLException("No fue posible actualizar el valor total", e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public Map<String, Object> registrarMovimiento(int idOperacion, BigDecimal monto, Timestamp fecha,
            String medio, String referencia, String estado, String idempotencia) throws SQLException {
        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                BigDecimal total;
                try (PreparedStatement lock = conn.prepareStatement("SELECT valor_total FROM operaciones_cobrables WHERE id_operacion = ? FOR UPDATE")) {
                    lock.setInt(1, idOperacion);
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) { conn.rollback(); return null; }
                        total = rs.getBigDecimal(1);
                    }
                }
                Map<String, Object> anterior = buscarMovimientoPorClave(conn, idempotencia);
                if (anterior != null) {
                    boolean mismo = ((Number) anterior.get("idOperacion")).intValue() == idOperacion
                            && ((BigDecimal) anterior.get("monto")).compareTo(monto) == 0
                            && fecha.equals(anterior.get("fechaPago"))
                            && medio.equals(anterior.get("medioPago"))
                            && java.util.Objects.equals(referencia, anterior.get("referenciaTransaccion"))
                            && estado.equals(anterior.get("estado"));
                    conn.rollback();
                    if (!mismo) throw new IllegalArgumentException("La clave de idempotencia ya se usó con otros datos.");
                    return anterior;
                }
                if ("Confirmado".equals(estado)) {
                    if (total == null || total.signum() <= 0) throw new IllegalArgumentException("Registra primero un valor total acordado mayor que cero.");
                    BigDecimal pagado = BigDecimal.ZERO;
                    try (PreparedStatement sum = conn.prepareStatement("SELECT COALESCE(SUM(monto), 0) FROM movimientos_pago WHERE id_operacion = ? AND estado = 'Confirmado'")) {
                        sum.setInt(1, idOperacion);
                        try (ResultSet rs = sum.executeQuery()) { if (rs.next()) pagado = rs.getBigDecimal(1); }
                    }
                    if (monto.compareTo(total.subtract(pagado)) > 0) throw new IllegalArgumentException("El abono supera el saldo pendiente.");
                }
                String sql = "INSERT INTO movimientos_pago (id_operacion, monto, fecha_pago, medio_pago, referencia_transaccion, estado, clave_idempotencia) VALUES (?, ?, ?, ?, ?, ?, ?)";
                int id;
                try (PreparedStatement insert = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insert.setInt(1, idOperacion);
                    insert.setBigDecimal(2, monto);
                    insert.setTimestamp(3, fecha);
                    insert.setString(4, medio);
                    insert.setString(5, referencia);
                    insert.setString(6, estado);
                    insert.setString(7, idempotencia);
                    insert.executeUpdate();
                    try (ResultSet keys = insert.getGeneratedKeys()) { if (!keys.next()) throw new SQLException("No se obtuvo el identificador del movimiento"); id = keys.getInt(1); }
                }
                conn.commit();
                try (PreparedStatement select = conn.prepareStatement("SELECT id_movimiento AS idMovimiento, id_operacion AS idOperacion, monto, fecha_pago AS fechaPago, medio_pago AS medioPago, referencia_transaccion AS referenciaTransaccion, estado FROM movimientos_pago WHERE id_movimiento = ?")) {
                    select.setInt(1, id);
                    try (ResultSet rs = select.executeQuery()) { rs.next(); return fila(rs); }
                }
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException) throw (SQLException) e;
                if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
                throw new SQLException("No fue posible registrar el movimiento", e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private List<Map<String, Object>> listarMovimientos(Connection conn, int id) throws SQLException {
        List<Map<String, Object>> movimientos = new ArrayList<>();
        String sql = "SELECT id_movimiento AS idMovimiento, id_operacion AS idOperacion, monto, fecha_pago AS fechaPago, medio_pago AS medioPago, referencia_transaccion AS referenciaTransaccion, estado FROM movimientos_pago WHERE id_operacion = ? ORDER BY fecha_pago DESC, id_movimiento DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) movimientos.add(fila(rs)); }
        }
        return movimientos;
    }

    private Map<String, Object> buscarMovimientoPorClave(Connection conn, String clave) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_movimiento AS idMovimiento, id_operacion AS idOperacion, monto, fecha_pago AS fechaPago, medio_pago AS medioPago, referencia_transaccion AS referenciaTransaccion, estado FROM movimientos_pago WHERE clave_idempotencia = ?")) {
            ps.setString(1, clave);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? fila(rs) : null; }
        }
    }

    private Map<String, Object> fila(ResultSet rs) throws SQLException {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("idMovimiento", rs.getInt("idMovimiento"));
        fila.put("idOperacion", rs.getInt("idOperacion"));
        fila.put("monto", rs.getBigDecimal("monto"));
        fila.put("fechaPago", rs.getTimestamp("fechaPago"));
        fila.put("medioPago", rs.getString("medioPago"));
        fila.put("referenciaTransaccion", rs.getString("referenciaTransaccion"));
        fila.put("estado", rs.getString("estado"));
        return fila;
    }

    private Map<String, Object> mapearOperacion(ResultSet rs) throws SQLException {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("idOperacion", rs.getInt("idOperacion"));
        fila.put("tipoOperacion", rs.getString("tipoOperacion"));
        fila.put("idVuelo", (Integer) rs.getObject("idVuelo"));
        fila.put("idSolicitud", (Integer) rs.getObject("idSolicitud"));
        BigDecimal total = rs.getBigDecimal("valorTotal");
        BigDecimal pagado = rs.getBigDecimal("totalPagado");
        fila.put("valorTotal", total);
        fila.put("totalPagado", pagado);
        fila.put("saldoPendiente", total == null ? null : total.subtract(pagado).max(BigDecimal.ZERO));
        int rechazados = rs.getInt("cantidadRechazados");
        fila.put("estadoPago", total == null || total.signum() <= 0 ? (rechazados > 0 && pagado.signum() == 0 ? "Rechazado" : "Pendiente") : pagado.compareTo(total) >= 0 ? "Pagado" : pagado.signum() > 0 ? "Parcial" : rechazados > 0 ? "Rechazado" : "Pendiente");
        fila.put("cantidadMovimientos", rs.getInt("cantidadMovimientos"));
        fila.put("referenciaOperacion", rs.getString("referenciaOperacion"));
        fila.put("origen", rs.getString("origen"));
        fila.put("destino", rs.getString("destino"));
        fila.put("fechaServicio", rs.getString("fechaServicio"));
        fila.put("tipoServicio", rs.getString("tipoServicio"));
        fila.put("avionSolicitado", rs.getString("avionSolicitado"));
        fila.put("contacto", rs.getString("contacto"));
        fila.put("fechaCreacion", rs.getTimestamp("fechaCreacion"));
        return fila;
    }
}
