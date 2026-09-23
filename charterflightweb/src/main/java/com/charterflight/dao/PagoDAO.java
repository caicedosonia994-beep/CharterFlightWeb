package com.charterflight.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.charterflight.conexion.Conexion;
import com.charterflight.modelo.Pago;

/**
 * Clase DAO (Data Access Object) para la entidad Pago.
 * Proporciona operaciones CRUD (insertar, listar, buscar, actualizar, eliminar)
 * utilizando PreparedStatement para prevenir inyección SQL.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class PagoDAO {

    private static final String SQL_INSERT = "INSERT INTO pagos (id_reserva, id_cliente, id_vuelo, monto, estado, fecha_pago, numero_factura) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT id_pago, id_reserva, id_cliente, id_vuelo, monto, estado, fecha_pago, numero_factura FROM pagos ORDER BY id_pago ASC";
    private static final String SQL_SELECT_BY_ID = "SELECT id_pago, id_reserva, id_cliente, id_vuelo, monto, estado, fecha_pago, numero_factura FROM pagos WHERE id_pago = ?";
    private static final String SQL_UPDATE = "UPDATE pagos SET id_reserva = ?, id_cliente = ?, id_vuelo = ?, monto = ?, estado = ?, fecha_pago = ?, numero_factura = ? WHERE id_pago = ?";
    private static final String SQL_DELETE = "DELETE FROM pagos WHERE id_pago = ?";
    private static final String SQL_CHECK_NUMERO_FACTURA = "SELECT COUNT(*) FROM pagos WHERE numero_factura = ?";
    private static final String SQL_CHECK_NUMERO_FACTURA_EXCEPTO = "SELECT COUNT(*) FROM pagos WHERE numero_factura = ? AND id_pago != ?";

    /**
     * Inserta un nuevo pago en la base de datos.
     *
     * @param pago el objeto Pago a insertar
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertar(Pago pago) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setInt(1, pago.getIdReserva());
            ps.setInt(2, pago.getIdCliente());
            ps.setInt(3, pago.getIdVuelo());
            ps.setDouble(4, pago.getMonto());
            ps.setString(5, pago.getEstado());
            ps.setString(6, pago.getFechaPago());
            ps.setString(7, pago.getNumeroFactura());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Conexion.close(conn, ps, null);
        }
    }

    /**
     * Lista todos los pagos ordenados por ID.
     *
     * @return lista de pagos (vacía si no hay registros)
     */
    public List<Pago> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Pago> pagos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                pagos.add(mapearPago(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return pagos;
    }

    /**
     * Busca un pago por su ID.
     *
     * @param idPago el identificador del pago
     * @return el Pago encontrado, o null si no existe
     */
    public Pago buscarPorId(int idPago) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Pago pago = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, idPago);
            rs = ps.executeQuery();
            if (rs.next()) {
                pago = mapearPago(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return pago;
    }

    /**
     * Actualiza los datos de un pago existente.
     *
     * @param pago el objeto Pago con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizar(Pago pago) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setInt(1, pago.getIdReserva());
            ps.setInt(2, pago.getIdCliente());
            ps.setInt(3, pago.getIdVuelo());
            ps.setDouble(4, pago.getMonto());
            ps.setString(5, pago.getEstado());
            ps.setString(6, pago.getFechaPago());
            ps.setString(7, pago.getNumeroFactura());
            ps.setInt(8, pago.getIdPago());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Conexion.close(conn, ps, null);
        }
    }

    /**
     * Elimina un pago por su ID.
     *
     * @param idPago el identificador del pago a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int idPago) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, idPago);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Conexion.close(conn, ps, null);
        }
    }

    /**
     * Verifica si ya existe un pago con el número de factura especificado.
     *
     * @param numeroFactura el número de factura a verificar
     * @return true si el número de factura ya está registrado, false en caso contrario
     */
    public boolean existeNumeroFactura(String numeroFactura) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_NUMERO_FACTURA);
            ps.setString(1, numeroFactura);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return false;
    }

    /**
     * Verifica si ya existe un pago con el número de factura especificado,
     * excluyendo al pago con el ID indicado (para validación en edición).
     *
     * @param numeroFactura el número de factura a verificar
     * @param idPago el ID del pago a excluir
     * @return true si el número de factura ya está registrado por otro pago, false en caso contrario
     */
    public boolean existeNumeroFacturaExcepto(String numeroFactura, int idPago) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_NUMERO_FACTURA_EXCEPTO);
            ps.setString(1, numeroFactura);
            ps.setInt(2, idPago);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Pago.
     *
     * @param rs el ResultSet posicionado en una fila válida
     * @return un objeto Pago con los datos de la fila
     * @throws SQLException si ocurre un error al leer las columnas
     */
    private Pago mapearPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setIdPago(rs.getInt("id_pago"));
        pago.setIdReserva(rs.getInt("id_reserva"));
        pago.setIdCliente(rs.getInt("id_cliente"));
        pago.setIdVuelo(rs.getInt("id_vuelo"));
        pago.setMonto(rs.getDouble("monto"));
        pago.setEstado(rs.getString("estado"));
        pago.setFechaPago(rs.getString("fecha_pago"));
        pago.setNumeroFactura(rs.getString("numero_factura"));
        return pago;
    }
}