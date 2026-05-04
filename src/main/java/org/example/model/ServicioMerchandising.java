package org.example.model;

public class ServicioMerchandising extends ServicioAdicional {

    public ServicioMerchandising(ICompra compra) {
        super(compra);
    }

    @Override
    public double getTotal() {
        return compra.getTotal() + 20000;
    }

    @Override
    public String getDescripcion() {
        return compra.getDescripcion() + " + Merchandising";
    }
}