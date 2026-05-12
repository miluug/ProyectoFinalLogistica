package org.example.model;
import java.util.LinkedList;

public class Zona {

    private String idZona;
    private String nombre;
    private TipoZona tipoZona;
    private double precioBase;
    private boolean numerado;

    //Para general
    private int capacidad;

    //Para numerado
    private LinkedList <Asiento> asientos;


    public Zona(String idZona, String nombre, TipoZona tipoZona, int capacidad, double precioBase, boolean numerado ) {
            this.idZona = idZona;
            this.nombre = nombre;
            this.tipoZona = tipoZona;
            this.capacidad = capacidad;
            this.precioBase = precioBase;
            this.numerado = numerado;
            asientos = new LinkedList<>();

        }
    //Getters y Setters

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

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public boolean isNumerado() {
        return numerado;
    }

    public void setNumerado(boolean numerado) {
        this.numerado = numerado;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public LinkedList<Asiento> getAsientos() {
        return asientos;
    }

    public void setAsientos(LinkedList<Asiento> asientos) {
        this.asientos = asientos;
    }
}
