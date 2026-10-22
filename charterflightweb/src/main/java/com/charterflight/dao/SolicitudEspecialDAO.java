package com.charterflight.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.charterflight.conexion.Conexion;
import com.charterflight.modelo.SolicitudEspecial;

public class SolicitudEspecialDAO {

    private static final String SQL_INSERT = "INSERT INTO solicitudes_especiales (nombre_contacto, correo_contacto, telefono_contacto, tipo, origen, destino, fecha_req, tipo_servicio, avion_solicitado, observaciones, tipo_mercancia, descripcion_carga, motivo_viaje, pasajeros_estimados, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT id_solicitud, nombre_contacto, correo_contacto, telefono_contacto, tipo, origen, destino, fecha_req, tipo_servicio, avion_solicitado, observaciones, tipo_mercancia, descripcion_carga, motivo_viaje, pasajeros_estimados, estado, fecha_solicitud FROM solicitudes_especiales ORDER BY id_solicitud ASC";
    private static final String SQL_SELECT_BY_ID = "SELECT id_solicitud, nombre_contacto, correo_contacto, telefono_contacto, tipo, origen, destino, fecha_req, tipo_servicio, avion_solicitado, observaciones, tipo_mercancia, descripcion_carga, motivo_viaje, pasajeros_estimados, estado, fecha_solicitud FROM solicitudes_especiales WHERE id_solicitud = ?";
    private static final String SQL_UPDATE = "UPDATE solicitudes_especiales SET estado = ? WHERE id_solicitud = ?";
    private static final String SQL_UPDATE_COMPLETO = "UPDATE solicitudes_especiales SET nombre_contacto = ?, correo_contacto = ?, telefono_contacto = ?, tipo = ?, origen = ?, destino = ?, fecha_req = ?, tipo_servicio = ?, avion_solicitado = ?, observaciones = ?, tipo_mercancia = ?, descripcion_carga = ?, motivo_viaje = ?, pasajeros_estimados = ?, estado = ? WHERE id_solicitud = ?";

    public int insertar(SolicitudEspecial s) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, s.getNombreContacto());
            ps.setString(2, s.getCorreoContacto());
            ps.setString(3, s.getTelefonoContacto());
            ps.setString(4, s.getTipo());
            ps.setString(5, s.getOrigen());
            ps.setString(6, s.getDestino());
            ps.setString(7, s.getFechaReq());
            ps.setString(8, s.getTipoServicio());
            ps.setString(9, s.getAvionSolicitado());
            ps.setString(10, s.getObservaciones());
            ps.setString(11, s.getTipoMercancia());
            ps.setString(12, s.getDescripcionCarga());
            ps.setString(13, s.getMotivoViaje());
            if (s.getPasajerosEstimados() != null) ps.setInt(14, s.getPasajerosEstimados()); else ps.setNull(14, java.sql.Types.INTEGER);
            ps.setString(15, s.getEstado());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            Conexion.close(conn, ps, null);
        }
    }

    public List<SolicitudEspecial> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SolicitudEspecial> lista = new ArrayList<>();
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return lista;
    }

    public SolicitudEspecial buscarPorId(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(conn, ps, rs);
        }
        return null;
    }

    public int actualizarEstado(int idSolicitud, String estado) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, estado);
            ps.setInt(2, idSolicitud);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            Conexion.close(conn, ps, null);
        }
    }

    public int actualizar(SolicitudEspecial s) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE_COMPLETO);
            ps.setString(1, s.getNombreContacto());
            ps.setString(2, s.getCorreoContacto());
            ps.setString(3, s.getTelefonoContacto());
            ps.setString(4, s.getTipo());
            ps.setString(5, s.getOrigen());
            ps.setString(6, s.getDestino());
            ps.setString(7, s.getFechaReq());
            ps.setString(8, s.getTipoServicio());
            ps.setString(9, s.getAvionSolicitado());
            ps.setString(10, s.getObservaciones());
            ps.setString(11, s.getTipoMercancia());
            ps.setString(12, s.getDescripcionCarga());
            ps.setString(13, s.getMotivoViaje());
            if (s.getPasajerosEstimados() != null) ps.setInt(14, s.getPasajerosEstimados());
            else ps.setNull(14, java.sql.Types.INTEGER);
            ps.setString(15, s.getEstado());
            ps.setInt(16, s.getIdSistema());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            Conexion.close(conn, ps, null);
        }
    }

    private SolicitudEspecial mapear(ResultSet rs) throws SQLException {
        SolicitudEspecial s = new SolicitudEspecial();
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        s.setNombreContacto(rs.getString("nombre_contacto"));
        s.setCorreoContacto(rs.getString("correo_contacto"));
        s.setTelefonoContacto(rs.getString("telefono_contacto"));
        s.setTipo(rs.getString("tipo"));
        s.setOrigen(rs.getString("origen"));
        s.setDestino(rs.getString("destino"));
        s.setFechaReq(rs.getString("fecha_req"));
        s.setTipoServicio(rs.getString("tipo_servicio"));
        s.setAvionSolicitado(rs.getString("avion_solicitado"));
        s.setObservaciones(rs.getString("observaciones"));
        s.setTipoMercancia(rs.getString("tipo_mercancia"));
        s.setDescripcionCarga(rs.getString("descripcion_carga"));
        s.setMotivoViaje(rs.getString("motivo_viaje"));
        s.setPasajerosEstimados(rs.getInt("pasajeros_estimados"));
        if (rs.wasNull()) s.setPasajerosEstimados(null);
        s.setEstado(rs.getString("estado"));
        s.setFechaSolicitud(rs.getString("fecha_solicitud"));
        return s;
    }
}
