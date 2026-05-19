package org.example.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UsuarioViewController implements Initializable {

    // ── FXML refs ─────────────────────────────────────────
    @FXML private Label     lblNombre;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbCiudad;
    @FXML private FlowPane  panelEventos;

    // ── Sesión activa ─────────────────────────────────────
    private Usuario usuarioActivo;
    private final GestorEventos gestor = GestorEventos.getInstance();

    // ── Inicialización FXML ───────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Poblar combos con valores base
        cbCategoria.getItems().addAll("Todas", "CONCIERTO", "OBRA_TEATRO", "CONFERENCIA");
        cbCategoria.setValue("Todas");

        cbCiudad.getItems().add("Todas las ciudades");
        cbCiudad.setValue("Todas las ciudades");
    }

    /**
     * Llamado desde el controller anterior para inyectar la sesión.
     * Dispara la carga inicial de eventos.
     */
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActivo = usuario;
        lblNombre.setText(usuario.getNombreCompleto());

        // Poblar ciudades dinámicamente desde los eventos existentes
        gestor.getEventos().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .map(Evento::getCiudad)
                .distinct()
                .forEach(ciudad -> {
                    if (!cbCiudad.getItems().contains(ciudad))
                        cbCiudad.getItems().add(ciudad);
                });

        cargarEventos();
    }

    // ── Carga y filtrado de eventos ───────────────────────

    private void cargarEventos() {
        List<Evento> publicados = gestor.getEventos().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .collect(Collectors.toList());
        renderizarEventos(publicados);
    }

    @FXML
    private void aplicarFiltros() {
        String busqueda  = txtBuscar.getText().trim().toLowerCase();
        String categoria = cbCategoria.getValue();
        String ciudad    = cbCiudad.getValue();

        List<Evento> filtrados = gestor.getEventos().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .filter(e -> busqueda.isEmpty()
                        || e.getNombre().toLowerCase().contains(busqueda)
                        || e.getDescripcion().toLowerCase().contains(busqueda))
                .filter(e -> "Todas".equals(categoria)
                        || e.getCategoria().name().equals(categoria))
                .filter(e -> "Todas las ciudades".equals(ciudad)
                        || e.getCiudad().equals(ciudad))
                .collect(Collectors.toList());

        renderizarEventos(filtrados);
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cbCategoria.setValue("Todas");
        cbCiudad.setValue("Todas las ciudades");
        cargarEventos();
    }

    private void renderizarEventos(List<Evento> eventos) {
        panelEventos.getChildren().clear();

        if (eventos.isEmpty()) {
            Label sinResultados = new Label("No se encontraron eventos con esos filtros.");
            sinResultados.setStyle(
                "-fx-text-fill: #444; -fx-font-size: 15px; -fx-font-family: 'Georgia';"
            );
            panelEventos.getChildren().add(sinResultados);
            return;
        }

        for (Evento evento : eventos) {
            panelEventos.getChildren().add(crearTarjetaEvento(evento));
        }
    }

    // ── Construcción de tarjeta de evento ─────────────────

    private VBox crearTarjetaEvento(Evento evento) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(18, 18, 18, 18));
        card.setStyle(
            "-fx-background-color: #161616;" +
            "-fx-border-color: #252525;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );

        // Badge categoría
        Label badge = new Label(evento.getCategoria().name());
        badge.setStyle(
            "-fx-background-color: " + colorCategoria(evento.getCategoria()) + "22;" +
            "-fx-text-fill: " + colorCategoria(evento.getCategoria()) + ";" +
            "-fx-font-size: 10px; -fx-font-weight: bold;" +
            "-fx-padding: 3 8; -fx-background-radius: 4;"
        );

        // Nombre
        Label nombre = new Label(evento.getNombre());
        nombre.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 16px;" +
            "-fx-font-weight: bold; -fx-text-fill: #EEEEEE;" +
            "-fx-wrap-text: true;"
        );
        nombre.setMaxWidth(264);

        // Ciudad
        Label ciudad = new Label("📍 " + evento.getCiudad());
        ciudad.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        // Fecha
        Label fecha = new Label("📅 " + evento.getFechaHora().toLocalDate().toString());
        fecha.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        // Descripción recortada
        String descTexto = evento.getDescripcion() != null
                ? evento.getDescripcion() : "";
        if (descTexto.length() > 75) descTexto = descTexto.substring(0, 75) + "...";
        Label descripcion = new Label(descTexto);
        descripcion.setStyle("-fx-text-fill: #555; -fx-font-size: 12px; -fx-wrap-text: true;");
        descripcion.setMaxWidth(264);

        // Precio mínimo (primer zona disponible)
        String precioLabel = obtenerPrecioMinimo(evento);
        Label precio = new Label(precioLabel);
        precio.setStyle(
            "-fx-text-fill: #E8C547; -fx-font-size: 13px; -fx-font-weight: bold;"
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Botones
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button btnComprar  = new Button("Comprar");
        Button btnDetalles = new Button("Ver Detalles");

        btnComprar.setStyle(
            "-fx-background-color: #E8C547; -fx-text-fill: #0D0D0D;" +
            "-fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;" +
            "-fx-background-radius: 6;"
        );
        btnDetalles.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #777;" +
            "-fx-border-color: #2E2E2E; -fx-border-width: 1;" +
            "-fx-padding: 8 14; -fx-cursor: hand;" +
            "-fx-border-radius: 6; -fx-background-radius: 6;"
        );

        btnComprar.setOnAction(e  -> abrirCompra(evento));
        btnDetalles.setOnAction(e -> abrirDetalle(evento));

        botones.getChildren().addAll(btnComprar, btnDetalles);

        card.getChildren().addAll(badge, nombre, ciudad, fecha, descripcion, precio, spacer, botones);

        // Hover
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #1A1A1A;" +
            "-fx-border-color: #E8C54744;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: #161616;" +
            "-fx-border-color: #252525;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        ));

        return card;
    }

    // ── Acciones de navegación ────────────────────────────

    private void abrirDetalle(Evento evento) {
        navegarConEvento("/DetalleEventoView.fxml", evento);
    }

    private void abrirCompra(Evento evento) {
        navegarConEvento("/CompraView.fxml", evento);
    }

    @FXML
    private void abrirMisCompras() {
        navegarSinDatos("/MisComprasView.fxml");
    }

    @FXML
    private void abrirPerfil() {
        navegarSinDatos("/PerfilView.fxml");
    }

    @FXML
    private void recargarPantalla() {
        limpiarFiltros();
    }

    @FXML
    private void cerrarSesion() {
        cargarVista("/LoginView.fxml");
    }

    // ── Helpers de navegación ─────────────────────────────

    /**
     * Navega a una vista que necesita tanto usuario como evento.
     */
    private void navegarConEvento(String fxmlPath, Evento evento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            // Todos los controllers de estas vistas implementan iniciarSesion(Usuario, Evento)
            SesionConEvento ctrl = loader.getController();
            ctrl.iniciarSesion(usuarioActivo, evento);

            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navega a una vista que solo necesita el usuario activo.
     */
    private void navegarSinDatos(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            SesionConUsuario ctrl = loader.getController();
            ctrl.iniciarSesion(usuarioActivo);

            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helpers de UI ─────────────────────────────────────

    private String colorCategoria(Categoria cat) {
        return switch (cat) {
            case CONCIERTO   -> "#E8C547";
            case OBRA_TEATRO -> "#7EC8E3";
            case CONFERENCIA -> "#A8E6A3";
        };
    }

    private String obtenerPrecioMinimo(Evento evento) {
        if (evento.getRecinto() == null || evento.getRecinto().getZonas().isEmpty())
            return "Consultar precio";

        double minimo = evento.getRecinto().getZonas().stream()
                .mapToDouble(Zona::getPrecioBase)
                .min()
                .orElse(0);

        return "Desde $" + String.format("%,.0f", minimo);
    }

    private Stage getStage() {
        return (Stage) panelEventos.getScene().getWindow();
    }
}
