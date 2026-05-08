package org.example.model;

import java.time.LocalDateTime;

public class ObraTeatro extends Evento {

    private String director;
    private String genero;

    public ObraTeatro(String idEvento, String nombre, Categoria categoria, String descripcion, String ciudad, LocalDateTime fechaHora, EstadoEvento estado, String politicas, String director, String genero) {
        super(idEvento, nombre, categoria, descripcion, ciudad, fechaHora, estado, politicas);
        this.director = director;
        this.genero = genero;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
