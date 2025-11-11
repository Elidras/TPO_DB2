package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class Medicion {

    private UUID sensorId;
    private String sensorName;
    private String pais;
    private String ciudad;
    private Date fechaHora;
    private UUID medicionId;
    private Integer temperatura;
    private Integer humedad;
    private String estado;

    public Medicion() {
    }

    public Medicion(UUID sensorId, String sensorName, String pais, String ciudad,
                    Date fechaHora, UUID medicionId, Integer temperatura,
                    Integer humedad, String estado) {
        this.sensorId = sensorId;
        this.sensorName = sensorName;
        this.pais = pais;
        this.ciudad = ciudad;
        this.fechaHora = fechaHora;
        this.medicionId = medicionId;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.estado = estado;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public UUID getMedicionId() {
        return medicionId;
    }

    public void setMedicionId(UUID medicionId) {
        this.medicionId = medicionId;
    }

    public Integer getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Integer temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getHumedad() {
        return humedad;
    }

    public void setHumedad(Integer humedad) {
        this.humedad = humedad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Medicion{" +
                "medicionId=" + medicionId +
                ", sensorId=" + sensorId +
                ", fechaHora=" + fechaHora +
                ", temperatura=" + temperatura +
                ", humedad=" + humedad +
                ", estado='" + estado + '\'' +
                '}';
    }
}