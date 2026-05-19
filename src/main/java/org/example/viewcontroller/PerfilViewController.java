package org.example.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Compra;
import org.example.model.EstadoCompra;
import org.example.model.GestorEventos;
import org.example.model.Usuario;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PerfilViewController implements Initializable, SesionConUsuario {

    // ── FXML refs — avatar ────────────────────────────────
    @FXML private Label lblInicialAvatar;
    @FXML private Label lblNombreAvatar;
    @FXML private Label lblUsernameAvatar;

    // ── FXML refs — stats ─────────────────────────────────
    @FXML private Label lblStatCompras;
    @FXML private Label lblStatConfirmadas;
    @FXML private Label lblStatCanceladas;

    // ── FXML refs — datos personales ──────────────────────
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private Label     lblMensajeDatos;

    // ── FXML refs — contraseña ────────────────────────────
    @FXML private PasswordField txtContrasenaActual;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Label         lblMensajeContrasena;

    // ── Sesión ────────────────────────────────────────────
    private Usuario         usuarioActivo;
    private final GestorEventos gestor = GestorEventos.getInstance();

    // ── Inicialización FXML ───────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada que inicializar antes de recibir la sesión
    }

    // ── Inyección de sesión ───────────────────────────────

    @Override
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActivo = usuario;
        poblarVista();
    }

    // ── Poblar vista con datos del usuario ────────────────

    private void poblarVista() {
        Usuario u = usuarioActivo;

        // Avatar: inicial del nombre
        String inicial = u.getNombreCompleto() != null && !u.getNombreCompleto().isEmpty()
                ? String.valueOf(u.getNombreCompleto().charAt(0)).toUpperCase()
                : "?";
        lblInicialAvatar.setText(inicial);
        lblNombreAvatar.setText(u.getNombreCompleto());
        lblUsernameAvatar.setText("@" + u.getUsuario());

        // Campos del formulario
        txtNombre.setText(u.getNombreCompleto());
        txtCorreo.setText(u.getCorreoElectronico());
        txtTelefono.setText(u.getTelefono());

        // Stats de compras
        poblarStats();
    }

    private void poblarStats() {
        List<Compra> misCompras = gestor.getCompras().stream()
                .filter(c -> c.getUsuario() != null &&
                        c.getUsuario().getUsuario().equals(usuarioActivo.getUsuario()))
                .collect(Collectors.toList());

        long confirmadas = misCompras.stream()
                .filter(c -> c.getEstadoEnum() == EstadoCompra.CONFIRMADA)
                .count();

        long canceladas = misCompras.stream()
                .filter(c -> c.getEstadoEnum() == EstadoCompra.CANCELADA)
                .count();

        lblStatCompras.setText(misCompras.size() + " compras realizadas");
        lblStatConfirmadas.setText(confirmadas + " confirmadas");
        lblStatCanceladas.setText(canceladas + " canceladas");
    }

    // ── Acción: guardar datos personales ──────────────────

    @FXML
    private void guardarDatos() {
        ocultarMensaje(lblMensajeDatos);

        String nombre   = txtNombre.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mostrarMensaje(lblMensajeDatos, "Todos los campos son obligatorios.", false);
            return;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            mostrarMensaje(lblMensajeDatos, "El correo no es válido.", false);
            return;
        }

        // Actualizar modelo
        usuarioActivo.setNombreCompleto(nombre);
        usuarioActivo.setCorreoElectronico(correo);
        usuarioActivo.setTelefono(telefono);

        // Refrescar avatar con el nuevo nombre
        String inicial = nombre.isEmpty() ? "?"
                : String.valueOf(nombre.charAt(0)).toUpperCase();
        lblInicialAvatar.setText(inicial);
        lblNombreAvatar.setText(nombre);

        gestor.notificar("PERFIL_ACTUALIZADO",
                "Usuario " + usuarioActivo.getUsuario() + " actualizó su perfil.");

        mostrarMensaje(lblMensajeDatos, "✓ Datos actualizados correctamente.", true);
    }

    // ── Acción: cambiar contraseña ────────────────────────

    @FXML
    private void cambiarContrasena() {
        ocultarMensaje(lblMensajeContrasena);

        String actual    = txtContrasenaActual.getText();
        String nueva     = txtNuevaContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();

        // Validaciones
        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            mostrarMensaje(lblMensajeContrasena, "Completa todos los campos.", false);
            return;
        }
        if (!actual.equals(usuarioActivo.getContrasena())) {
            mostrarMensaje(lblMensajeContrasena, "La contraseña actual es incorrecta.", false);
            return;
        }
        if (nueva.length() < 4) {
            mostrarMensaje(lblMensajeContrasena, "La nueva contraseña debe tener mínimo 4 caracteres.", false);
            return;
        }
        if (!nueva.equals(confirmar)) {
            mostrarMensaje(lblMensajeContrasena, "Las contraseñas nuevas no coinciden.", false);
            return;
        }
        if (nueva.equals(actual)) {
            mostrarMensaje(lblMensajeContrasena, "La nueva contraseña debe ser diferente a la actual.", false);
            return;
        }

        // Actualizar contraseña en el modelo
        usuarioActivo.setContrasena(nueva);

        // Limpiar campos
        txtContrasenaActual.clear();
        txtNuevaContrasena.clear();
        txtConfirmarContrasena.clear();

        mostrarMensaje(lblMensajeContrasena, "✓ Contraseña actualizada correctamente.", true);
    }

    // ── Navegación ────────────────────────────────────────

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/UsuarioView.fxml")
            );
            Scene scene = new Scene(loader.load());
            SesionConUsuario ctrl = loader.getController();
            ctrl.iniciarSesion(usuarioActivo);
            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irMisCompras() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/MisComprasView.fxml")
            );
            Scene scene = new Scene(loader.load());
            SesionConUsuario ctrl = loader.getController();
            ctrl.iniciarSesion(usuarioActivo);
            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helpers de UI ─────────────────────────────────────

    private void mostrarMensaje(Label lbl, String texto, boolean exito) {
        lbl.setText(texto);
        lbl.setStyle(
            "-fx-font-size: 12px; -fx-text-fill: " +
            (exito ? "#4CAF50" : "#E05555") + ";"
        );
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private void ocultarMensaje(Label lbl) {
        lbl.setVisible(false);
        lbl.setManaged(false);
    }

    private Stage getStage() {
        return (Stage) txtNombre.getScene().getWindow();
    }
}
