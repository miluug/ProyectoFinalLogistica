package org.example.model;

/**
 * Interfaz objetivo (Target) del patrón Adapter.
 * Define el contrato que el sistema espera para generar reportes,
 * independientemente del formato de salida (PDF, CSV, texto, etc.).
 */
public interface IReporte {

    /** Genera el reporte y retorna su contenido como String. */
    String generar();

    /** Retorna el nombre o título del reporte. */
    String getTitulo();
}
