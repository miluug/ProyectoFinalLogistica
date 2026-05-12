package org.example.model;

public class ServicioVIP extends ServicioAdicional {

        public ServicioVIP(ICompra compra) {
            super(compra);
        }

        @Override
        public double getTotal() {
            return compra.getTotal() + 50000;
        }

    @Override
    public String getDescripcion() {
        return compra.getDescripcion() + "\nServicio adicional:" + "\n - VIP";
    }
}
