package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class Sensor {

    private UUID id;
    private String nombre;          // Nombre o código
    private String tipoSensor;      // temperatura, humedad
    private Double latitud;
    private Double longitud;
    private String ciudad;
    private String pais;
    private String estadoSensor;    // activo, inactivo, falla
    private Date fechaInicioEmision;

    // Constructor completo
    public Sensor(UUID id, String nombre, String tipoSensor, Double latitud, Double longitud,
                  String ciudad, String pais, String estadoSensor, Date fechaInicioEmision) {
        this.id = id;
        this.nombre = nombre;
        this.tipoSensor = tipoSensor;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ciudad = ciudad;
        this.pais = pais;
        this.estadoSensor = estadoSensor;
        this.fechaInicioEmision = fechaInicioEmision;
    }

    // Constructor vacío
    public Sensor() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public void setTipoSensor(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstadoSensor() {
        return estadoSensor;
    }

    public void setEstadoSensor(String estadoSensor) {
        this.estadoSensor = estadoSensor;
    }

    public Date getFechaInicioEmision() {
        return fechaInicioEmision;
    }

    public void setFechaInicioEmision(Date fechaInicioEmision) {
        this.fechaInicioEmision = fechaInicioEmision;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipoSensor='" + tipoSensor + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", ciudad='" + ciudad + '\'' +
                ", pais='" + pais + '\'' +
                ", estadoSensor='" + estadoSensor + '\'' +
                ", fechaInicioEmision=" + fechaInicioEmision +
                '}';
    }
}
