package org.example.model;

import java.time.LocalDate;
import java.util.LinkedList;


public class Compra implements ICompra {
    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private LocalDate fechaCreacion;
    private Pago pago;
    private Tarifa tarifa;

    //State
    private IEstadoCompra iEstadoCompra;

    //enum
    private EstadoCompra estadoEnum;

    private LinkedList <Entrada> listEntradas;
    private double totalBase;

    public Compra(String idCompra, LocalDate fechaCreacion, IEstadoCompra iEstadoCompra, EstadoCompra estadoEnum, double totalBase) {
        this.idCompra = idCompra;
        this.fechaCreacion = fechaCreacion;

        // Estado inicial
        this.iEstadoCompra = new CompraCreada();
        this.estadoEnum = EstadoCompra.CREADA;

        this.totalBase = totalBase;
        listEntradas = new LinkedList<>();
    }


    //Implementación de State
    public void setEstado(IEstadoCompra iEstadoCompra) {
        this.iEstadoCompra = iEstadoCompra;
    }

    public void pagar(){ iEstadoCompra.pagar(this); }
    public void cancelar(){ iEstadoCompra.cancelar(this); }
    public void confirmar(){ iEstadoCompra.confirmar(this); }
    public void reembolsar(){ iEstadoCompra.reembolsar(this); }


    //Implementación de Decorator
    @Override
    public double getTotal() {
        double totalBase = 0;
        for (Entrada entrada : listEntradas) {
            totalBase += entrada.calcularPrecioFinal();
        }
        if (tarifa != null) {
            return tarifa.calcularTotal(totalBase);
        }
        return totalBase;
    }

    @Override
    public String getDescripcion() {

        String entradas = "";
        if (listEntradas.isEmpty()) {
            entradas = " - No hay entradas agregadas\n";

        } else {
            for (Entrada entrada : listEntradas) {
                entradas += " - "
                        + entrada.getIdEntrada()
                        + " | Zona: "
                        + entrada.getZona().getTipoZona()
                        + " | Precio: $"
                        + entrada.calcularPrecioFinal()
                        + "\n";
            }
        }

        return "========== COMPRA ==========\n" +
                        "ID Compra: " + idCompra + "\n" +
                        "Usuario: " + usuario.getNombreCompleto() + "\n" +
                        "Evento: " + evento.getNombre() + "\n" +
                        "Fecha: " + fechaCreacion + "\n" +
                        "Estado: " + estadoEnum + "\n\n" +

                        "Entradas:\n" +
                        entradas + "\n" +
                        "Total: $" + getTotal() + "\n" +
                        "============================";
    }

    //Getters y Setters


    public String getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public IEstadoCompra getiEstadoCompra() {
        return iEstadoCompra;
    }

    public void setiEstadoCompra(IEstadoCompra iEstadoCompra) {
        this.iEstadoCompra = iEstadoCompra;
    }

    public EstadoCompra getEstadoEnum() {
        return estadoEnum;
    }

    public void setEstadoEnum(EstadoCompra estadoEnum) {
        this.estadoEnum = estadoEnum;
    }

    public LinkedList<Entrada> getListEntradas() {
        return listEntradas;
    }

    public void setListEntradas(LinkedList<Entrada> listEntradas) {
        this.listEntradas = listEntradas;
    }

    public double getTotalBase() {
        return totalBase;
    }

    public void setTotalBase(double totalBase) {
        this.totalBase = totalBase;
    }
}
