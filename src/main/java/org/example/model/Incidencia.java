package org.example.model;
import java.time.LocalDate;

public class Incidencia {

    private String          idIncidencia;
    private String          tipo;
    private String          descripcion;
    private LocalDate       fecha;
    private EntidadAfectada entidadAfectada;

    public Incidencia() {}

    public Incidencia(String idIncidencia, String tipo, String descripcion,
                      LocalDate fecha, EntidadAfectada entidadAfectada) {
        this.idIncidencia    = idIncidencia;
        this.tipo            = tipo;
        this.descripcion     = descripcion;
        this.fecha           = fecha;
        this.entidadAfectada = entidadAfectada;
    }

    public String getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(String idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EntidadAfectada getEntidadAfectada() {
        return entidadAfectada;
    }

    public void setEntidadAfectada(EntidadAfectada entidadAfectada) {
        this.entidadAfectada = entidadAfectada;
    }
}