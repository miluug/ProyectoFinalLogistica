package org.example.model;

public class Compra implements ICompra {

    private double totalBase;

    public Compra(double totalBase) {
        this.totalBase = totalBase;
    }

    @Override
    public double getTotal() {
        return totalBase;
    }

    @Override
    public String getDescripcion() {
        return "Compra base";
    }
}
