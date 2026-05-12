package org.example.model;
public class Entrada {

    private String idEntrada;
    private EstadoEntrada estadoEntrada;
    private Zona zona;

    public Entrada(String idEntrada, EstadoEntrada estadoEntrada) {
        this.estadoEntrada = EstadoEntrada.ACTIVA;
        this.idEntrada = idEntrada;
    }

    public double calcularPrecioFinal() {
        if (zona != null) {
            return zona.getPrecioBase();
        }
        return 0;
    }

    //Getters y Setters

    public String getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(String idEntrada) {
        this.idEntrada = idEntrada;
    }

    public EstadoEntrada getEstadoEntrada() {
        return estadoEntrada;
    }

    public void setEstadoEntrada(EstadoEntrada estadoEntrada) {
        this.estadoEntrada = estadoEntrada;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }
}