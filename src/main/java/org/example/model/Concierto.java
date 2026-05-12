package org.example.model;
import java.time.LocalDateTime;

public class Concierto extends Evento {
    private String artista;
    private String generoMusical;

    public Concierto(String idEvento, String nombre, Categoria categoria, String descripcion, String ciudad, LocalDateTime fechaHora, EstadoEvento estado, String politicas, String artista, String generoMusical) {
        super(idEvento, nombre, categoria, descripcion, ciudad, fechaHora, estado, politicas);
        this.artista = artista;
        this.generoMusical = generoMusical;
    }

    //Getters y Setters

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getGeneroMusical() {
        return generoMusical;
    }

    public void setGeneroMusical(String generoMusical) {
        this.generoMusical = generoMusical;
    }
}
