package com.uade.tpo.facturacion.model;

import java.time.LocalDateTime;

public class SessionData {
    private String sessionId;
    private String emailUsuario;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private long duracionMinutos;

    public SessionData() {}

    public SessionData(String sessionId, String emailUsuario, LocalDateTime inicio, LocalDateTime fin) {
        this.sessionId = sessionId;
        this.emailUsuario = emailUsuario;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public long getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(long duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}