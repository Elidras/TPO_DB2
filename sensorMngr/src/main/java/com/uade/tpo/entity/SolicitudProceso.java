package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class SolicitudProceso {

    private UUID id;
    private String usuario; // You could also link to a Usuario entity
    private Proceso proceso; // Reference to the Proceso entity
    private Date fechaSolicitud;
    private String estado; // pendiente/completado

    // Constructor completo
    public SolicitudProceso(UUID id, String usuario, Proceso proceso, Date fechaSolicitud, String estado) {
        this.id = id;
        this.usuario = usuario;
        this.proceso = proceso;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    // Constructor vacío
    public SolicitudProceso() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "SolicitudProceso{" +
                "id=" + id +
                ", usuario='" + usuario + '\'' +
                ", proceso=" + proceso +
                ", fechaSolicitud=" + fechaSolicitud +
                ", estado='" + estado + '\'' +
                '}';
    }
}
