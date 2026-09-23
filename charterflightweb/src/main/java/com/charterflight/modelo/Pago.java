package com.charterflight.modelo;

/**
 * Clase modelo que representa un pago de reserva del sistema Charter Flight.
 * Mapea los campos de la tabla {@code pagos} en la base de datos.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class Pago {

    private int idPago;
    private int idReserva;
    private int idCliente;
    private int idVuelo;
    private double monto;
    private String estado;
    private String fechaPago;
    private String numeroFactura;

    /**
     * Constructor vacío. Requerido para frameworks y beans.
     */
    public Pago() {
    }

    /**
     * Constructor con todos los campos excepto el ID (para inserciones).
     *
     * @param idReserva      identificador de la reserva asociada
     * @param idCliente      identificador del cliente
     * @param idVuelo        identificador del vuelo
     * @param monto          monto del pago
     * @param estado         estado del pago
     * @param fechaPago      fecha del pago
     * @param numeroFactura  número de factura
     */
    public Pago(int idReserva, int idCliente, int idVuelo, double monto, String estado, String fechaPago, String numeroFactura) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.idVuelo = idVuelo;
        this.monto = monto;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.numeroFactura = numeroFactura;
    }

    /**
     * Constructor con todos los campos incluido el ID (para actualizaciones).
     *
     * @param idPago         identificador único del pago
     * @param idReserva      identificador de la reserva asociada
     * @param idCliente      identificador del cliente
     * @param idVuelo        identificador del vuelo
     * @param monto          monto del pago
     * @param estado         estado del pago
     * @param fechaPago      fecha del pago
     * @param numeroFactura  número de factura
     */
    public Pago(int idPago, int idReserva, int idCliente, int idVuelo, double monto, String estado, String fechaPago, String numeroFactura) {
        this.idPago = idPago;
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.idVuelo = idVuelo;
        this.monto = monto;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.numeroFactura = numeroFactura;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    @Override
    public String toString() {
        return "Pago [idPago=" + idPago + ", idReserva=" + idReserva + ", idCliente=" + idCliente
                + ", idVuelo=" + idVuelo + ", monto=" + monto + ", estado=" + estado
                + ", fechaPago=" + fechaPago + ", numeroFactura=" + numeroFactura + "]";
    }
}