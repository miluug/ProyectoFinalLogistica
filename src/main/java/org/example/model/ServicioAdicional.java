package org.example.model;

public abstract class ServicioAdicional implements ICompra {

    protected ICompra compra;

    public ServicioAdicional (ICompra compra) {
        this.compra = compra;
    }

    @Override
    public double getTotal(){
        return compra.getTotal();
    }

    @Override
    public String getDescripcion(){
        return compra.getDescripcion();
    }
}
