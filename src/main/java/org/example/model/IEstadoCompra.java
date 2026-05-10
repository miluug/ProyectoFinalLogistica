package org.example.model;

public interface IEstadoCompra {

    void pagar(Compra compra);
    void cancelar(Compra compra);
    void confirmar(Compra compra);
    void reembolsar(Compra compra);
}