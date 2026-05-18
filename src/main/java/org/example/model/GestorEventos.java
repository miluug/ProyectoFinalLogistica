package org.example.model;

import java.util.LinkedList;

/**
 * Singleton y Subject (Observable) del sistema.
 *
 * Responsabilidades:
 * - Gestión centralizada de eventos, usuarios, recintos y compras
 * - Notificación a observers cuando ocurren cambios relevantes
 *
 * En MVC, los controladores obtienen esta instancia para leer/modificar datos
 * y disparar notificaciones al modelo.
 */
public class GestorEventos {

    private static GestorEventos instance;

    private final LinkedList<Administrador> administradores;
    private final LinkedList<Evento> eventos;
    private final LinkedList<Usuario> usuarios;
    private final LinkedList<Recinto> recintos;
    private final LinkedList<Compra> compras;
    private final LinkedList<Incidencia> incidencias;

    // Observers registrados
    private final LinkedList<EventoObserver> observers;

    private GestorEventos() {
        administradores = new LinkedList<>();
        eventos = new LinkedList<>();
        usuarios = new LinkedList<>();
        recintos = new LinkedList<>();
        compras = new LinkedList<>();
        incidencias = new LinkedList<>();
        observers = new LinkedList<>();
    }

    public static GestorEventos getInstance() {
        if (instance == null) {
            instance = new GestorEventos();
        }
        return instance;
    }

    // -------------------------
    // Observer
    // -------------------------

    public void agregarObserver(EventoObserver observer) {
        observers.add(observer);
    }

    public void eliminarObserver(EventoObserver observer) {
        observers.remove(observer);
    }

    /** Notifica a todos los observers registrados. */
    public void notificar(String tipo, String mensaje) {
        for (EventoObserver obs : observers) {
            obs.actualizar(tipo, mensaje);
        }
    }

    // -------------------------
    // Gestión de Administradores
    // -------------------------


    public void agregarAdministrador(Administrador admin) {
        administradores.add(admin);
    }

    public Administrador buscarAdministrador(String usuario) {
        return administradores.stream()
                .filter(a -> a.getUsuario().equals(usuario))
                .findFirst().orElse(null);
    }


    // -------------------------
    // Gestión de Eventos
    // -------------------------

    public void agregarEvento(Evento evento) {
        eventos.add(evento);
        notificar("EVENTO_CREADO", "Nuevo evento registrado: " + evento.getNombre());
    }

    public void cancelarEvento(String idEvento) {
        Evento evento = buscarEvento(idEvento);
        if (evento != null) {
            evento.setEstado(EstadoEvento.CANCELADO);
            notificar("EVENTO_CANCELADO", "El evento '" + evento.getNombre() + "' fue cancelado.");
        }
    }

    public Evento buscarEvento(String idEvento) {
        return eventos.stream()
                .filter(e -> e.getIdEvento().equals(idEvento))
                .findFirst()
                .orElse(null);
    }

    public LinkedList<Evento> getEventos() { return eventos; }

    // -------------------------
    // Gestión de Usuarios
    // -------------------------

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public Usuario buscarUsuario(String nombreUsuario) {
        return usuarios.stream()
                .filter(u -> u.getUsuario().equals(nombreUsuario))
                .findFirst()
                .orElse(null);
    }

    public LinkedList<Usuario> getUsuarios() { return usuarios; }

    // -------------------------
    // Gestión de Recintos
    // -------------------------

    public void agregarRecinto(Recinto recinto) {
        recintos.add(recinto);
    }

    public LinkedList<Recinto> getRecintos() { return recintos; }

    // -------------------------
    // Gestión de Compras
    // -------------------------

    public void registrarCompra(Compra compra) {
        compras.add(compra);
        notificar("COMPRA_REGISTRADA", "Nueva compra: " + compra.getIdCompra()
                + " - Usuario: " + compra.getUsuario().getNombreCompleto());
    }

    public Compra buscarCompra(String idCompra) {
        return compras.stream()
                .filter(c -> c.getIdCompra().equals(idCompra))
                .findFirst()
                .orElse(null);
    }

    public LinkedList<Compra> getCompras() { return compras; }

    // -------------------------
    // Gestión de Incidencias
    // -------------------------

    public void registrarIncidencia(Incidencia incidencia) {
        incidencias.add(incidencia);
        notificar("INCIDENCIA", "Incidencia registrada: " + incidencia.getDescripcion());
    }

    public LinkedList<Incidencia> getIncidencias() { return incidencias; }
}
