package org.example.model;

public class PagoService {

    private PagoStrategy pagoStrategy;

    public PagoService() {}

    public void setStrategy(PagoStrategy strategy) {
        this.pagoStrategy = strategy;
    }

    public boolean procesarPago(double monto) {
        if (pagoStrategy == null) throw new IllegalStateException("Estrategia de pago no configurada.");
        return pagoStrategy.pagar(monto);
    }
}