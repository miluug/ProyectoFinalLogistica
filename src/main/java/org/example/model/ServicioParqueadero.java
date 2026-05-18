package org.example.model;

public class ServicioParqueadero extends ServicioAdicional {

    public ServicioParqueadero(ICompra compra) {
        super(compra);
    }

    @Override
    public double getTotal() {
        return compra.getTotal() + 15000;
    }

    @Override
    public String getDescripcion() {
        return compra.getDescripcion() + "\nServicio adicional:" + "\n - Parqueadero";
    }
}
