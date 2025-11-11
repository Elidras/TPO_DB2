package com.uade.tpo.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Factura {

    private UUID id;
    private User usuario; // Reference to the user
    private Date fechaEmision;
    private List<Proceso> procesosFacturados; // List of billed processes
    private String estado; // pendiente, pagada, vencida

    // Constructor completo
    public Factura(UUID id, User usuario, Date fechaEmision, List<Proceso> procesosFacturados, String estado) {
        this.id = id;
        this.usuario = usuario;
        this.fechaEmision = fechaEmision;
        this.procesosFacturados = procesosFacturados;
        this.estado = estado;
    }

    // Constructor vacío
    public Factura() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public List<Proceso> getProcesosFacturados() {
        return procesosFacturados;
    }

    public void setProcesosFacturados(List<Proceso> procesosFacturados) {
        this.procesosFacturados = procesosFacturados;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", fechaEmision=" + fechaEmision +
                ", procesosFacturados=" + procesosFacturados +
                ", estado='" + estado + '\'' +
                '}';
    }
}
