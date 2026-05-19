package org.example.viewcontroller;

import org.example.model.Usuario;

/**
 * Contrato para controllers que solo reciben el usuario activo al navegar.
 */
public interface SesionConUsuario {
    void iniciarSesion(Usuario usuario);
}
