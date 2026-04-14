package org.example.model;

public class Zona {

    private String idZona;
    private String nombre;
    private TipoZona tipoZona;
    private int capacidad;
    private double precioBase;

        public Zona(String idZona, String nombre, TipoZona tipoZona, int capacidad, double precioBase) {
            this.idZona = idZona;
            this.nombre = nombre;
            this.tipoZona = tipoZona;
            this.capacidad = capacidad;
            this.precioBase = precioBase;
        }

    public String getIdZona() {
        return idZona;
    }

    public void setIdZona(String idZona) {
        this.idZona = idZona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoZona getTipoZona() {
        return tipoZona;
    }

    public void setTipoZona(TipoZona tipoZona) {
        this.tipoZona = tipoZona;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
}
