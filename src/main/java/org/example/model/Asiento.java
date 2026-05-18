package org.example.model;

/**
 * Representa un asiento individual dentro de una Zona numerada.
 * Solo aplica cuando Zona.numerado == true.
 */
public class Asiento {

    private String idAsiento;
    private String fila;
    private int numero;
    private EstadoAsiento estado;

    public Asiento(String idAsiento, String fila, int numero) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.estado = EstadoAsiento.DISPONIBLE;
    }

    // Getters y Setters

    public String getIdAsiento() { return idAsiento; }
    public void setIdAsiento(String idAsiento) { this.idAsiento = idAsiento; }

    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public EstadoAsiento getEstado() { return estado; }
    public void setEstado(EstadoAsiento estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Fila " + fila + " - Asiento " + numero + " [" + estado + "]";
    }
}
