package org.example.model;

public class Tarifa {

    private double precioBase;
    private double porcentajeImpuesto;
    private double comisionServicio;


    public Tarifa(double precioBase, double porcentajeImpuesto, double comisionServicio) {
        this.precioBase = precioBase;
        this.porcentajeImpuesto = porcentajeImpuesto;
        this.comisionServicio = comisionServicio;
    }

    public double calcularTotal(double totalBase) {
        return totalBase + (totalBase * porcentajeImpuesto / 100) + comisionServicio;
    }

    //Getters y Setters

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public double getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(double porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }

    public double getComisionServicio() {
        return comisionServicio;
    }

    public void setComisionServicio(double comisionServicio) {
        this.comisionServicio = comisionServicio;
    }
}


