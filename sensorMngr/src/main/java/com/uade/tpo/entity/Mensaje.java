package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class Mensaje {

    private UUID id;
    private String remitente; // Usuario que envía
    private String destinatario; // Usuario o Grupo
    private Date fechaHora;
    private String contenido;
    private String tipo; // privado o grupal

    // Constructor completo
    public Mensaje(UUID id, String remitente, String destinatario, Date fechaHora, String contenido, String tipo) {
        this.id = id;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.fechaHora = fechaHora;
        this.contenido = contenido;
        this.tipo = tipo;
    }

    // Constructor vacío
    public Mensaje() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "id=" + id +
                ", remitente='" + remitente + '\'' +
                ", destinatario='" + destinatario + '\'' +
                ", fechaHora=" + fechaHora +
                ", contenido='" + contenido + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
