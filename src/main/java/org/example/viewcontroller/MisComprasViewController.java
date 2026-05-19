package org.example.viewcontroller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MisComprasViewController implements Initializable, SesionConUsuario {

    // ── FXML refs ─────────────────────────────────────────
    @FXML private Label               lblInfoUsuario;
    @FXML private Label               lblTotalCompras;
    @FXML private ComboBox<String>    cbFiltroEstado;
    @FXML private VBox                panelCompras;

    // ── Sesión ────────────────────────────────────────────
    private Usuario         usuarioActivo;
    private final GestorEventos gestor = GestorEventos.getInstance();

    // ── Inicialización FXML ───────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFiltroEstado.getItems().addAll(
            "Todas",
            "CREADA",
            "PAGADA",
            "CONFIRMADA",
            "CANCELADA",
            "REEMBOLSADA"
        );
        cbFiltroEstado.setValue("Todas");
    }

    // ── Inyección de sesión ───────────────────────────────

    @Override
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActivo = usuario;
        lblInfoUsuario.setText(
            usuario.getNombreCompleto() + "  ·  " + usuario.getCorreoElectronico()
        );
        cargarCompras();
    }

    // ── Carga y filtrado ──────────────────────────────────

    private void cargarCompras() {
        List<Compra> misCompras = obtenerComprasUsuario();
        renderizarCompras(misCompras);
    }

    @FXML
    private void aplicarFiltro() {
        String filtro = cbFiltroEstado.getValue();

        List<Compra> filtradas = obtenerComprasUsuario().stream()
                .filter(c -> "Todas".equals(filtro)
                        || c.getEstadoEnum().name().equals(filtro))
                .collect(Collectors.toList());

        renderizarCompras(filtradas);
    }

    private List<Compra> obtenerComprasUsuario() {
        return gestor.getCompras().stream()
                .filter(c -> c.getUsuario() != null &&
                        c.getUsuario().getUsuario().equals(usuarioActivo.getUsuario()))
                .collect(Collectors.toList());
    }

    private void renderizarCompras(List<Compra> compras) {
        panelCompras.getChildren().clear();

        // Actualizar contador
        lblTotalCompras.setText(compras.size() + " compra(s) encontrada(s)");

        if (compras.isEmpty()) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(60, 0, 0, 0));

            Label ico = new Label("🎟");
            ico.setStyle("-fx-font-size: 48px;");

            Label msg = new Label("No tienes compras aún.");
            msg.setStyle(
                "-fx-font-family: 'Georgia'; -fx-font-size: 18px;" +
                "-fx-text-fill: #333;"
            );

            Label sub = new Label("Explora los eventos disponibles y compra tu primera entrada.");
            sub.setStyle("-fx-text-fill: #444; -fx-font-size: 13px;");

            Button btnExplorar = new Button("Ver Eventos");
            btnExplorar.setStyle(
                "-fx-background-color: #E8C547; -fx-text-fill: #0D0D0D;" +
                "-fx-font-weight: bold; -fx-padding: 10 28;" +
                "-fx-cursor: hand; -fx-background-radius: 6;"
            );
            btnExplorar.setOnAction(e -> volverInicio());

            empty.getChildren().addAll(ico, msg, sub, btnExplorar);
            panelCompras.getChildren().add(empty);
            return;
        }

        for (Compra compra : compras) {
            panelCompras.getChildren().add(crearCardCompra(compra));
        }
    }

    // ── Card de compra ────────────────────────────────────

    private VBox crearCardCompra(Compra compra) {
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: #161616;" +
            "-fx-border-color: #222222;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );

        // ── Franja superior de color según estado ─────────
        HBox franjaEstado = new HBox();
        franjaEstado.setPrefHeight(4);
        franjaEstado.setStyle(
            "-fx-background-color: " + colorEstado(compra.getEstadoEnum()) + ";" +
            "-fx-background-radius: 10 10 0 0;"
        );

        // ── Contenido principal ───────────────────────────
        VBox contenido = new VBox(14);
        contenido.setPadding(new Insets(18, 20, 18, 20));

        // Fila 1: nombre evento + badge estado
        HBox fila1 = new HBox(12);
        fila1.setAlignment(Pos.CENTER_LEFT);

        Label lblEvento = new Label(
            compra.getEvento() != null ? compra.getEvento().getNombre() : "Evento"
        );
        lblEvento.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 17px;" +
            "-fx-font-weight: bold; -fx-text-fill: #EEEEEE;"
        );
        HBox.setHgrow(lblEvento, Priority.ALWAYS);

        Label badgeEstado = new Label(compra.getEstadoEnum().name());
        badgeEstado.setStyle(
            "-fx-background-color: " + colorEstado(compra.getEstadoEnum()) + "22;" +
            "-fx-text-fill: " + colorEstado(compra.getEstadoEnum()) + ";" +
            "-fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 4 10; -fx-background-radius: 4;"
        );

        fila1.getChildren().addAll(lblEvento, badgeEstado);

        // Fila 2: chips de info
        HBox fila2 = new HBox(28);
        fila2.setAlignment(Pos.CENTER_LEFT);
        fila2.getChildren().addAll(
            chipInfo("ID",       compra.getIdCompra()),
            chipInfo("Fecha",    compra.getFechaCreacion().toString()),
            chipInfo("Entradas", String.valueOf(compra.getListEntradas().size())),
            chipInfo("Total",    "$" + String.format("%,.0f", compra.getTotal()))
        );

        // Fila 3: detalle de entradas (colapsable visualmente)
        VBox detalleEntradas = new VBox(4);
        if (!compra.getListEntradas().isEmpty()) {
            for (Entrada entrada : compra.getListEntradas()) {
                HBox filaEntrada = new HBox(12);
                filaEntrada.setAlignment(Pos.CENTER_LEFT);

                Label idEntrada = new Label("· " + entrada.getIdEntrada());
                idEntrada.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");

                Label zonaEntrada = new Label(
                    entrada.getZona() != null
                        ? entrada.getZona().getNombre() + " — " + entrada.getZona().getTipoZona()
                        : "—"
                );
                zonaEntrada.setStyle("-fx-text-fill: #555; -fx-font-size: 11px;");

                Label estadoEntrada = new Label(entrada.getEstadoEntrada().name());
                estadoEntrada.setStyle(
                    "-fx-text-fill: " + colorEstadoEntrada(entrada.getEstadoEntrada()) + ";" +
                    "-fx-font-size: 10px; -fx-font-weight: bold;"
                );

                filaEntrada.getChildren().addAll(idEntrada, zonaEntrada, estadoEntrada);
                detalleEntradas.getChildren().add(filaEntrada);
            }
        }

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1E1E1E;");

        // Fila botones
        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_LEFT);

        // Comprobante — siempre disponible
        Button btnComprobante = crearBoton(
            "Ver Comprobante", "#7EC8E3", "#7EC8E322"
        );
        btnComprobante.setOnAction(e -> mostrarComprobante(compra));

        // Cancelar — solo si está en estado cancelable
        Button btnCancelar = crearBoton(
            "Cancelar", "#E05555", "#E0555522"
        );
        boolean cancelable = compra.getEstadoEnum() == EstadoCompra.CREADA
                || compra.getEstadoEnum() == EstadoCompra.PAGADA
                || compra.getEstadoEnum() == EstadoCompra.CONFIRMADA;
        btnCancelar.setDisable(!cancelable);
        btnCancelar.setOnAction(e -> cancelarCompra(compra));

        // Reembolso — solo si está cancelada
        Button btnReembolso = crearBoton(
            "Solicitar Reembolso", "#FF9800", "#FF980022"
        );
        boolean reembolsable = compra.getEstadoEnum() == EstadoCompra.CANCELADA;
        btnReembolso.setDisable(!reembolsable);
        btnReembolso.setOnAction(e -> solicitarReembolso(compra));

        botones.getChildren().addAll(btnComprobante, btnCancelar, btnReembolso);

        contenido.getChildren().addAll(fila1, fila2, detalleEntradas, sep, botones);
        card.getChildren().addAll(franjaEstado, contenido);

        return card;
    }

    // ── Acciones sobre compras ────────────────────────────

    private void cancelarCompra(Compra compra) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar Compra");
        confirm.setHeaderText("¿Estás seguro de cancelar esta compra?");
        confirm.setContentText(
            "Compra: " + compra.getIdCompra() + "\n" +
            "Esta acción no se puede deshacer."
        );

        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    compra.cancelar();
                    gestor.notificar(
                        "COMPRA_CANCELADA",
                        "Compra " + compra.getIdCompra() + " cancelada por el usuario."
                    );
                    cargarCompras(); // Refrescar lista
                } catch (IllegalStateException ex) {
                    mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "No se puede cancelar",
                        ex.getMessage()
                    );
                }
            }
        });
    }

    private void solicitarReembolso(Compra compra) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Solicitar Reembolso");
        confirm.setHeaderText("¿Deseas solicitar el reembolso de esta compra?");
        confirm.setContentText("ID: " + compra.getIdCompra());

        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    compra.reembolsar();
                    gestor.notificar(
                        "REEMBOLSO",
                        "Reembolso procesado para compra " + compra.getIdCompra()
                    );
                    cargarCompras();
                } catch (IllegalStateException ex) {
                    mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "No se puede reembolsar",
                        ex.getMessage()
                    );
                }
            }
        });
    }

    private void mostrarComprobante(Compra compra) {
        Stage ventana = new Stage();
        ventana.setTitle("Comprobante — " + compra.getIdCompra());

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #111111;");

        // Header del comprobante
        VBox header = new VBox(6);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setStyle("-fx-background-color: #161616; -fx-border-color: #222; -fx-border-width: 0 0 1 0;");

        Label lblTitulo = new Label("EVENTIC — Comprobante de Compra");
        lblTitulo.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 16px;" +
            "-fx-font-weight: bold; -fx-text-fill: #E8C547;"
        );

        Label lblId = new Label("ID: " + compra.getIdCompra());
        lblId.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        header.getChildren().addAll(lblTitulo, lblId);

        // Texto del comprobante
        TextArea area = new TextArea(compra.getDescripcion());
        area.setEditable(false);
        area.setPrefRowCount(18);
        area.setStyle(
            "-fx-control-inner-background: #111111;" +
            "-fx-text-fill: #CCCCCC;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: transparent;"
        );

        root.getChildren().addAll(header, area);
        VBox.setVgrow(area, Priority.ALWAYS);

        Scene sc = new Scene(root, 520, 480);
        ventana.setScene(sc);
        ventana.show();
    }

    // ── Reporte CSV (RF-011 / RF-046) ─────────────────────

    @FXML
    private void descargarReporte() {
        List<Compra> misCompras = obtenerComprasUsuario();

        if (misCompras.isEmpty()) {
            mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Sin datos",
                "No tienes compras para exportar."
            );
            return;
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID Compra,Evento,Fecha,Estado,Entradas,Total\n");

        for (Compra c : misCompras) {
            csv.append(c.getIdCompra()).append(",")
               .append(c.getEvento() != null ? c.getEvento().getNombre() : "").append(",")
               .append(c.getFechaCreacion()).append(",")
               .append(c.getEstadoEnum()).append(",")
               .append(c.getListEntradas().size()).append(",")
               .append(c.getTotal()).append("\n");
        }

        String ruta = System.getProperty("user.home") +
                "/eventic_compras_" + LocalDate.now() + ".csv";

        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(csv.toString());
            mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Reporte descargado",
                "Archivo guardado en:\n" + ruta
            );
        } catch (IOException ex) {
            mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error al guardar",
                "No se pudo guardar el archivo:\n" + ex.getMessage()
            );
        }
    }

    // ── Navegación ────────────────────────────────────────

    @FXML
    private void volverInicio() {
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

    // ── Helpers de UI ─────────────────────────────────────

    private VBox chipInfo(String titulo, String valor) {
        VBox v = new VBox(3);
        Label t = new Label(titulo.toUpperCase());
        t.setStyle("-fx-text-fill: #3A3A3A; -fx-font-size: 9px; -fx-font-weight: bold;");
        Label val = new Label(valor);
        val.setStyle("-fx-text-fill: #999; -fx-font-size: 13px;");
        v.getChildren().addAll(t, val);
        return v;
    }

    private Button crearBoton(String texto, String textColor, String bgColor) {
        Button btn = new Button(texto);
        btn.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-border-color: " + textColor + "; -fx-border-width: 1;" +
            "-fx-border-radius: 6; -fx-background-radius: 6;" +
            "-fx-font-size: 12px; -fx-padding: 7 14; -fx-cursor: hand;"
        );
        return btn;
    }

    private String colorEstado(EstadoCompra estado) {
        return switch (estado) {
            case CONFIRMADA  -> "#4CAF50";
            case PAGADA      -> "#E8C547";
            case CREADA      -> "#7EC8E3";
            case CANCELADA   -> "#E05555";
            case REEMBOLSADA -> "#FF9800";
            case INCIDENCIA  -> "#AA44AA";
        };
    }

    private String colorEstadoEntrada(EstadoEntrada estado) {
        return switch (estado) {
            case ACTIVA  -> "#4CAF50";
            case USADA   -> "#888888";
            case ANULADA -> "#E05555";
        };
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Stage getStage() {
        return (Stage) panelCompras.getScene().getWindow();
    }
}
