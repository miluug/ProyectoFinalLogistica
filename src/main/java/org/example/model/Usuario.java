package org.example.model;

import java.util.LinkedList;

public  class Usuario extends Persona {

    private String nombreCompleto;
    private String correoElectronico;
    private String telefono;
    private LinkedList <MetodoPago> listMetodosPago;

    public Usuario(String nombreCompleto, String correoElectronico, String telefono, String usuario, String contrasena) {
        super(usuario, contrasena);
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
        this.listMetodosPago = new LinkedList<>();
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LinkedList<MetodoPago> getListMetodosPago() {
        return listMetodosPago;
    }

    public void setListMetodosPago(LinkedList<MetodoPago> listMetodosPago) {
        this.listMetodosPago = listMetodosPago;
    }
}

