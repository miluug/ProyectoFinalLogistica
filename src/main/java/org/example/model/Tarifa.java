package org.example.model;


public class Tarifa {

    private String idTarifa;
    private String nombre;
    private double valor;

    public Tarifa() {
    }

    public Tarifa(String idTarifa, String nombre, double valor) {
        this.idTarifa = idTarifa;
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(String idTarifa) {
        this.idTarifa = idTarifa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}


