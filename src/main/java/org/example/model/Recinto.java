package org.example.model;

import java.util.LinkedList;

public class Recinto {

    private String idRecinto;
    private String nombre;
    private String direccion;
    private String ciudad;
    private LinkedList <Zona> zonas;

        public Recinto(String idRecinto, String nombre, String direccion, String ciudad) {
            this.idRecinto = idRecinto;
            this.nombre = nombre;
            this.direccion = direccion;
            this.ciudad = ciudad;
            this.zonas = new LinkedList<>();
        }

    public String getIdRecinto() {
        return idRecinto;
    }

    public void setIdRecinto(String idRecinto) {
        this.idRecinto = idRecinto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LinkedList<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(LinkedList<Zona> zonas) {
        this.zonas = zonas;
    }
}
