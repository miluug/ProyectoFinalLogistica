package org.example.viewcontroller;

import org.example.model.Evento;
import org.example.model.Usuario;

/**
 * Contrato para controllers que reciben usuario + evento al navegar.
 * Permite que UsuarioViewController navegue sin conocer la clase concreta.
 */
public interface SesionConEvento {
    void iniciarSesion(Usuario usuario, Evento evento);
}
