package org.example.model;

import java.util.LinkedList;

/**
 * Observer del usuario final.
 * Recibe notificaciones relacionadas con sus compras y los eventos a los que asistirá
 * (confirmaciones, cambios de fecha, cancelaciones de evento, etc).
 */
public class UsuarioObserver implements EventoObserver {

    private final Usuario usuario;
    private final LinkedList<String> notificaciones;

    public UsuarioObserver(Usuario usuario) {
        this.usuario = usuario;
        this.notificaciones = new LinkedList<>();
    }

    @Override
    public void actualizar(String tipo, String mensaje) {
        String notificacion = "[" + usuario.getNombreCompleto() + "][" + tipo + "] " + mensaje;
        notificaciones.add(notificacion);
        System.out.println(notificacion);
    }

    public LinkedList<String> getNotificaciones() {
        return notificaciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
