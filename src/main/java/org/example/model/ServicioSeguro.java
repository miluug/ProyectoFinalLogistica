package org.example.model;

public class ServicioSeguro extends ServicioAdicional {

    public ServicioSeguro(ICompra compra) {
        super(compra);
    }

    @Override
    public double getTotal() {
        return compra.getTotal() + 10000;
    }

    @Override
    public String getDescripcion() {
        return compra.getDescripcion() + " + Seguro";
    }
}