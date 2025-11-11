package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class Alerta {

    private UUID id;
    private String tipo; // Ej: "sensor" o "climática"
    private UUID sensorId; // Opcional, si aplica
    private Date fechaHora;
    private String descripcion;
    private String estado; // Ej: "activa" o "resuelta"

    // Constructor completo
    public Alerta(UUID id, String tipo, UUID sensorId, Date fechaHora, String descripcion, String estado) {
        this.id = id;
        this.tipo = tipo;
        this.sensorId = sensorId;
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // Constructor vacío
    public Alerta() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Alerta{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", sensorId=" + sensorId +
                ", fechaHora=" + fechaHora +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
