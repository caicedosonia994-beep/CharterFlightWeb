package com.charterflight.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.charterflight.conexion.Conexion;
import com.charterflight.modelo.Cliente;

/**
 * Clase DAO (Data Access Object) para la entidad Cliente.
 * Proporciona operaciones CRUD (insertar, listar, buscar, actualizar, eliminar)
 * utilizando PreparedStatement para prevenir inyección SQL.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class ClienteDAO {

    private static final String SQL_INSERT = "INSERT INTO clientes (nombre, apellido, documento, telefono, correo) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT id_cliente, nombre, apellido, documento, telefono, correo FROM clientes ORDER BY id_cliente ASC";
    private static final String SQL_SELECT_BY_ID = "SELECT id_cliente, nombre, apellido, documento, telefono, correo FROM clientes WHERE id_cliente = ?";
    private static final String SQL_UPDATE = "UPDATE clientes SET nombre = ?, apellido = ?, documento = ?, telefono = ?, correo = ? WHERE id_cliente = ?";
    private static final String SQL_DELETE = "DELETE FROM clientes WHERE id_cliente = ?";
    private static final String SQL_SEARCH = "SELECT id_cliente, nombre, apellido, documento, telefono, correo FROM clientes WHERE nombre LIKE ? OR apellido LIKE ? OR documento LIKE ? ORDER BY id_cliente ASC";
    private static final String SQL_CHECK_DOCUMENTO = "SELECT COUNT(*) FROM clientes WHERE documento = ?";
    private static final String SQL_CHECK_DOCUMENTO_EXCEPTO = "SELECT COUNT(*) FROM clientes WHERE documento = ? AND id_cliente != ?";

    /**
     * Inserta un nuevo cliente en la base de datos.
     *
     * @param cliente el objeto Cliente a insertar
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertar(Cliente cliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDocumento());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
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
     * Lista todos los clientes ordenados por ID.
     *
     * @return lista de clientes (vacía si no hay registros)
     */
    public List<Cliente> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return clientes;
    }

    /**
     * Busca un cliente por su ID.
     *
     * @param idCliente el identificador del cliente
     * @return el Cliente encontrado, o null si no existe
     */
    public Cliente buscarPorId(int idCliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cliente cliente = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();
            if (rs.next()) {
                cliente = mapearCliente(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return cliente;
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente el objeto Cliente con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizar(Cliente cliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDocumento());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
            ps.setInt(6, cliente.getIdCliente());
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
     * Elimina un cliente por su ID.
     *
     * @param idCliente el identificador del cliente a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean eliminar(int idCliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, idCliente);
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
     * Busca clientes por nombre, apellido o documento.
     *
     * @param termino el término de búsqueda
     * @return lista de clientes que coinciden con la búsqueda (vacía si no hay resultados)
     */
    public List<Cliente> buscar(String termino) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SEARCH);
            String pattern = "%" + termino + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return clientes;
    }

    /**
     * Verifica si ya existe un cliente con el documento especificado.
     *
     * @param documento el documento a verificar
     * @return true si el documento ya está registrado, false en caso contrario
     */
    public boolean existeDocumento(String documento) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_DOCUMENTO);
            ps.setString(1, documento);
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
     * Verifica si ya existe un cliente con el documento especificado,
     * excluyendo al cliente con el ID indicado (para validación en edición).
     *
     * @param documento el documento a verificar
     * @param idCliente el ID del cliente a excluir
     * @return true si el documento ya está registrado por otro cliente, false en caso contrario
     */
    public boolean existeDocumentoExcepto(String documento, int idCliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_CHECK_DOCUMENTO_EXCEPTO);
            ps.setString(1, documento);
            ps.setInt(2, idCliente);
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
     * Mapea un ResultSet a un objeto Cliente.
     *
     * @param rs el ResultSet posicionado en una fila válida
     * @return un objeto Cliente con los datos de la fila
     * @throws SQLException si ocurre un error al leer las columnas
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellido(rs.getString("apellido"));
        cliente.setDocumento(rs.getString("documento"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreo(rs.getString("correo"));
        return cliente;
    }
}
