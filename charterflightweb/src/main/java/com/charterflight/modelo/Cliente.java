package com.charterflight.modelo;

/**
 * Clase modelo que representa un cliente del sistema Charter Flight.
 * Mapea los campos de la tabla {@code clientes} en la base de datos.
 *
 * @author Charter Flight Development Team
 * @version 1.0
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private String correo;

    /**
     * Constructor vacío. Requerido para frameworks y beans.
     */
    public Cliente() {
    }

    /**
     * Constructor con todos los campos excepto el ID (para inserciones).
     *
     * @param nombre    nombre del cliente
     * @param apellido  apellido del cliente
     * @param documento documento o pasaporte
     * @param telefono  número de contacto
     * @param correo    correo electrónico
     */
    public Cliente(String nombre, String apellido, String documento, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
    }

    /**
     * Constructor con todos los campos incluido el ID (para actualizaciones).
     *
     * @param idCliente identificador único del cliente
     * @param nombre    nombre del cliente
     * @param apellido  apellido del cliente
     * @param documento documento o pasaporte
     * @param telefono  número de contacto
     * @param correo    correo electrónico
     */
    public Cliente(int idCliente, String nombre, String apellido, String documento, String telefono, String correo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
    }

    /**
     * Obtiene el ID del cliente.
     *
     * @return el identificador único
     */
    public int getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el ID del cliente.
     *
     * @param idCliente el identificador único
     */
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     *
     * @param nombre el nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del cliente.
     *
     * @return el apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del cliente.
     *
     * @param apellido el apellido
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el documento del cliente.
     *
     * @return el documento o pasaporte
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Establece el documento del cliente.
     *
     * @param documento el documento o pasaporte
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * Obtiene el teléfono del cliente.
     *
     * @return el teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del cliente.
     *
     * @param telefono el teléfono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el correo electrónico del cliente.
     *
     * @return el correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del cliente.
     *
     * @param correo el correo electrónico
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Devuelve una representación en cadena del cliente para depuración.
     *
     * @return cadena con todos los campos del cliente
     */
    @Override
    public String toString() {
        return "Cliente [idCliente=" + idCliente + ", nombre=" + nombre + ", apellido=" + apellido
                + ", documento=" + documento + ", telefono=" + telefono + ", correo=" + correo + "]";
    }
}
