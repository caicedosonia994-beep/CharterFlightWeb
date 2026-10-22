package com.charterflight.modelo;

public class SolicitudEspecial {
    private int idSistema;
    private String nombreContacto;
    private String correoContacto;
    private String telefonoContacto;
    private String tipo;
    private String origen;
    private String destino;
    private String fechaReq;
    private String tipoServicio;
    private String avionSolicitado;
    private String observaciones;
    private String tipoMercancia;
    private String descripcionCarga;
    private String motivoViaje;
    private Integer pasajerosEstimados;
    private String estado;
    private String fechaSistema;

    public SolicitudEspecial() {}
    public int getIdSistema() { return idSistema; }
    public int getIdSolicitud() { return idSistema; }
    public void setIdSolicitud(int v) { this.idSistema = v; }
    public void setIdSistema(int v) { this.idSistema = v; }
    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String v) { this.nombreContacto = v; }
    public String getCorreoContacto() { return correoContacto; }
    public void setCorreoContacto(String v) { this.correoContacto = v; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String v) { this.telefonoContacto = v; }
    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }
    public String getOrigen() { return origen; }
    public void setOrigen(String v) { this.origen = v; }
    public String getDestino() { return destino; }
    public void setDestino(String v) { this.destino = v; }
    public String getFechaReq() { return fechaReq; }
    public void setFechaReq(String v) { this.fechaReq = v; }
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String v) { this.tipoServicio = v; }
    public String getAvionSolicitado() { return avionSolicitado; }
    public void setAvionSolicitado(String v) { this.avionSolicitado = v; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String v) { this.observaciones = v; }
    public String getTipoMercancia() { return tipoMercancia; }
    public void setTipoMercancia(String v) { this.tipoMercancia = v; }
    public String getDescripcionCarga() { return descripcionCarga; }
    public void setDescripcionCarga(String v) { this.descripcionCarga = v; }
    public String getMotivoViaje() { return motivoViaje; }
    public void setMotivoViaje(String v) { this.motivoViaje = v; }
    public Integer getPasajerosEstimados() { return pasajerosEstimados; }
    public void setPasajerosEstimados(Integer v) { this.pasajerosEstimados = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public String getFechaSistema() { return fechaSistema; }
    public String getFechaSolicitud() { return fechaSistema; }
    public void setFechaSolicitud(String v) { this.fechaSistema = v; }
    public void setFechaSistema(String v) { this.fechaSistema = v; }
}
