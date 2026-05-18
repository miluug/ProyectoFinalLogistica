package org.example.model;

/**
 * Interfaz Observer base del sistema.
 * Implementada por AdminObserver y UsuarioObserver.
 * GestorEventos actúa como Subject (Observable).
 */
public interface EventoObserver {

    /**
     * Llamado cuando ocurre un cambio relevante en el sistema.
     * @param tipo  Tipo de notificación (ej. "EVENTO_CANCELADO", "COMPRA_CONFIRMADA")
     * @param mensaje Descripción del cambio para mostrar al usuario/admin
     */
    void actualizar(String tipo, String mensaje);
}
