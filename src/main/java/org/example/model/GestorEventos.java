package org.example.model;

public class GestorEventos {

    private static GestorEventos instance;

    private GestorEventos() {
        // Constructor privado para evitar instanciación externa
    }

    public static GestorEventos getInstance() {
        if (instance == null) {
            instance = new GestorEventos();
        }
        return instance;
    }
}
