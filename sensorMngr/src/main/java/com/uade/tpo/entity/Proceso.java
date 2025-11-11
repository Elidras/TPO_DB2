package com.uade.tpo.entity;

import java.util.UUID;

public class Proceso {

    private UUID id;
    private String nombre;
    private String descripcion;
    private String tipoProceso;
    private Double costo;

    // Constructor completo
    public Proceso(UUID id, String nombre, String descripcion, String tipoProceso, Double costo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoProceso = tipoProceso;
        this.costo = costo;
    }

    // Constructor vacío
    public Proceso() {}

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoProceso() {
        return tipoProceso;
    }

    public void setTipoProceso(String tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        return "Proceso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tipoProceso='" + tipoProceso + '\'' +
                ", costo=" + costo +
                '}';
    }
}
