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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.*;

import java.net.URL;
import java.util.*;

public class CompraViewController implements Initializable, SesionConEvento {

    // ── FXML refs — top ───────────────────────────────────
    @FXML private Label lblNombreEvento;
    @FXML private Label lblUsuarioTop;

    // ── FXML refs — selección ─────────────────────────────
    @FXML private VBox  panelZonas;

    // ── FXML refs — servicios ─────────────────────────────
    @FXML private CheckBox chkVIP;
    @FXML private CheckBox chkSeguro;
    @FXML private CheckBox chkMerchandising;
    @FXML private CheckBox chkParqueadero;
    @FXML private CheckBox chkPreferencial;

    // ── FXML refs — pago ──────────────────────────────────
    @FXML private ComboBox<String> cbMetodoPago;

    // ── FXML refs — resumen ───────────────────────────────
    @FXML private Label lblResumenEvento;
    @FXML private Label lblResumenFecha;
    @FXML private VBox  panelDesglose;
    @FXML private Label lblTotal;
    @FXML private Label lblError;

    // ── Sesión y estado ───────────────────────────────────
    private Usuario usuarioActivo;
    private Evento  eventoActivo;
    private final GestorEventos gestor  = GestorEventos.getInstance();
    private final CompraFacade  facade  = new CompraFacade();

    // Spinners por zona general (zona → spinner cantidad)
    private final Map<Zona, Spinner<Integer>> spinnerZonas = new LinkedHashMap<>();

    // Asientos seleccionados en zonas numeradas (zona → lista asientos)
    private final Map<Zona, List<Asiento>> asientosSeleccionados = new LinkedHashMap<>();

    // ── Inicialización FXML ───────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbMetodoPago.getItems().addAll(
            "Tarjeta de Crédito",
            "Tarjeta de Débito",
            "PSE"
        );
        cbMetodoPago.setValue("Tarjeta de Crédito");
    }

    // ── Inyección de sesión ───────────────────────────────

    @Override
    public void iniciarSesion(Usuario usuario, Evento evento) {
        this.usuarioActivo = usuario;
        this.eventoActivo  = evento;

        lblNombreEvento.setText("Comprando: " + evento.getNombre());
        lblUsuarioTop.setText(usuario.getNombreCompleto());
        lblResumenEvento.setText(evento.getNombre());
        lblResumenFecha.setText("📅 " + evento.getFechaHora().toLocalDate().toString());

        construirFilasZonas();
        actualizarTotal();
    }

    // ── Construcción dinámica de filas de zona ────────────

    private void construirFilasZonas() {
        panelZonas.getChildren().clear();

        if (eventoActivo.getRecinto() == null
                || eventoActivo.getRecinto().getZonas().isEmpty()) {
            Label sinZonas = new Label("No hay zonas disponibles para este evento.");
            sinZonas.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");
            panelZonas.getChildren().add(sinZonas);
            return;
        }

        for (Zona zona : eventoActivo.getRecinto().getZonas()) {
            panelZonas.getChildren().add(crearFilaZona(zona));
        }
    }

    private HBox crearFilaZona(Zona zona) {
        HBox fila = new HBox(16);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 16, 14, 16));
        fila.setStyle(
            "-fx-background-color: #161616;" +
            "-fx-border-color: #252525; -fx-border-width: 1;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        // Info de la zona
        VBox infoZona = new VBox(4);
        HBox.setHgrow(infoZona, Priority.ALWAYS);

        Label lblNombreZona = new Label(zona.getNombre() + "  —  " + zona.getTipoZona().name());
        lblNombreZona.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 14px;" +
            "-fx-font-weight: bold; -fx-text-fill: #E8C547;"
        );

        Label lblPrecioZona = new Label(
            "$" + String.format("%,.0f", zona.getPrecioBase()) + " por entrada"
        );
        lblPrecioZona.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

        Label lblCapZona = new Label(
            "Capacidad: " + zona.getCapacidad() +
            "  ·  " + (zona.isNumerado() ? "Numerado" : "General")
        );
        lblCapZona.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");

        infoZona.getChildren().addAll(lblNombreZona, lblPrecioZona, lblCapZona);

        if (zona.isNumerado()) {
            // Zona numerada → botón para abrir el selector de asientos
            Label lblSeleccionados = new Label("0 asientos seleccionados");
            lblSeleccionados.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
            infoZona.getChildren().add(lblSeleccionados);

            Button btnSeleccionar = new Button("Seleccionar asientos");
            btnSeleccionar.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #E8C547;" +
                "-fx-border-color: #E8C54766; -fx-border-width: 1;" +
                "-fx-border-radius: 6; -fx-background-radius: 6;" +
                "-fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 12px;"
            );
            btnSeleccionar.setOnAction(e ->
                abrirSelectorAsientos(zona, lblSeleccionados)
            );

            fila.getChildren().addAll(infoZona, btnSeleccionar);

        } else {
            // Zona general → spinner de cantidad
            Spinner<Integer> spinner = new Spinner<>(0, zona.getCapacidad(), 0);
            spinner.setPrefWidth(100);
            spinner.setStyle("-fx-background-color: #1A1A1A;");
            spinner.valueProperty().addListener((obs, oldVal, newVal) -> actualizarTotal());
            spinnerZonas.put(zona, spinner);

            Label lblEnt = new Label("entradas");
            lblEnt.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

            fila.getChildren().addAll(infoZona, spinner, lblEnt);
        }

        return fila;
    }

    // ── Selector de asientos (ventana modal) ──────────────

    private void abrirSelectorAsientos(Zona zona, Label lblContador) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Seleccionar asientos — " + zona.getNombre());

        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #111111;");

        // Título
        Label titulo = new Label("Selecciona tus asientos");
        titulo.setStyle(
            "-fx-font-family: 'Georgia'; -fx-font-size: 18px;" +
            "-fx-font-weight: bold; -fx-text-fill: #E8C547;"
        );

        Label subTitulo = new Label(zona.getNombre() + "  ·  $" +
            String.format("%,.0f", zona.getPrecioBase()) + " c/u");
        subTitulo.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        // Leyenda
        HBox leyenda = new HBox(16);
        leyenda.setAlignment(Pos.CENTER_LEFT);
        leyenda.getChildren().addAll(
            itemLeyenda("#4CAF50", "Disponible"),
            itemLeyenda("#E8C547", "Seleccionado"),
            itemLeyenda("#E05555", "Vendido"),
            itemLeyenda("#FF9800", "Reservado"),
            itemLeyenda("#333333", "Bloqueado")
        );

        // Grilla de asientos agrupada por filas
        VBox grilla = new VBox(8);
        grilla.setStyle("-fx-background-color: #111111;");

        List<Asiento> seleccionActual =
            asientosSeleccionados.computeIfAbsent(zona, k -> new ArrayList<>());

        // Agrupar asientos por fila
        Map<String, List<Asiento>> porFila = new LinkedHashMap<>();
        for (Asiento a : zona.getAsientos()) {
            porFila.computeIfAbsent(a.getFila(), k -> new ArrayList<>()).add(a);
        }

        for (Map.Entry<String, List<Asiento>> entry : porFila.entrySet()) {
            HBox filaHBox = new HBox(6);
            filaHBox.setAlignment(Pos.CENTER_LEFT);

            // Etiqueta de fila
            Label lblFila = new Label("Fila " + entry.getKey());
            lblFila.setPrefWidth(55);
            lblFila.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");
            filaHBox.getChildren().add(lblFila);

            for (Asiento asiento : entry.getValue()) {
                Button btnAsiento = new Button(String.valueOf(asiento.getNumero()));
                btnAsiento.setPrefSize(38, 32);
                aplicarEstiloAsiento(btnAsiento, asiento,
                    seleccionActual.contains(asiento));

                if (asiento.getEstado() == EstadoAsiento.DISPONIBLE) {
                    btnAsiento.setOnAction(ev -> {
                        if (seleccionActual.contains(asiento)) {
                            seleccionActual.remove(asiento);
                            aplicarEstiloAsiento(btnAsiento, asiento, false);
                        } else {
                            seleccionActual.add(asiento);
                            aplicarEstiloAsiento(btnAsiento, asiento, true);
                        }
                        lblContador.setText(seleccionActual.size() + " asientos seleccionados");
                        actualizarTotal();
                    });
                } else {
                    btnAsiento.setDisable(true);
                }

                filaHBox.getChildren().add(btnAsiento);
            }

            grilla.getChildren().add(filaHBox);
        }

        ScrollPane scrollGrilla = new ScrollPane(grilla);
        scrollGrilla.setFitToWidth(true);
        scrollGrilla.setStyle(
            "-fx-background: #111111; -fx-background-color: #111111;" +
            "-fx-border-color: transparent;"
        );
        scrollGrilla.setPrefHeight(300);

        // Botón confirmar
        Button btnOk = new Button("Confirmar selección");
        btnOk.setMaxWidth(Double.MAX_VALUE);
        btnOk.setStyle(
            "-fx-background-color: #E8C547; -fx-text-fill: #0D0D0D;" +
            "-fx-font-weight: bold; -fx-padding: 12 0;" +
            "-fx-cursor: hand; -fx-background-radius: 6;"
        );
        btnOk.setOnAction(e -> {
            lblContador.setText(seleccionActual.size() + " asientos seleccionados");
            actualizarTotal();
            modal.close();
        });

        root.getChildren().addAll(titulo, subTitulo, leyenda, scrollGrilla, btnOk);

        Scene sceneModal = new Scene(root, 520, 520);
        modal.setScene(sceneModal);
        modal.showAndWait();
    }

    private void aplicarEstiloAsiento(Button btn, Asiento asiento, boolean seleccionado) {
        if (seleccionado) {
            btn.setStyle(
                "-fx-background-color: #E8C547; -fx-text-fill: #0D0D0D;" +
                "-fx-font-weight: bold; -fx-background-radius: 5;" +
                "-fx-cursor: hand; -fx-font-size: 11px;"
            );
            return;
        }
        switch (asiento.getEstado()) {
            case DISPONIBLE -> btn.setStyle(
                "-fx-background-color: #1E3A1E; -fx-text-fill: #4CAF50;" +
                "-fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;"
            );
            case VENDIDO -> btn.setStyle(
                "-fx-background-color: #3A1E1E; -fx-text-fill: #E05555;" +
                "-fx-background-radius: 5; -fx-font-size: 11px;"
            );
            case RESERVADO -> btn.setStyle(
                "-fx-background-color: #3A2C1E; -fx-text-fill: #FF9800;" +
                "-fx-background-radius: 5; -fx-font-size: 11px;"
            );
            case BLOQUEADO -> btn.setStyle(
                "-fx-background-color: #1A1A1A; -fx-text-fill: #333;" +
                "-fx-background-radius: 5; -fx-font-size: 11px;"
            );
        }
    }

    // ── Actualizar total en tiempo real ───────────────────

    @FXML
    private void actualizarTotal() {
        double total = calcularTotal();
        lblTotal.setText("$" + String.format("%,.0f", total));
        actualizarDesglose();
    }

    private double calcularTotal() {
        double total = 0;

        // Zonas generales
        for (Map.Entry<Zona, Spinner<Integer>> entry : spinnerZonas.entrySet()) {
            total += entry.getKey().getPrecioBase() * entry.getValue().getValue();
        }

        // Asientos numerados
        for (Map.Entry<Zona, List<Asiento>> entry : asientosSeleccionados.entrySet()) {
            total += entry.getKey().getPrecioBase() * entry.getValue().size();
        }

        // Servicios adicionales
        if (chkVIP.isSelected())          total += 50000;
        if (chkSeguro.isSelected())       total += 10000;
        if (chkMerchandising.isSelected()) total += 20000;
        if (chkParqueadero.isSelected())  total += 15000;
        if (chkPreferencial.isSelected()) total += 25000;

        return total;
    }

    private void actualizarDesglose() {
        panelDesglose.getChildren().clear();

        // Líneas de entradas por zona
        for (Map.Entry<Zona, Spinner<Integer>> entry : spinnerZonas.entrySet()) {
            int cantidad = entry.getValue().getValue();
            if (cantidad > 0) {
                agregarLineaDesglose(
                    cantidad + "x " + entry.getKey().getNombre(),
                    entry.getKey().getPrecioBase() * cantidad
                );
            }
        }

        for (Map.Entry<Zona, List<Asiento>> entry : asientosSeleccionados.entrySet()) {
            int cantidad = entry.getValue().size();
            if (cantidad > 0) {
                agregarLineaDesglose(
                    cantidad + "x " + entry.getKey().getNombre() + " (numerado)",
                    entry.getKey().getPrecioBase() * cantidad
                );
            }
        }

        // Líneas de servicios
        if (chkVIP.isSelected())           agregarLineaDesglose("Acceso VIP",          50000);
        if (chkSeguro.isSelected())        agregarLineaDesglose("Seguro cancelación",  10000);
        if (chkMerchandising.isSelected()) agregarLineaDesglose("Merchandising",       20000);
        if (chkParqueadero.isSelected())   agregarLineaDesglose("Parqueadero",         15000);
        if (chkPreferencial.isSelected())  agregarLineaDesglose("Acceso preferencial", 25000);
    }

    private void agregarLineaDesglose(String concepto, double valor) {
        HBox linea = new HBox();
        linea.setAlignment(Pos.CENTER_LEFT);

        Label lblConcepto = new Label(concepto);
        lblConcepto.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");
        HBox.setHgrow(lblConcepto, Priority.ALWAYS);

        Label lblValor = new Label("$" + String.format("%,.0f", valor));
        lblValor.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");

        linea.getChildren().addAll(lblConcepto, lblValor);
        panelDesglose.getChildren().add(linea);
    }

    // ── Confirmar compra ──────────────────────────────────

    @FXML
    private void confirmarCompra() {
        ocultarError();

        // Validar que haya al menos una entrada
        List<Entrada> entradas = construirEntradas();
        if (entradas.isEmpty()) {
            mostrarError("Debes seleccionar al menos una entrada.");
            return;
        }

        double total = calcularTotal();

        // Crear compra via Builder a través del Facade
        String idCompra = "C-" + System.currentTimeMillis();
        Compra compra   = facade.crearCompra(
            idCompra, usuarioActivo, eventoActivo, entradas, null
        );

        // Aplicar servicios adicionales (Decorator)
        ICompra compraDecorada = compra;
        if (chkVIP.isSelected())           compraDecorada = new ServicioVIP(compraDecorada);
        if (chkSeguro.isSelected())        compraDecorada = new ServicioSeguro(compraDecorada);
        if (chkMerchandising.isSelected()) compraDecorada = new ServicioMerchandising(compraDecorada);
        if (chkParqueadero.isSelected())   compraDecorada = new ServicioParqueadero(compraDecorada);
        if (chkPreferencial.isSelected())  compraDecorada = new ServicioAccesoPreferencial(compraDecorada);

        // Procesar pago (Strategy)
        PagoStrategy estrategia = resolverEstrategia(cbMetodoPago.getValue());
        boolean exitoso = facade.procesarPago(compra, estrategia, total);

        if (exitoso) {
            facade.confirmarCompra(compra);

            // Marcar asientos seleccionados como vendidos
            asientosSeleccionados.values().forEach(lista ->
                lista.forEach(a -> a.setEstado(EstadoAsiento.VENDIDO))
            );

            mostrarDialogoExito(compra, total);
        } else {
            mostrarError("No se pudo procesar el pago. Intenta de nuevo.");
        }
    }

    private List<Entrada> construirEntradas() {
        List<Entrada> entradas = new ArrayList<>();
        int contador = 1;

        // Zonas generales
        for (Map.Entry<Zona, Spinner<Integer>> entry : spinnerZonas.entrySet()) {
            int cantidad = entry.getValue().getValue();
            for (int i = 0; i < cantidad; i++) {
                entradas.add(new Entrada(
                    "ENT-" + System.currentTimeMillis() + "-" + contador++,
                    entry.getKey()
                ));
            }
        }

        // Asientos numerados
        for (Map.Entry<Zona, List<Asiento>> entry : asientosSeleccionados.entrySet()) {
            for (Asiento asiento : entry.getValue()) {
                entradas.add(new Entrada(
                    "ENT-" + System.currentTimeMillis() + "-" + contador++,
                    entry.getKey(),
                    asiento
                ));
            }
        }

        return entradas;
    }

    private PagoStrategy resolverEstrategia(String metodo) {
        return switch (metodo) {
            case "Tarjeta de Débito" -> new PagoTarjetaDebito();
            case "PSE"               -> new PagoPSE();
            default                  -> new PagoTarjetaCredito();
        };
    }

    private void mostrarDialogoExito(Compra compra, double total) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Compra exitosa!");
        alert.setHeaderText("Tu compra fue confirmada");
        alert.setContentText(
            "ID de compra: " + compra.getIdCompra() + "\n" +
            "Evento: " + eventoActivo.getNombre() + "\n" +
            "Total pagado: $" + String.format("%,.0f", total) + "\n\n" +
            "Puedes ver tu comprobante en \"Mis Compras\"."
        );
        alert.showAndWait();

        // Navegar a Mis Compras
        navegarMisCompras();
    }

    // ── Navegación ────────────────────────────────────────

    private void navegarMisCompras() {
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

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/DetalleEventoView.fxml")
            );
            Scene scene = new Scene(loader.load());
            SesionConEvento ctrl = loader.getController();
            ctrl.iniciarSesion(usuarioActivo, eventoActivo);
            getStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irInicio() {
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

    // ── Helpers ───────────────────────────────────────────

    private HBox itemLeyenda(String color, String texto) {
        HBox h = new HBox(6);
        h.setAlignment(Pos.CENTER_LEFT);
        Rectangle r = new Rectangle(12, 12);
        r.setFill(Color.web(color));
        r.setArcWidth(3);
        r.setArcHeight(3);
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        h.getChildren().addAll(r, l);
        return h;
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private Stage getStage() {
        return (Stage) lblTotal.getScene().getWindow();
    }
}
