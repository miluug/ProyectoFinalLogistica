package org.example.model;

public class CompraReembolsada implements IEstadoCompra {

    @Override
    public void pagar(Compra compra){
        throw new IllegalStateException("Compra reembolsada, no se puede operar.");
    }

    @Override
    public void cancelar(Compra compra){
        throw new IllegalStateException("Compra reembolsada, no se puede operar.");
    }

    @Override
    public void confirmar(Compra compra){
        throw new IllegalStateException("Compra reembolsada, no se puede operar.");
    }

    @Override
    public void reembolsar(Compra compra){
        throw new IllegalStateException("Ya fue reembolsada.");
    }
}