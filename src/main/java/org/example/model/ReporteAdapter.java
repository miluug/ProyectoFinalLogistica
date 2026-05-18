package org.example.model;

import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Adapter que adapta los datos crudos del GestorEventos
 * a la interfaz IReporte esperada por el sistema de reportes.
 *
 * El "Adaptee" es GestorEventos (datos internos del sistema).
 * El "Target" es IReporte (lo que la vista o exportador espera).
 *
 * Permite agregar futuros adaptadores para otros formatos
 * (PDF, Excel, JSON) sin modificar GestorEventos ni la vista.
 */
public class ReporteAdapter implements IReporte {

    public enum TipoReporte {
        VENTAS,
        EVENTOS,
        INCIDENCIAS
    }

    private final GestorEventos gestor;
    private final TipoReporte tipo;
    private final LocalDate desde;
    private final LocalDate hasta;

    public ReporteAdapter(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        this.gestor = GestorEventos.getInstance();
        this.tipo = tipo;
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public String getTitulo() {
        return "Reporte de " + tipo.name() + " | " + desde + " → " + hasta;
    }

    @Override
    public String generar() {
        return switch (tipo) {
            case VENTAS      -> generarReporteVentas();
            case EVENTOS     -> generarReporteEventos();
            case INCIDENCIAS -> generarReporteIncidencias();
        };
    }

    private String generarReporteVentas() {
        LinkedList<Compra> compras = gestor.getCompras();
        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE DE VENTAS =====\n");
        sb.append("Periodo: ").append(desde).append(" - ").append(hasta).append("\n\n");

        double totalGeneral = 0;
        for (Compra c : compras) {
            if (!estaEnRango(c.getFechaCreacion())) continue;
            sb.append("Compra: ").append(c.getIdCompra())
              .append(" | Usuario: ").append(c.getUsuario().getNombreCompleto())
              .append(" | Evento: ").append(c.getEvento().getNombre())
              .append(" | Estado: ").append(c.getEstadoEnum())
              .append(" | Total: $").append(c.getTotal())
              .append("\n");
            totalGeneral += c.getTotal();
        }
        sb.append("\nTOTAL RECAUDADO: $").append(totalGeneral).append("\n");
        sb.append("=============================");
        return sb.toString();
    }

    private String generarReporteEventos() {
        LinkedList<Evento> eventos = gestor.getEventos();
        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE DE EVENTOS =====\n");
        for (Evento e : eventos) {
            sb.append("ID: ").append(e.getIdEvento())
              .append(" | Nombre: ").append(e.getNombre())
              .append(" | Ciudad: ").append(e.getCiudad())
              .append(" | Estado: ").append(e.getEstado())
              .append(" | Fecha: ").append(e.getFechaHora())
              .append("\n");
        }
        sb.append("==============================");
        return sb.toString();
    }

    private String generarReporteIncidencias() {
        LinkedList<Incidencia> incidencias = gestor.getIncidencias();
        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE DE INCIDENCIAS =====\n");
        for (Incidencia i : incidencias) {
            if (!estaEnRango(i.getFecha())) continue;
            sb.append("ID: ").append(i.getIdIncidencia())
              .append(" | Tipo: ").append(i.getTipo())
              .append(" | Entidad: ").append(i.getEntidadAfectada())
              .append(" | Fecha: ").append(i.getFecha())
              .append(" | Desc: ").append(i.getDescripcion())
              .append("\n");
        }
        sb.append("==================================");
        return sb.toString();
    }

    private boolean estaEnRango(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }
}
