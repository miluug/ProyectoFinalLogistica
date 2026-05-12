package org.example.model;

public class CompraPagada implements IEstadoCompra {

    @Override
    public void confirmar(Compra compra) {
        System.out.println("Confirmando compra...");
        compra.setEstadoEnum(EstadoCompra.CONFIRMADA);
        compra.setEstado(new CompraConfirmada());
    }

    @Override
    public void cancelar(Compra compra) {
        System.out.println("Cancelando compra...");
        compra.getListEntradas().forEach(e -> e.setEstadoEntrada(EstadoEntrada.ANULADA));
        compra.setEstadoEnum(EstadoCompra.CANCELADA);
        compra.setEstado(new CompraCancelada());
    }

    @Override public void pagar(Compra compra){
        throw new IllegalStateException("La compra ya fue pagada.");
    }

    @Override public void reembolsar(Compra compra){
        throw new IllegalStateException("Debe confirmar o cancelar primero.");
    }
}
