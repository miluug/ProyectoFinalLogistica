package org.example.model;

public class PagoPSE implements PagoStrategy {
    @Override
    public boolean pagar(double monto) {
        System.out.println("Pagando $" + monto + " por PSE.");
        return true;
    }
}
