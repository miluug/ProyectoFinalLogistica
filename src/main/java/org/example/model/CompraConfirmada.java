package org.example.model;

public class CompraConfirmada implements IEstadoCompra {

    @Override
    public void cancelar(Compra compra) {
        System.out.println("Cancelando compra confirmada...");
        compra.setEstadoEnum(EstadoCompra.CANCELADA);
        compra.setEstado(new CompraCancelada());
    }

    @Override
    public void pagar(Compra compra){
        throw new IllegalStateException("La compra ya está confirmada.");
    }

    @Override
    public void confirmar(Compra compra){
        throw new IllegalStateException("La compra ya está confirmada.");
    }

    @Override
    public void reembolsar(Compra compra) {
        throw new IllegalStateException("Cancele primero para reembolsar.");
    }
}