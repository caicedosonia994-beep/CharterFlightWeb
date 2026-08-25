package com.charterflight.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase de conexión a la base de datos MySQL.
 * Proporciona métodos estáticos para obtener y cerrar conexiones JDBC,
 * PreparedStatements y ResultSets de forma segura.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class Conexion {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/charterflight?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver de MySQL", e);
        }
    }

    /**
     * Obtiene una conexión a la base de datos MySQL.
     *
     * @return Connection instancia de conexión activa
     * @throws SQLException si ocurre un error de conexión
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    /**
     * Cierra una conexión JDBC de forma segura.
     *
     * @param conn la conexión a cerrar (puede ser null)
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cierra un PreparedStatement de forma segura.
     *
     * @param ps el PreparedStatement a cerrar (puede ser null)
     */
    public static void close(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cierra un ResultSet de forma segura.
     *
     * @param rs el ResultSet a cerrar (puede ser null)
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cierra todos los recursos JDBC en orden inverso (ResultSet, PreparedStatement, Connection).
     *
     * @param conn la conexión a cerrar (puede ser null)
     * @param ps   el PreparedStatement a cerrar (puede ser null)
     * @param rs   el ResultSet a cerrar (puede ser null)
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
        close(conn);
    }
}
