package com.uade.tpo.entity;

import java.util.Date;

public class Movimiento {

    private String descripcion;
    private Date fecha;
    private Double monto;
    private String tipo; // Ej: "ingreso", "egreso"

    public Movimiento() {}

    public Movimiento(String descripcion, Date fecha, Double monto, String tipo) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.monto = monto;
        this.tipo = tipo;
    }

    // Getters y setters
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
