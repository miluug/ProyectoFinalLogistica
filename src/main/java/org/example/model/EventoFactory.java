package org.example.model;

import java.time.LocalDateTime;

public class EventoFactory {

    /* extra1 y extra2
    en concierto e1: artista y e2: generoMusical
    en conferencia e1: ponente y e2: tematica
    en obra teatro e1: director y e2: genero
     */

    public Evento crearEvento(Categoria categoriaEvento, String idEvento, String nombre, Categoria categoria, String descripcion, String ciudad, LocalDateTime fechaHora, EstadoEvento estado, String politicas, String extra1, String extra2) {

        switch (categoriaEvento){
            case CONCIERTO:
                return new Concierto(idEvento, nombre, categoria, descripcion, ciudad, fechaHora, estado, politicas, extra1, extra2);
            case CONFERENCIA:
                return new Conferencia(idEvento, nombre, categoria, descripcion, ciudad, fechaHora, estado, politicas, extra1, extra2);
            case OBRA_TEATRO:
                return new ObraTeatro(idEvento, nombre, categoria, descripcion, ciudad, fechaHora, estado, politicas, extra1, extra2);
            default:
                return null;
        }
    }
}