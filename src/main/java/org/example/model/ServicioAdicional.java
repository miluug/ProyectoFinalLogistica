package org.example.model;

public abstract class ServicioAdicional implements ICompra {

    protected ICompra compra;

    public ServicioAdicional (ICompra compra) {
        this.compra = compra;
    }
}
