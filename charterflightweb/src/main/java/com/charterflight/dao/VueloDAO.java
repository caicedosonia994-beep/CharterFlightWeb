package com.charterflight.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.charterflight.conexion.Conexion;
import com.charterflight.modelo.Vuelo;

/**
 * Clase DAO (Data Access Object) para la entidad Vuelo.
 * Proporciona operaciones CRUD (insertar, listar, buscar, actualizar, eliminar)
 * utilizando PreparedStatement para prevenir inyección SQL.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class VueloDAO {

    private static final String SQL_INSERT = "INSERT INTO vuelos (codigo, origen, destino, fecha, hora, tipo, capacidad, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT id_vuelo, codigo, origen, destino, fecha, hora, tipo, capacidad, estado FROM vuelos ORDER BY id_vuelo ASC";
    private static final String SQL_SELECT_BY_ID = "SELECT id_vuelo, codigo, origen, destino, fecha, hora, tipo, capacidad, estado FROM vuelos WHERE id_vuelo = ?";
    private static final String SQL_UPDATE = "UPDATE vuelos SET codigo = ?, origen = ?, destino = ?, fecha = ?, hora = ?, tipo = ?, capacidad = ?, estado = ? WHERE id_vuelo = ?";
    private static final String SQL_DELETE = "DELETE FROM vuelos WHERE id_vuelo = ?";
    private static final String SQL_CHECK_CODIGO = "SELECT COUNT(*) FROM vuelos WHERE codigo = ?";
    private static final String SQL_CHECK_CODIGO_EXCEPTO = "SELECT COUNT(*) FROM vuelos WHERE codigo = ? AND id_vuelo != ?";

    /**
     * Inserta un nuevo vuelo en la base de datos.
     *
     * @param vuelo el objeto Vuelo a insertar
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertar(Vuelo vuelo) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, vuelo.getCodigo());
            ps.setString(2, vuelo.getOrigen());
            ps.setString(3, vuelo.getDestino());
            ps.setString(4, vuelo.getFecha());
            ps.setString(5, vuelo.getHora());
            ps.setString(6, vuelo.getTipo());
            ps.setInt(7, vuelo.getCapacidad());
            ps.setString(8, vuelo.getEstado());
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
     * Lista todos los vuelos ordenados por ID.
     *
     * @return lista de vuelos (vacía si no hay registros)
     */
    public List<Vuelo> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Vuelo> vuelos = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                vuelos.add(mapearVuelo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return vuelos;
    }

    /**
     * Busca un vuelo por su ID.
     *
     * @param idVuelo el identificador del vuelo
     * @return el Vuelo encontrado, o null si no existe
     */
    public Vuelo buscarPorId(int idVuelo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vuelo vuelo = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, idVuelo);
            rs = ps.executeQuery();
            if (rs.next()) {
                vuelo = mapearVuelo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return vuelo;
    }

    /**
     * Actualiza los datos de un vuelo existente.
     *
     * @param vuelo el objeto Vuelo con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizar(Vuelo vuelo) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, vuelo.getCodigo());
            ps.setString(2, vuelo.getOrigen());
            ps.setString(3, vuelo.getDestino());
            ps.setString(4, vuelo.getFecha());
            ps.setString(5, vuelo.getHora());
            ps.setString(6, vuelo.getTipo());
            ps.setInt(7, vuelo.getCapacidad());
            ps.setString(8, vuelo.getEstado());
            ps.setInt(9, vuelo.getIdVuelo());
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
     * Elimina un vuelo por su ID.
     *
     * @param idVuelo el identificador del vuelo a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int idVuelo) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, idVuelo);
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
     * Verifica si ya existe un vuelo con el código especificado.
     *
     * @param codigo el código a verificar
     * @return true si el código ya está registrado, false en caso contrario
     */
    public boolean existeCodigo(String codigo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_CODIGO);
            ps.setString(1, codigo);
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
     * Verifica si ya existe un vuelo con el código especificado,
     * excluyendo al vuelo con el ID indicado (para validación en edición).
     *
     * @param codigo el código a verificar
     * @param idVuelo el ID del vuelo a excluir
     * @return true si el código ya está registrado por otro vuelo, false en caso contrario
     */
    public boolean existeCodigoExcepto(String codigo, int idVuelo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_CODIGO_EXCEPTO);
            ps.setString(1, codigo);
            ps.setInt(2, idVuelo);
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
     * Mapea un ResultSet a un objeto Vuelo.
     *
     * @param rs el ResultSet posicionado en una fila válida
     * @return un objeto Vuelo con los datos de la fila
     * @throws SQLException si ocurre un error al leer las columnas
     */
    private Vuelo mapearVuelo(ResultSet rs) throws SQLException {
        Vuelo vuelo = new Vuelo();
        vuelo.setIdVuelo(rs.getInt("id_vuelo"));
        vuelo.setCodigo(rs.getString("codigo"));
        vuelo.setOrigen(rs.getString("origen"));
        vuelo.setDestino(rs.getString("destino"));
        vuelo.setFecha(rs.getString("fecha"));
        vuelo.setHora(rs.getString("hora"));
        vuelo.setTipo(rs.getString("tipo"));
        vuelo.setCapacidad(rs.getInt("capacidad"));
        vuelo.setEstado(rs.getString("estado"));
        return vuelo;
    }
}
