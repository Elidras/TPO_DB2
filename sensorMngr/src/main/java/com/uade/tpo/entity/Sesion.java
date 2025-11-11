package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class Sesion {

    private UUID id;
    private String usuario;
    private String rol;
    private Date fechaHoraInicio;
    private Date fechaHoraCierre;
    private String estadoActual; // activa, inactiva

    // Constructor completo
    public Sesion(UUID id, String usuario, String rol, Date fechaHoraInicio, Date fechaHoraCierre, String estadoActual) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraCierre = fechaHoraCierre;
        this.estadoActual = estadoActual;
    }

    // Constructor vacío
    public Sesion() {}

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Date getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(Date fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Date getFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public void setFechaHoraCierre(Date fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    @Override
    public String toString() {
        return "Sesion{" +
                "id=" + id +
                ", usuario='" + usuario + '\'' +
                ", rol='" + rol + '\'' +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraCierre=" + fechaHoraCierre +
                ", estadoActual='" + estadoActual + '\'' +
                '}';
    }
}
