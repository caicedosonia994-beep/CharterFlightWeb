package com.charterflight.modelo;

/**
 * Clase modelo que representa un vuelo charter del sistema Charter Flight.
 * Mapea los campos de la tabla {@code vuelos} en la base de datos.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class Vuelo {

    private int idVuelo;
    private String codigo;
    private String origen;
    private String destino;
    private String fecha;
    private String hora;
    private String tipo;
    private int capacidad;
    private String estado;

    /**
     * Constructor vacío. Requerido para frameworks y beans.
     */
    public Vuelo() {
    }

    /**
     * Constructor con todos los campos excepto el ID (para inserciones).
     *
     * @param codigo   código del vuelo
     * @param origen   ciudad de origen
     * @param destino  ciudad de destino
     * @param fecha    fecha de salida
     * @param hora     hora de salida
     * @param tipo     tipo de vuelo (nacional, internacional, transcontinental)
     * @param capacidad capacidad en pasajeros
     * @param estado   estado del vuelo
     */
    public Vuelo(String codigo, String origen, String destino, String fecha, String hora, String tipo, int capacidad, String estado) {
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    /**
     * Constructor con todos los campos incluido el ID (para actualizaciones).
     *
     * @param idVuelo identificador único del vuelo
     * @param codigo   código del vuelo
     * @param origen   ciudad de origen
     * @param destino  ciudad de destino
     * @param fecha    fecha de salida
     * @param hora     hora de salida
     * @param tipo     tipo de vuelo
     * @param capacidad capacidad en pasajeros
     * @param estado   estado del vuelo
     */
    public Vuelo(int idVuelo, String codigo, String origen, String destino, String fecha, String hora, String tipo, int capacidad, String estado) {
        this.idVuelo = idVuelo;
        this.codigo = codigo;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Vuelo [idVuelo=" + idVuelo + ", codigo=" + codigo + ", origen=" + origen
                + ", destino=" + destino + ", fecha=" + fecha + ", hora=" + hora
                + ", tipo=" + tipo + ", capacidad=" + capacidad + ", estado=" + estado + "]";
    }
}
