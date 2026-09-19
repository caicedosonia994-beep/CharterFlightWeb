package com.charterflight.modelo;

/**
 * Clase modelo que representa una reserva de vuelo charter del sistema Charter Flight.
 * Mapea los campos de la tabla {@code reservas} en la base de datos.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class Reserva {

    private int idReserva;
    private int idCliente;
    private int idVuelo;
    private int pasajeros;
    private String fechaReserva;
    private String estado;

    /**
     * Constructor vacío. Requerido para frameworks y beans.
     */
    public Reserva() {
    }

    /**
     * Constructor con todos los campos excepto el ID (para inserciones).
     *
     * @param idCliente      identificador del cliente que realiza la reserva
     * @param idVuelo        identificador del vuelo reservado
     * @param pasajeros      número de pasajeros
     * @param fechaReserva   fecha en que se realiza la reserva
     * @param estado         estado de la reserva
     */
    public Reserva(int idCliente, int idVuelo, int pasajeros, String fechaReserva, String estado) {
        this.idCliente = idCliente;
        this.idVuelo = idVuelo;
        this.pasajeros = pasajeros;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    /**
     * Constructor con todos los campos incluido el ID (para actualizaciones).
     *
     * @param idReserva      identificador único de la reserva
     * @param idCliente      identificador del cliente
     * @param idVuelo        identificador del vuelo
     * @param pasajeros      número de pasajeros
     * @param fechaReserva   fecha de la reserva
     * @param estado         estado de la reserva
     */
    public Reserva(int idReserva, int idCliente, int idVuelo, int pasajeros, String fechaReserva, String estado) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.idVuelo = idVuelo;
        this.pasajeros = pasajeros;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdVuelo() {
        return idVuelo;
    }

    public void setIdVuelo(int idVuelo) {
        this.idVuelo = idVuelo;
    }

    public int getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(int pasajeros) {
        this.pasajeros = pasajeros;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva [idReserva=" + idReserva + ", idCliente=" + idCliente + ", idVuelo=" + idVuelo
                + ", pasajeros=" + pasajeros + ", fechaReserva=" + fechaReserva + ", estado=" + estado + "]";
    }
}