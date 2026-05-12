package org.example.model;

public class PagoTarjetaCredito implements PagoStrategy {
    @Override
    public boolean pagar(double monto) {
        System.out.println("Pagando $" + monto + " con tarjeta de crédito.");
        return true;
    }
}