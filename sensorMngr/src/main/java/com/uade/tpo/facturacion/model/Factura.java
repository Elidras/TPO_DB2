package com.uade.tpo.facturacion.model;

import java.time.LocalDateTime;
import java.util.List;

public class Factura {
    private Integer id;
    private String emailUsuario;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaVencimiento;
    private Double total;
    private String estado;
    private String descripcion;
    private Double horasSesion;
    private String procesosIncluidos;
    private LocalDateTime createdAt;
    private List<ItemFactura> items;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getHorasSesion() { return horasSesion; }
    public void setHorasSesion(Double horasSesion) { this.horasSesion = horasSesion; }

    public String getProcesosIncluidos() { return procesosIncluidos; }
    public void setProcesosIncluidos(String procesosIncluidos) { this.procesosIncluidos = procesosIncluidos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ItemFactura> getItems() { return items; }
    public void setItems(List<ItemFactura> items) { this.items = items; }
}