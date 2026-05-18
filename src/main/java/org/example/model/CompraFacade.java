package org.example.model;

import java.util.List;

/**
 * Facade del subsistema de compras.
 *
 * Simplifica operaciones complejas que involucran múltiples clases:
 * Compra, PagoService, GestorEventos, Entrada, ServicioAdicional.
 *
 * El controlador de MVC llama a esta clase en lugar de orquestar
 * manualmente todos los pasos del proceso de compra.
 */
public class CompraFacade {

    private final GestorEventos gestor;
    private final PagoService pagoService;

    public CompraFacade() {
        this.gestor = GestorEventos.getInstance();
        this.pagoService = new PagoService();
    }

    /**
     * Flujo completo: crear compra → agregar entradas → aplicar tarifa → registrar.
     *
     * @param idCompra  ID único de la compra
     * @param usuario   Usuario que compra
     * @param evento    Evento al que asiste
     * @param entradas  Lista de entradas seleccionadas
     * @param tarifa    Tarifa con impuestos y comisión (puede ser null)
     * @return          Compra creada y registrada en el sistema
     */
    public Compra crearCompra(String idCompra, Usuario usuario, Evento evento,
                              List<Entrada> entradas, Tarifa tarifa) {

        Compra compra = new Compra.Builder(idCompra, usuario, evento)
                .tarifa(tarifa)
                .build();

        for (Entrada entrada : entradas) {
            compra.agregarEntrada(entrada);
        }

        gestor.registrarCompra(compra);
        return compra;
    }

    /**
     * Procesa el pago de una compra usando la estrategia indicada.
     *
     * @param compra    Compra en estado CREADA
     * @param estrategia Método de pago elegido
     * @param monto     Monto a cobrar
     * @return          true si el pago fue exitoso
     */
    public boolean procesarPago(Compra compra, PagoStrategy estrategia, double monto) {
        pagoService.setStrategy(estrategia);
        boolean exitoso = pagoService.procesarPago(monto);

        if (exitoso) {
            Pago pago = new Pago(
                    "PAG-" + compra.getIdCompra(),
                    monto,
                    resolverMetodoPago(estrategia)
            );
            pago.setExitoso(true);
            compra.setPago(pago);
            compra.pagar(); // Transición de estado: CREADA → PAGADA
            gestor.notificar("COMPRA_PAGADA",
                    "Compra " + compra.getIdCompra() + " pagada por " + compra.getUsuario().getNombreCompleto());
        }

        return exitoso;
    }

    /**
     * Confirma una compra ya pagada.
     */
    public void confirmarCompra(Compra compra) {
        compra.confirmar(); // PAGADA → CONFIRMADA
        gestor.notificar("COMPRA_CONFIRMADA",
                "Compra " + compra.getIdCompra() + " confirmada.");
    }

    /**
     * Cancela una compra y notifica al sistema.
     */
    public void cancelarCompra(Compra compra) {
        compra.cancelar();
        gestor.notificar("COMPRA_CANCELADA",
                "Compra " + compra.getIdCompra() + " fue cancelada.");
    }

    /**
     * Aplica un servicio adicional (Decorator) sobre una compra o compra ya decorada.
     *
     * @param base       ICompra original (Compra o ServicioAdicional previo)
     * @param tipoServicio Tipo de servicio a agregar
     * @return           ICompra decorada con el nuevo servicio
     */
    public ICompra aplicarServicio(ICompra base, String tipoServicio) {
        switch (tipoServicio.toUpperCase()) {
            case "VIP":           return new ServicioVIP(base);
            case "SEGURO":        return new ServicioSeguro(base);
            case "MERCHANDISING": return new ServicioMerchandising(base);
            case "PARQUEADERO":   return new ServicioParqueadero(base);
            case "PREFERENCIAL":  return new ServicioAccesoPreferencial(base);
            default:
                throw new IllegalArgumentException("Servicio desconocido: " + tipoServicio);
        }
    }

    // Infiere MetodoPago desde la estrategia concreta
    private MetodoPago resolverMetodoPago(PagoStrategy estrategia) {
        if (estrategia instanceof PagoTarjetaCredito) return MetodoPago.TARJETA_CREDITO;
        if (estrategia instanceof PagoTarjetaDebito)  return MetodoPago.TARJETA_DEBITO;
        if (estrategia instanceof PagoPSE)             return MetodoPago.PSE;
        return MetodoPago.TRANSFERENCIA;
    }
}
