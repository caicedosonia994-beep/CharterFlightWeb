package com.charterflight.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.charterflight.conexion.Conexion;
import com.charterflight.modelo.Reserva;

/**
 * Clase DAO (Data Access Object) para la entidad Reserva.
 * Proporciona operaciones CRUD (insertar, listar, buscar, actualizar, eliminar)
 * utilizando PreparedStatement para prevenir inyección SQL.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class ReservaDAO {

    private static final String SQL_INSERT = "INSERT INTO reservas (id_cliente, id_vuelo, pasajeros, fecha_reserva, estado) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT id_reserva, id_cliente, id_vuelo, pasajeros, fecha_reserva, estado FROM reservas ORDER BY id_reserva ASC";
    private static final String SQL_SELECT_BY_ID = "SELECT id_reserva, id_cliente, id_vuelo, pasajeros, fecha_reserva, estado FROM reservas WHERE id_reserva = ?";
    private static final String SQL_UPDATE = "UPDATE reservas SET id_cliente = ?, id_vuelo = ?, pasajeros = ?, fecha_reserva = ?, estado = ? WHERE id_reserva = ?";
    private static final String SQL_DELETE = "DELETE FROM reservas WHERE id_reserva = ?";
    private static final String SQL_CHECK_CLIENTE = "SELECT COUNT(*) FROM clientes WHERE id_cliente = ?";
    private static final String SQL_CHECK_VUELO = "SELECT COUNT(*) FROM vuelos WHERE id_vuelo = ?";

    /**
     * Inserta una nueva reserva en la base de datos.
     *
     * @param reserva el objeto Reserva a insertar
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertar(Reserva reserva) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setInt(1, reserva.getIdCliente());
            ps.setInt(2, reserva.getIdVuelo());
            ps.setInt(3, reserva.getPasajeros());
            ps.setString(4, reserva.getFechaReserva());
            ps.setString(5, reserva.getEstado());
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
     * Lista todas las reservas ordenadas por ID.
     *
     * @return lista de reservas (vacía si no hay registros)
     */
    public List<Reserva> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Reserva> reservas = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapearReserva(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return reservas;
    }

    /**
     * Busca una reserva por su ID.
     *
     * @param idReserva el identificador de la reserva
     * @return la Reserva encontrada, o null si no existe
     */
    public Reserva buscarPorId(int idReserva) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Reserva reserva = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, idReserva);
            rs = ps.executeQuery();
            if (rs.next()) {
                reserva = mapearReserva(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return reserva;
    }

    /**
     * Actualiza los datos de una reserva existente.
     *
     * @param reserva el objeto Reserva con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizar(Reserva reserva) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setInt(1, reserva.getIdCliente());
            ps.setInt(2, reserva.getIdVuelo());
            ps.setInt(3, reserva.getPasajeros());
            ps.setString(4, reserva.getFechaReserva());
            ps.setString(5, reserva.getEstado());
            ps.setInt(6, reserva.getIdReserva());
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
     * Elimina una reserva por su ID.
     *
     * @param idReserva el identificador de la reserva a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int idReserva) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, idReserva);
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
     * Verifica si existe un cliente con el ID especificado.
     *
     * @param idCliente el ID del cliente a verificar
     * @return true si el cliente existe, false en caso contrario
     */
    public boolean existeCliente(int idCliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_CLIENTE);
            ps.setInt(1, idCliente);
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
     * Verifica si existe un vuelo con el ID especificado.
     *
     * @param idVuelo el ID del vuelo a verificar
     * @return true si el vuelo existe, false en caso contrario
     */
    public boolean existeVuelo(int idVuelo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_VUELO);
            ps.setInt(1, idVuelo);
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
     * Mapea un ResultSet a un objeto Reserva.
     *
     * @param rs el ResultSet posicionado en una fila válida
     * @return un objeto Reserva con los datos de la fila
     * @throws SQLException si ocurre un error al leer las columnas
     */
    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setIdCliente(rs.getInt("id_cliente"));
        reserva.setIdVuelo(rs.getInt("id_vuelo"));
        reserva.setPasajeros(rs.getInt("pasajeros"));
        reserva.setFechaReserva(rs.getString("fecha_reserva"));
        reserva.setEstado(rs.getString("estado"));
        return reserva;
    }
}