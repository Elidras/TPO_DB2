package com.uade.tpo.entity;

import java.util.Date;
import java.util.UUID;

public class Pago {

    private UUID id;
    private Factura factura; // Reference to the invoice
    private Date fechaPago;
    private Double montoPagado;
    private String metodoPago; // Ejemplo: efectivo, tarjeta, transferencia

    // Constructor completo
    public Pago(UUID id, Factura factura, Date fechaPago, Double montoPagado, String metodoPago) {
        this.id = id;
        this.factura = factura;
        this.fechaPago = fechaPago;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
    }

    // Constructor vacío
    public Pago() {}

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(Double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", factura=" + factura +
                ", fechaPago=" + fechaPago +
                ", montoPagado=" + montoPagado +
                ", metodoPago='" + metodoPago + '\'' +
                '}';
    }
}
