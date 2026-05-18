package org.example.model;

import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Entidad central del sistema. Implementa:
 * - ICompra → permite ser decorada por ServicioAdicional (Decorator)
 * - State → delega operaciones al estado actual (IEstadoCompra)
 *
 * Se construye exclusivamente mediante Compra.Builder para garantizar
 * que los campos obligatorios siempre estén presentes.
 */
public class Compra implements ICompra {

    private final String idCompra;
    private final Usuario usuario;
    private final Evento evento;
    private final LocalDate fechaCreacion;

    private Pago pago;
    private Tarifa tarifa;

    // State pattern
    private IEstadoCompra estadoActual;
    private EstadoCompra estadoEnum;

    private final LinkedList<Entrada> listEntradas;

    // Constructor privado — solo accesible desde Builder
    private Compra(Builder builder) {
        this.idCompra = builder.idCompra;
        this.usuario = builder.usuario;
        this.evento = builder.evento;
        this.fechaCreacion = builder.fechaCreacion;
        this.tarifa = builder.tarifa;
        this.listEntradas = new LinkedList<>();

        // Estado inicial siempre es CREADA
        this.estadoActual = new CompraCreada();
        this.estadoEnum = EstadoCompra.CREADA;
    }

    // -------------------------
    // State — delegación de operaciones
    // -------------------------

    public void setEstado(IEstadoCompra nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public void pagar()     { estadoActual.pagar(this); }
    public void cancelar()  { estadoActual.cancelar(this); }
    public void confirmar() { estadoActual.confirmar(this); }
    public void reembolsar(){ estadoActual.reembolsar(this); }

    // -------------------------
    // Decorator — ICompra
    // -------------------------

    @Override
    public double getTotal() {
        double base = listEntradas.stream()
                .mapToDouble(Entrada::calcularPrecioFinal)
                .sum();
        return (tarifa != null) ? tarifa.calcularTotal(base) : base;
    }

    @Override
    public String getDescripcion() {
        StringBuilder entradas = new StringBuilder();
        if (listEntradas.isEmpty()) {
            entradas.append(" - No hay entradas agregadas\n");
        } else {
            for (Entrada e : listEntradas) {
                entradas.append(" - ")
                        .append(e.getIdEntrada())
                        .append(" | Zona: ").append(e.getZona().getTipoZona())
                        .append(" | Precio: $").append(e.calcularPrecioFinal())
                        .append("\n");
            }
        }

        return "========== COMPRA ==========\n" +
                "ID Compra: " + idCompra + "\n" +
                "Usuario: " + usuario.getNombreCompleto() + "\n" +
                "Evento: " + evento.getNombre() + "\n" +
                "Fecha: " + fechaCreacion + "\n" +
                "Estado: " + estadoEnum + "\n\n" +
                "Entradas:\n" + entradas + "\n" +
                "Total: $" + getTotal() + "\n" +
                "============================";
    }

    // -------------------------
    // Getters y Setters
    // -------------------------

    public String getIdCompra() { return idCompra; }
    public Usuario getUsuario() { return usuario; }
    public Evento getEvento() { return evento; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    public Tarifa getTarifa() { return tarifa; }
    public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }

    public EstadoCompra getEstadoEnum() { return estadoEnum; }
    public void setEstadoEnum(EstadoCompra estadoEnum) { this.estadoEnum = estadoEnum; }

    public LinkedList<Entrada> getListEntradas() { return listEntradas; }

    public void agregarEntrada(Entrada entrada) {
        listEntradas.add(entrada);
    }

    // -------------------------
    // Builder
    // -------------------------

    /**
     * Builder para Compra. Garantiza que idCompra, usuario y evento
     * siempre estén presentes al construir una compra.
     *
     * Uso:
     *   Compra compra = new Compra.Builder("C001", usuario, evento)
     *       .tarifa(tarifa)
     *       .build();
     */
    public static class Builder {

        // Obligatorios
        private final String idCompra;
        private final Usuario usuario;
        private final Evento evento;

        // Opcionales con defaults
        private LocalDate fechaCreacion = LocalDate.now();
        private Tarifa tarifa = null;

        public Builder(String idCompra, Usuario usuario, Evento evento) {
            if (idCompra == null || usuario == null || evento == null) {
                throw new IllegalArgumentException("idCompra, usuario y evento son obligatorios.");
            }
            this.idCompra = idCompra;
            this.usuario = usuario;
            this.evento = evento;
        }

        public Builder fechaCreacion(LocalDate fecha) {
            this.fechaCreacion = fecha;
            return this;
        }

        public Builder tarifa(Tarifa tarifa) {
            this.tarifa = tarifa;
            return this;
        }

        public Compra build() {
            return new Compra(this);
        }
    }
}
