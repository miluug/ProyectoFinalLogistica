package org.example.model;

/**
 * Representa una entrada (ticket) generada para un evento.
 * Asociada obligatoriamente a una Zona para poder calcular precio.
 * Si la zona es numerada, también referencia un Asiento específico.
 */
public class Entrada {

    private String idEntrada;
    private EstadoEntrada estadoEntrada;
    private Zona zona;
    private Asiento asiento; // Puede ser null si la zona no es numerada

    /**
     * Constructor principal. El estado siempre inicia como ACTIVA.
     * La zona se asigna después mediante setZona() o directamente aquí.
     */
    public Entrada(String idEntrada, Zona zona) {
        this.idEntrada = idEntrada;
        this.zona = zona;
        this.estadoEntrada = EstadoEntrada.ACTIVA;
    }

    /**
     * Constructor para zonas numeradas: incluye el asiento reservado.
     */
    public Entrada(String idEntrada, Zona zona, Asiento asiento) {
        this(idEntrada, zona);
        this.asiento = asiento;
        if (asiento != null) {
            asiento.setEstado(EstadoAsiento.RESERVADO);
        }
    }

    /**
     * Retorna el precio base de la zona asociada.
     * Retorna 0 si no hay zona asignada (caso de error de datos).
     */
    public double calcularPrecioFinal() {
        if (zona != null) {
            return zona.getPrecioBase();
        }
        return 0;
    }

    // Getters y Setters

    public String getIdEntrada() { return idEntrada; }
    public void setIdEntrada(String idEntrada) { this.idEntrada = idEntrada; }

    public EstadoEntrada getEstadoEntrada() { return estadoEntrada; }
    public void setEstadoEntrada(EstadoEntrada estadoEntrada) { this.estadoEntrada = estadoEntrada; }

    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }

    public Asiento getAsiento() { return asiento; }
    public void setAsiento(Asiento asiento) { this.asiento = asiento; }
}
