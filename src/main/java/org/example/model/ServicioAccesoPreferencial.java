package org.example.model;

public class ServicioAccesoPreferencial extends ServicioAdicional {

    public ServicioAccesoPreferencial(ICompra compra) {
        super(compra);
    }

    @Override
    public double getTotal() {
        return compra.getTotal() + 25000;
    }

    @Override
    public String getDescripcion() {
        return compra.getDescripcion() + "\nServicio adicional:" + "\n - Acceso Preferencial";
    }
}