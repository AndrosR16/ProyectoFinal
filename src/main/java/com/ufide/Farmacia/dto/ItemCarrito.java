package com.ufide.Farmacia.dto;

public class ItemCarrito {

    private Long medicamentoId;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private Integer stockDisponible;

    public ItemCarrito() {
    }

    public ItemCarrito(
            Long medicamentoId,
            String nombre,
            Double precio,
            Integer cantidad,
            Integer stockDisponible) {

        this.medicamentoId = medicamentoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stockDisponible = stockDisponible;
    }

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Double getSubtotal() {
        return precio * cantidad;
    }
}