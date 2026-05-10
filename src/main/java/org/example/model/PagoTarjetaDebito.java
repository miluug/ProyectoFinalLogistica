package org.example.model;

public class PagoTarjetaDebito implements PagoStrategy {
    @Override
    public boolean pagar(double monto) {
        System.out.println("Pagando $" + monto + " con tarjeta débito.");
        return true;
    }
}