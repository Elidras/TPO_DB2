package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class HistorialEjecucion {

    private UUID id;
    private SolicitudProceso solicitud; // Reference to the solicitud
    private Date fechaEjecucion;
    private String resultado;
    private String estado; // activo/inactivo, completado, etc.

    // Constructor completo
    public HistorialEjecucion(UUID id, SolicitudProceso solicitud, Date fechaEjecucion, String resultado, String estado) {
        this.id = id;
        this.solicitud = solicitud;
        this.fechaEjecucion = fechaEjecucion;
        this.resultado = resultado;
        this.estado = estado;
    }

    // Constructor vacío
    public HistorialEjecucion() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SolicitudProceso getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SolicitudProceso solicitud) {
        this.solicitud = solicitud;
    }

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "HistorialEjecucion{" +
                "id=" + id +
                ", solicitud=" + solicitud +
                ", fechaEjecucion=" + fechaEjecucion +
                ", resultado='" + resultado + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
