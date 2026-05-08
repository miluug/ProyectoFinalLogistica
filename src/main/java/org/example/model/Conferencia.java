package org.example.model;

import java.time.LocalDateTime;

public class Conferencia extends Evento {
    private String ponente;
    private String tematica;

    public Conferencia(String idEvento, String nombre, Categoria categoria, String descripcion, String ciudad, LocalDateTime fechaHora, EstadoEvento estado, String politicas, String ponente, String tematica) {
        super(idEvento, nombre, categoria, descripcion, ciudad, fechaHora, estado, politicas);
        this.ponente = ponente;
        this.tematica = tematica;
    }

    public String getPonente() {
        return ponente;
    }

    public void setPonente(String ponente) {
        this.ponente = ponente;
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }
}
