package org.example.model;
public class CompraCreada implements IEstadoCompra {

    @Override
    public void pagar(Compra compra) {
        System.out.println("Procesando pago...");
        compra.setEstadoEnum(EstadoCompra.PAGADA);
        compra.setEstado(new CompraPagada());
    }

    @Override
    public void cancelar(Compra compra) {
        System.out.println("Cancelando compra...");
        compra.setEstadoEnum(EstadoCompra.CANCELADA);
        compra.setEstado(new CompraCancelada());
    }

    @Override
    public void confirmar(Compra compra)  {
        throw new IllegalStateException("Debe pagar primero.");
    }

    @Override
    public void reembolsar(Compra compra) {
        throw new IllegalStateException("No se puede reembolsar una compra no pagada.");
    }
}
