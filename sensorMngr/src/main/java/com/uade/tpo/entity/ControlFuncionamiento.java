package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class ControlFuncionamiento {

    private UUID id;
    private UUID sensorId; 
    private Date fechaRevision;
    private String estadoSensor; 
    private String observaciones;

    public ControlFuncionamiento(UUID id, UUID sensorId, Date fechaRevision, String estadoSensor, String observaciones) {
        this.id = id;
        this.sensorId = sensorId;
        this.fechaRevision = fechaRevision;
        this.estadoSensor = estadoSensor;
        this.observaciones = observaciones;
    }

    public ControlFuncionamiento() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getEstadoSensor() {
        return estadoSensor;
    }

    public void setEstadoSensor(String estadoSensor) {
        this.estadoSensor = estadoSensor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "ControlFuncionamiento{" +
                "id=" + id +
                ", sensorId=" + sensorId +
                ", fechaRevision=" + fechaRevision +
                ", estadoSensor='" + estadoSensor + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
