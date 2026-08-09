package com.ufide.Farmacia.service.exception;

public class StockInsuficienteException extends RuntimeException {

    private final String medicamento;

    public StockInsuficienteException(String medicamento) {
        super("Stock insuficiente para el medicamento: " + medicamento);
        this.medicamento = medicamento;
    }

    public String getMedicamento() {
        return medicamento;
    }
}
