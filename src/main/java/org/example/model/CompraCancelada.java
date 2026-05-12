package org.example.model;

public class CompraCancelada implements IEstadoCompra {

    @Override
    public void reembolsar(Compra compra) {
        System.out.println("Procesando reembolso...");
        compra.setEstadoEnum(EstadoCompra.REEMBOLSADA);
        compra.setEstado(new CompraReembolsada());
    }

    @Override
    public void pagar(Compra compra){
        throw new IllegalStateException("La compra está cancelada.");
    }

    @Override
    public void confirmar(Compra compra){
        throw new IllegalStateException("La compra está cancelada.");
    }

    @Override public void cancelar(Compra compra){
        throw new IllegalStateException("Ya está cancelada.");
    }
}