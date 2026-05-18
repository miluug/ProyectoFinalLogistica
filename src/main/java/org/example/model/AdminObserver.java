package org.example.model;

import java.util.LinkedList;

/**
 * Observer del administrador.
 * Acumula notificaciones de todo el sistema (incidencias, cancelaciones, nuevas compras).
 * En MVC, el controlador de admin consultará esta lista para actualizar la vista.
 */
public class AdminObserver implements EventoObserver {

    private final Administrador administrador;
    private final LinkedList<String> notificaciones;

    public AdminObserver(Administrador administrador) {
        this.administrador = administrador;
        this.notificaciones = new LinkedList<>();
    }

    @Override
    public void actualizar(String tipo, String mensaje) {
        String notificacion = "[ADMIN][" + tipo + "] " + mensaje;
        notificaciones.add(notificacion);
        System.out.println(notificacion);
    }

    public LinkedList<String> getNotificaciones() {
        return notificaciones;
    }

    public Administrador getAdministrador() {
        return administrador;
    }
}
