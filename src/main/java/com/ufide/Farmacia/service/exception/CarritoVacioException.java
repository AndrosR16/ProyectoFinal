package com.ufide.Farmacia.service.exception;

public class CarritoVacioException extends RuntimeException {

    public CarritoVacioException() {
        super("El carrito está vacío");
    }

    public CarritoVacioException(String mensaje) {
        super(mensaje);
    }
}
