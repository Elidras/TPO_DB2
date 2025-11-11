package com.uade.tpo.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CuentaCorriente {

    private UUID id;
    private User usuario; // Reference to the user
    private Double saldoActual;
    private List<Movimiento> historialMovimientos;

    // Constructor completo
    public CuentaCorriente(UUID id, User usuario, Double saldoActual, List<Movimiento> historialMovimientos) {
        this.id = id;
        this.usuario = usuario;
        this.saldoActual = saldoActual;
        this.historialMovimientos = historialMovimientos;
    }

    // Constructor vacío
    public CuentaCorriente() {
        this.historialMovimientos = new ArrayList<>();
    }

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

    public Double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(Double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public List<Movimiento> getHistorialMovimientos() {
        return historialMovimientos;
    }

    public void setHistorialMovimientos(List<Movimiento> historialMovimientos) {
        this.historialMovimientos = historialMovimientos;
    }

    @Override
    public String toString() {
        return "CuentaCorriente{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", saldoActual=" + saldoActual +
                ", historialMovimientos=" + historialMovimientos +
                '}';
    }
}
