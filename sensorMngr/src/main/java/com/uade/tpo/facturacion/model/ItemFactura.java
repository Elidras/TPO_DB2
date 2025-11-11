package com.uade.tpo.facturacion.model;

public class ItemFactura {
    private Integer id;
    private Integer facturaId;
    private String concepto;
    private String tipoItem;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private String procesoId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public String getProcesoId() { return procesoId; }
    public void setProcesoId(String procesoId) { this.procesoId = procesoId; }
}