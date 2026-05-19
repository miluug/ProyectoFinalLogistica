package org.example.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminViewController implements Initializable {

    // ── FXML refs — top ───────────────────────────────────
    @FXML private Label lblAdminNombre;

    // ── FXML refs — nav botones ───────────────────────────
    @FXML private Button btnNavUsuarios;
    @FXML private Button btnNavEventos;
    @FXML private Button btnNavIncidencias;
    @FXML private Button btnNavMetricas;

    // ── FXML refs — paneles ───────────────────────────────
    @FXML private VBox panelUsuarios;
    @FXML private VBox panelEventos;
    @FXML private VBox panelIncidencias;
    @FXML private VBox panelMetricas;

    // ── FXML refs — usuarios ──────────────────────────────
    @FXML private TextField txtBuscarUsuario;
    @FXML private VBox      listaUsuarios;

    // ── FXML refs — eventos ───────────────────────────────
    @FXML private ComboBox<String> cbFiltroEstadoEvento;
    @FXML private VBox             listaEventos;

    // ── FXML refs — incidencias ───────────────────────────
    @FXML private ComboBox<String> cbTipoIncidencia;
    @FXML private ComboBox<String> cbEntidadAfectada;
    @FXML private TextArea         txtDescripcionIncidencia;
    @FXML private Label            lblMensajeIncidencia;
    @FXML private VBox             listaIncidencias;

    // ── FXML refs — métricas ──────────────────────────────
    @FXML private HBox              filaKPIs;
    @FXML private BarChart<String, Number>  graficaVentas;
    @FXML private PieChart                  graficaEstados;
    @FXML private LineChart<String, Number> graficaIngresos;

    // ── Sesión y estado ───────────────────────────────────
    private Administrador adminActivo;
    private final GestorEventos gestor = GestorEventos.getInstance();

    // ── Inicialización FXML ───────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFiltroEstadoEvento.getItems().addAll(
                "Todos", "BORRADO", "PUBLICADO", "PAUSADO", "CANCELADO", "FINALIZADO"
        );
        cbFiltroEstadoEvento.setValue("Todos");

        cbTipoIncidencia.getItems().addAll(
                "Error de pago", "Doble compra", "Cancelación masiva",
                "Error de sistema", "Reembolso pendiente", "Otro"
        );
        cbTipoIncidencia.setValue("Error de pago");

        cbEntidadAfectada.getItems().addAll(
                EntidadAfectada.EVENTO.name(),
                EntidadAfectada.COMPRA.name(),
                EntidadAfectada.USUARIO.name()
        );
        cbEntidadAfectada.setValue(EntidadAfectada.COMPRA.name());
    }

    // ── Inyección de sesión ───────────────────────────────

    public void iniciarSesion(Administrador admin) {
        this.adminActivo = admin;
        lblAdminNombre.setText("Admin: " + admin.getUsuario());
        mostrarUsuarios();
    }

    // ── Navegación entre paneles ──────────────────────────

    @FXML
    private void mostrarUsuarios() {
        activarPanel(panelUsuarios);
        marcarNavActivo(btnNavUsuarios);
        cargarUsuarios();
    }

    @FXML
    private void mostrarEventos() {
        activarPanel(panelEventos);
        marcarNavActivo(btnNavEventos);
        cargarEventos();
    }

    @FXML
    private void mostrarIncidencias() {
        activarPanel(panelIncidencias);
        marcarNavActivo(btnNavIncidencias);
        cargarIncidencias();
    }

    @FXML
    private void mostrarMetricas() {
        activarPanel(panelMetricas);
        marcarNavActivo(btnNavMetricas);
        cargarMetricas();
    }

    private void activarPanel(VBox panelActivo) {
        List<VBox> todos = List.of(panelUsuarios, panelEventos, panelIncidencias, panelMetricas);
        for (VBox panel : todos) {
            boolean activo = panel == panelActivo;
            panel.setVisible(activo);
            panel.setManaged(activo);
        }
    }

    private void marcarNavActivo(Button btnActivo) {
        List<Button> todos = List.of(btnNavUsuarios, btnNavEventos, btnNavIncidencias, btnNavMetricas);
        for (Button btn : todos) {
            if (btn == btnActivo) {
                btn.setStyle(
                        "-fx-background-color: #E8C54718; -fx-text-fill: #E8C547;" +
                                "-fx-font-size: 13px; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;" +
                                "-fx-padding: 11 16; -fx-background-radius: 6; -fx-border-color: transparent;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #666;" +
                                "-fx-font-size: 13px; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;" +
                                "-fx-padding: 11 16; -fx-background-radius: 6; -fx-border-color: transparent;"
                );
            }
        }
    }

    // ════════════════════════════════════════════════════════
    // PANEL USUARIOS
    // ════════════════════════════════════════════════════════

    private void cargarUsuarios() {
        renderizarUsuarios(gestor.getUsuarios());
    }

    @FXML
    private void buscarUsuario() {
        String texto = txtBuscarUsuario.getText().trim().toLowerCase();
        List<Usuario> filtrados = gestor.getUsuarios().stream()
                .filter(u -> u.getNombreCompleto().toLowerCase().contains(texto)
                        || u.getUsuario().toLowerCase().contains(texto)
                        || u.getCorreoElectronico().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        renderizarUsuarios(filtrados);
    }

    private void renderizarUsuarios(List<Usuario> usuarios) {
        listaUsuarios.getChildren().clear();

        if (usuarios.isEmpty()) {
            Label lbl = new Label("No se encontraron usuarios.");
            lbl.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");
            listaUsuarios.getChildren().add(lbl);
            return;
        }

        for (Usuario u : usuarios) {
            listaUsuarios.getChildren().add(crearFilaUsuario(u));
        }
    }

    private HBox crearFilaUsuario(Usuario usuario) {
        HBox fila = new HBox(16);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 18, 14, 18));
        fila.setStyle(
                "-fx-background-color: #161616;" +
                        "-fx-border-color: #1E1E1E; -fx-border-width: 1;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        // Inicial avatar
        StackPane avatar = new StackPane();
        avatar.setPrefSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setStyle(
                "-fx-background-color: #E8C54722;" +
                        "-fx-border-color: #E8C547; -fx-border-width: 1;" +
                        "-fx-border-radius: 20; -fx-background-radius: 20;"
        );
        Label inicial = new Label(
                String.valueOf(usuario.getNombreCompleto().charAt(0)).toUpperCase()
        );
        inicial.setStyle("-fx-text-fill: #E8C547; -fx-font-size: 16px; -fx-font-weight: bold;");
        avatar.getChildren().add(inicial);

        // Info usuario
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblNombre = new Label(usuario.getNombreCompleto());
        lblNombre.setStyle("-fx-text-fill: #DDD; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label lblDetalle = new Label(
                "@" + usuario.getUsuario() + "  ·  " + usuario.getCorreoElectronico() +
                        "  ·  " + usuario.getTelefono()
        );
        lblDetalle.setStyle("-fx-text-fill: #555; -fx-font-size: 11px;");

        // Contar compras de este usuario
        long comprasCount = gestor.getCompras().stream()
                .filter(c -> c.getUsuario() != null &&
                        c.getUsuario().getUsuario().equals(usuario.getUsuario()))
                .count();
        Label lblCompras = new Label(comprasCount + " compras");
        lblCompras.setStyle("-fx-text-fill: #E8C547; -fx-font-size: 11px;");

        info.getChildren().addAll(lblNombre, lblDetalle, lblCompras);

        // Botones
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle(
                "-fx-background-color: #E0555522; -fx-text-fill: #E05555;" +
                        "-fx-border-color: #E05555; -fx-border-width: 1;" +
                        "-fx-border-radius: 6; -fx-background-radius: 6;" +
                        "-fx-font-size: 11px; -fx-padding: 6 12; -fx-cursor: hand;"
        );
        btnEliminar.setOnAction(e -> eliminarUsuario(usuario));

        fila.getChildren().addAll(avatar, info, btnEliminar);
        return fila;
    }

    @FXML
    private void nuevoUsuario() {
        abrirDialogoNuevoUsuario();
    }

    private void abrirDialogoNuevoUsuario() {
        Stage dialog = new Stage();
        dialog.setTitle("Nuevo Usuario");

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #161616;");
        root.setPrefWidth(380);

        Label titulo = new Label("Crear Usuario");
        titulo.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E8C547;");

        TextField fNombre    = campo("Nombre completo");
        TextField fCorreo    = campo("Correo electrónico");
        TextField fTelefono  = campo("Teléfono");
        TextField fUsuario   = campo("Nombre de usuario");
        PasswordField fClave = new PasswordField();
        fClave.setPromptText("Contraseña");
        aplicarEstilo(fClave);

        Label lblErr = new Label("");
        lblErr.setStyle("-fx-text-fill: #E05555; -fx-font-size: 12px;");
        lblErr.setVisible(false);

        Button btnGuardar = new Button("Crear Usuario");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setStyle(
                "-fx-background-color: #E8C547; -fx-text-fill: #0D0D0D;" +
                        "-fx-font-weight: bold; -fx-padding: 12 0; -fx-cursor: hand; -fx-background-radius: 6;"
        );
        btnGuardar.setOnAction(e -> {
            String nombre   = fNombre.getText().trim();
            String correo   = fCorreo.getText().trim();
            String telefono = fTelefono.getText().trim();
            String user     = fUsuario.getText().trim();
            String clave    = fClave.getText();

            if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()
                    || user.isEmpty() || clave.isEmpty()) {
                lblErr.setText("Todos los campos son obligatorios.");
                lblErr.setVisible(true);
                return;
            }
            if (gestor.buscarUsuario(user) != null) {
                lblErr.setText("El usuario '" + user + "' ya existe.");
                lblErr.setVisible(true);
                return;
            }

            Usuario nuevo = new Usuario(nombre, correo, telefono, user, clave);
            gestor.agregarUsuario(nuevo);
            gestor.agregarObserver(new UsuarioObserver(nuevo));
            dialog.close();
            cargarUsuarios();
        });

        root.getChildren().addAll(
                titulo, new Separator(),
                etiqueta("NOMBRE"), fNombre,
                etiqueta("CORREO"), fCorreo,
                etiqueta("TELÉFONO"), fTelefono,
                etiqueta("USUARIO"), fUsuario,
                etiqueta("CONTRASEÑA"), fClave,
                lblErr, btnGuardar
        );

        Scene sc = new Scene(root);
        dialog.setScene(sc);
        dialog.show();
    }

    private void eliminarUsuario(Usuario usuario) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar Usuario");
        confirm.setHeaderText("¿Eliminar a " + usuario.getNombreCompleto() + "?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                gestor.getUsuarios().remove(usuario);
                gestor.notificar("USUARIO_ELIMINADO",
                        "Usuario " + usuario.getUsuario() + " eliminado por el admin.");
                cargarUsuarios();
            }
        });
    }

    // ════════════════════════════════════════════════════════
    // PANEL EVENTOS
    // ════════════════════════════════════════════════════════

    private void cargarEventos() {
        renderizarEventos(new ArrayList<>(gestor.getEventos()));
    }

    @FXML
    private void filtrarEventos() {
        String filtro = cbFiltroEstadoEvento.getValue();
        List<Evento> filtrados = gestor.getEventos().stream()
                .filter(e -> "Todos".equals(filtro) || e.getEstado().name().equals(filtro))
                .collect(Collectors.toList());
        renderizarEventos(filtrados);
    }

    private void renderizarEventos(List<Evento> eventos) {
        listaEventos.getChildren().clear();

        if (eventos.isEmpty()) {
            Label lbl = new Label("No hay eventos.");
            lbl.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");
            listaEventos.getChildren().add(lbl);
            return;
        }

        for (Evento ev : eventos) {
            listaEventos.getChildren().add(crearFilaEvento(ev));
        }
    }

    private VBox crearFilaEvento(Evento evento) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle(
                "-fx-background-color: #161616;" +
                        "-fx-border-color: #1E1E1E; -fx-border-width: 1;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        // Fila superior
        HBox fila1 = new HBox(12);
        fila1.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label(evento.getCategoria().name());
        badge.setStyle(
                "-fx-background-color: #E8C54722; -fx-text-fill: #E8C547;" +
                        "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-padding: 3 8; -fx-background-radius: 4;"
        );

        Label nombre = new Label(evento.getNombre());
        nombre.setStyle("-fx-text-fill: #DDD; -fx-font-size: 14px; -fx-font-weight: bold;");
        HBox.setHgrow(nombre, Priority.ALWAYS);

        Label badgeEstado = new Label(evento.getEstado().name());
        String colorEst = switch (evento.getEstado()) {
            case PUBLICADO  -> "#4CAF50";
            case PAUSADO    -> "#FF9800";
            case CANCELADO  -> "#E05555";
            case FINALIZADO -> "#888888";
            case BORRADO    -> "#555555";
        };
        badgeEstado.setStyle(
                "-fx-background-color: " + colorEst + "22;" +
                        "-fx-text-fill: " + colorEst + ";" +
                        "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-padding: 3 8; -fx-background-radius: 4;"
        );

        fila1.getChildren().addAll(badge, nombre, badgeEstado);

        // Fila info
        Label info = new Label(
                "📍 " + evento.getCiudad() +
                        "   📅 " + evento.getFechaHora().toLocalDate() +
                        "   🏟 " + (evento.getRecinto() != null ? evento.getRecinto().getNombre() : "Sin recinto")
        );
        info.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

        // Botones de estado
        HBox botones = new HBox(8);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button btnPublicar  = botonEstado("Publicar",  "#4CAF50");
        Button btnPausar    = botonEstado("Pausar",    "#FF9800");
        Button btnCancelar  = botonEstado("Cancelar",  "#E05555");
        Button btnFinalizar = botonEstado("Finalizar", "#888888");

        btnPublicar.setOnAction(e  -> cambiarEstadoEvento(evento, EstadoEvento.PUBLICADO));
        btnPausar.setOnAction(e    -> cambiarEstadoEvento(evento, EstadoEvento.PAUSADO));
        btnCancelar.setOnAction(e  -> cambiarEstadoEvento(evento, EstadoEvento.CANCELADO));
        btnFinalizar.setOnAction(e -> cambiarEstadoEvento(evento, EstadoEvento.FINALIZADO));

        botones.getChildren().addAll(btnPublicar, btnPausar, btnCancelar, btnFinalizar);

        card.getChildren().addAll(fila1, info, botones);
        return card;
    }

    private void cambiarEstadoEvento(Evento evento, EstadoEvento nuevoEstado) {
        evento.setEstado(nuevoEstado);
        gestor.notificar("EVENTO_" + nuevoEstado.name(),
                "Evento '" + evento.getNombre() + "' → " + nuevoEstado.name());
        cargarEventos();
    }

    @FXML
    private void nuevoEvento() {
        abrirDialogoNuevoEvento();
    }

    private void abrirDialogoNuevoEvento() {
        Stage dialog = new Stage();
        dialog.setTitle("Nuevo Evento");

        VBox root = new VBox(12);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #161616;");
        root.setPrefWidth(440);

        Label titulo = new Label("Crear Evento");
        titulo.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E8C547;");

        TextField fNombre = campo("Nombre del evento");
        TextField fCiudad = campo("Ciudad");
        TextArea  fDesc   = new TextArea();
        fDesc.setPromptText("Descripción");
        fDesc.setPrefRowCount(3);
        fDesc.setStyle("-fx-control-inner-background: #1A1A1A; -fx-text-fill: #EEE; -fx-prompt-text-fill: #444; -fx-font-size: 13px;");

        ComboBox<String> cbCat = new ComboBox<>();
        cbCat.getItems().addAll("CONCIERTO", "OBRA_TEATRO", "CONFERENCIA");
        cbCat.setValue("CONCIERTO");
        cbCat.setMaxWidth(Double.MAX_VALUE);

        TextField fExtra1 = campo("Artista / Ponente / Director");
        TextField fExtra2 = campo("Género / Temática");

        ComboBox<Recinto> cbRecinto = new ComboBox<>();
        cbRecinto.getItems().addAll(gestor.getRecintos());
        cbRecinto.setMaxWidth(Double.MAX_VALUE);
        if (!gestor.getRecintos().isEmpty())
            cbRecinto.setValue(gestor.getRecintos().getFirst());

        Label lblErr = new Label("");
        lblErr.setStyle("-fx-text-fill: #E05555; -fx-font-size: 12px;");
        lblErr.setVisible(false);

        Button btnGuardar = new Button("Crear Evento");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setStyle("-fx-background-color: #E8C547; -fx-text-fill: #0D0D0D; -fx-font-weight: bold; -fx-padding: 12 0; -fx-cursor: hand; -fx-background-radius: 6;");
        btnGuardar.setOnAction(e -> {
            String nombre = fNombre.getText().trim();
            String ciudad = fCiudad.getText().trim();
            String desc   = fDesc.getText().trim();
            String cat    = cbCat.getValue();
            String e1     = fExtra1.getText().trim();
            String e2     = fExtra2.getText().trim();

            if (nombre.isEmpty() || ciudad.isEmpty()) {
                lblErr.setText("Nombre y ciudad son obligatorios.");
                lblErr.setVisible(true);
                return;
            }

            EventoFactory factory = new EventoFactory();
            String idEvento = "EV-" + System.currentTimeMillis();
            Evento nuevo = factory.crearEvento(
                    Categoria.valueOf(cat), idEvento, nombre,
                    Categoria.valueOf(cat), desc.isEmpty() ? "Sin descripción" : desc,
                    ciudad, java.time.LocalDateTime.now().plusMonths(3),
                    EstadoEvento.BORRADO, "Sin políticas.", e1, e2
            );

            if (cbRecinto.getValue() != null)
                nuevo.setRecinto(cbRecinto.getValue());

            gestor.agregarEvento(nuevo);
            dialog.close();
            cargarEventos();
        });

        root.getChildren().addAll(
                titulo, new Separator(),
                etiqueta("NOMBRE"), fNombre,
                etiqueta("CIUDAD"), fCiudad,
                etiqueta("DESCRIPCIÓN"), fDesc,
                etiqueta("CATEGORÍA"), cbCat,
                etiqueta("ARTISTA / PONENTE / DIRECTOR"), fExtra1,
                etiqueta("GÉNERO / TEMÁTICA"), fExtra2,
                etiqueta("RECINTO"), cbRecinto,
                lblErr, btnGuardar
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #161616; -fx-background-color: #161616;");

        Scene sc = new Scene(scroll, 460, 580);
        dialog.setScene(sc);
        dialog.show();
    }

    // ════════════════════════════════════════════════════════
    // PANEL INCIDENCIAS
    // ════════════════════════════════════════════════════════

    private void cargarIncidencias() {
        listaIncidencias.getChildren().clear();

        List<Incidencia> incidencias = gestor.getIncidencias();

        if (incidencias.isEmpty()) {
            Label lbl = new Label("No hay incidencias registradas.");
            lbl.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");
            listaIncidencias.getChildren().add(lbl);
            return;
        }

        for (Incidencia inc : incidencias) {
            listaIncidencias.getChildren().add(crearFilaIncidencia(inc));
        }
    }

    @FXML
    private void registrarIncidencia() {
        ocultarMensajeInc();

        String tipo    = cbTipoIncidencia.getValue();
        String entidad = cbEntidadAfectada.getValue();
        String desc    = txtDescripcionIncidencia.getText().trim();

        if (desc.isEmpty()) {
            mostrarMensajeInc("La descripción es obligatoria.", false);
            return;
        }

        String idInc = "INC-" + System.currentTimeMillis();
        Incidencia nueva = new Incidencia(
                idInc, tipo, desc,
                LocalDate.now(),
                EntidadAfectada.valueOf(entidad)
        );

        gestor.registrarIncidencia(nueva);

        txtDescripcionIncidencia.clear();
        mostrarMensajeInc("✓ Incidencia registrada correctamente.", true);
        cargarIncidencias();
    }

    private HBox crearFilaIncidencia(Incidencia inc) {
        HBox fila = new HBox(14);
        fila.setAlignment(Pos.TOP_LEFT);
        fila.setPadding(new Insets(12, 16, 12, 16));
        fila.setStyle(
                "-fx-background-color: #161616;" +
                        "-fx-border-color: #1E1E1E; -fx-border-width: 1;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        // Badge entidad
        Label badgeEntidad = new Label(inc.getEntidadAfectada().name());
        badgeEntidad.setStyle(
                "-fx-background-color: #FF980022; -fx-text-fill: #FF9800;" +
                        "-fx-font-size: 10px; -fx-font-weight: bold;" +
                        "-fx-padding: 3 8; -fx-background-radius: 4;"
        );
        badgeEntidad.setMinWidth(80);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTipo = new Label(inc.getTipo());
        lblTipo.setStyle("-fx-text-fill: #CCC; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label lblDesc = new Label(inc.getDescripcion());
        lblDesc.setStyle("-fx-text-fill: #666; -fx-font-size: 12px; -fx-wrap-text: true;");
        lblDesc.setMaxWidth(400);

        Label lblFecha = new Label("📅 " + inc.getFecha().toString() + "  ·  ID: " + inc.getIdIncidencia());
        lblFecha.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");

        info.getChildren().addAll(lblTipo, lblDesc, lblFecha);
        fila.getChildren().addAll(badgeEntidad, info);
        return fila;
    }

    private void mostrarMensajeInc(String texto, boolean exito) {
        lblMensajeIncidencia.setText(texto);
        lblMensajeIncidencia.setStyle(
                "-fx-font-size: 12px; -fx-text-fill: " + (exito ? "#4CAF50" : "#E05555") + ";"
        );
        lblMensajeIncidencia.setVisible(true);
        lblMensajeIncidencia.setManaged(true);
    }

    private void ocultarMensajeInc() {
        lblMensajeIncidencia.setVisible(false);
        lblMensajeIncidencia.setManaged(false);
    }

    // ════════════════════════════════════════════════════════
    // PANEL MÉTRICAS — RF-018 / RF-019
    // ════════════════════════════════════════════════════════

    private void cargarMetricas() {
        List<Compra> todasCompras = new ArrayList<>(gestor.getCompras());

        // ── KPI Cards ─────────────────────────────────────
        filaKPIs.getChildren().clear();

        long totalCompras = todasCompras.size();
        long confirmadas  = todasCompras.stream()
                .filter(c -> c.getEstadoEnum() == EstadoCompra.CONFIRMADA).count();
        long canceladas   = todasCompras.stream()
                .filter(c -> c.getEstadoEnum() == EstadoCompra.CANCELADA).count();
        double ingresoTotal = todasCompras.stream()
                .filter(c -> c.getEstadoEnum() == EstadoCompra.CONFIRMADA
                        || c.getEstadoEnum() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal)
                .sum();

        double tasaCancelacion = totalCompras > 0
                ? (canceladas * 100.0 / totalCompras) : 0;

        filaKPIs.getChildren().addAll(
                kpiCard("Total Compras",     String.valueOf(totalCompras),  "#7EC8E3"),
                kpiCard("Confirmadas",       String.valueOf(confirmadas),   "#4CAF50"),
                kpiCard("Canceladas",        String.valueOf(canceladas),    "#E05555"),
                kpiCard("Ingresos",          "$" + String.format("%,.0f", ingresoTotal), "#E8C547"),
                kpiCard("Tasa Cancelación",  String.format("%.1f%%", tasaCancelacion),  "#FF9800")
        );

        // ── Gráfica de barras: compras por evento ─────────
        graficaVentas.getData().clear();
        graficaVentas.setStyle("-fx-background-color: transparent;");
        graficaVentas.setLegendVisible(false);

        XYChart.Series<String, Number> serieVentas = new XYChart.Series<>();
        Map<String, Long> comprasPorEvento = todasCompras.stream()
                .filter(c -> c.getEvento() != null)
                .collect(Collectors.groupingBy(
                        c -> truncar(c.getEvento().getNombre(), 14),
                        Collectors.counting()
                ));

        comprasPorEvento.forEach((nombreEvento, cantidad) ->
                serieVentas.getData().add(
                        new XYChart.Data<>(nombreEvento, cantidad)
                )
        );
        graficaVentas.getData().add(serieVentas);

        // ── Gráfica de pie: estados de compras ────────────
        graficaEstados.getData().clear();
        graficaEstados.setStyle("-fx-background-color: transparent;");

        Map<EstadoCompra, Long> porEstado = todasCompras.stream()
                .collect(Collectors.groupingBy(Compra::getEstadoEnum, Collectors.counting()));

        porEstado.forEach((estado, cantidad) -> {
            if (cantidad > 0) {
                graficaEstados.getData().add(
                        new PieChart.Data(estado.name() + " (" + cantidad + ")", cantidad)
                );
            }
        });

        // ── Gráfica de líneas: ingresos por categoría ─────
        graficaIngresos.getData().clear();
        graficaIngresos.setStyle("-fx-background-color: transparent;");
        graficaIngresos.setLegendVisible(false);

        XYChart.Series<String, Number> serieIngresos = new XYChart.Series<>();
        Map<String, Double> ingresosPorCategoria = todasCompras.stream()
                .filter(c -> c.getEvento() != null &&
                        (c.getEstadoEnum() == EstadoCompra.CONFIRMADA
                                || c.getEstadoEnum() == EstadoCompra.PAGADA))
                .collect(Collectors.groupingBy(
                        c -> c.getEvento().getCategoria().name(),
                        Collectors.summingDouble(Compra::getTotal)
                ));

        ingresosPorCategoria.forEach((cat, total) ->
                serieIngresos.getData().add(new XYChart.Data<>(cat, total))
        );
        graficaIngresos.getData().add(serieIngresos);
    }

    private VBox kpiCard(String titulo, String valor, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle(
                "-fx-background-color: #161616;" +
                        "-fx-border-color: " + color + "33; -fx-border-width: 1;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        Label lblTitulo = new Label(titulo.toUpperCase());
        lblTitulo.setStyle("-fx-text-fill: #444; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label lblValor = new Label(valor);
        lblValor.setStyle(
                "-fx-font-family: 'Georgia'; -fx-font-size: 24px;" +
                        "-fx-font-weight: bold; -fx-text-fill: " + color + ";"
        );

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    // ── Exportar CSV (RF-046) ─────────────────────────────

    @FXML
    private void exportarCSV() {
        List<Compra> compras = new ArrayList<>(gestor.getCompras());

        if (compras.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sin datos", "No hay compras para exportar.");
            return;
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID Compra,Usuario,Evento,Fecha,Estado,Entradas,Total\n");

        for (Compra c : compras) {
            csv.append(c.getIdCompra()).append(",")
                    .append(c.getUsuario() != null ? c.getUsuario().getNombreCompleto() : "").append(",")
                    .append(c.getEvento()  != null ? c.getEvento().getNombre()          : "").append(",")
                    .append(c.getFechaCreacion()).append(",")
                    .append(c.getEstadoEnum()).append(",")
                    .append(c.getListEntradas().size()).append(",")
                    .append(c.getTotal()).append("\n");
        }

        String ruta = System.getProperty("user.home") +
                "/eventic_ventas_admin_" + LocalDate.now() + ".csv";

        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(csv.toString());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte exportado",
                    "Archivo guardado en:\n" + ruta);
        } catch (IOException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
        }
    }

    // ── Cerrar sesión ─────────────────────────────────────

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helpers de UI ─────────────────────────────────────

    private Button botonEstado(String texto, String color) {
        Button btn = new Button(texto);
        btn.setStyle(
                "-fx-background-color: " + color + "22;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-border-color: " + color + "; -fx-border-width: 1;" +
                        "-fx-border-radius: 5; -fx-background-radius: 5;" +
                        "-fx-font-size: 11px; -fx-padding: 5 12; -fx-cursor: hand;"
        );
        return btn;
    }

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        aplicarEstilo(tf);
        return tf;
    }

    private void aplicarEstilo(Control c) {
        c.setStyle(
                "-fx-background-color: #1A1A1A; -fx-border-color: #2A2A2A; -fx-border-width: 1;" +
                        "-fx-border-radius: 6; -fx-background-radius: 6;" +
                        "-fx-text-fill: #EEE; -fx-font-size: 13px; -fx-padding: 9 12;" +
                        "-fx-prompt-text-fill: #444;"
        );
        c.setMaxWidth(Double.MAX_VALUE);
    }

    private Label etiqueta(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #444; -fx-font-size: 10px; -fx-font-weight: bold;");
        return l;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String truncar(String texto, int max) {
        return texto.length() > max ? texto.substring(0, max) + "…" : texto;
    }

    private Stage getStage() {
        return (Stage) lblAdminNombre.getScene().getWindow();
    }
}
