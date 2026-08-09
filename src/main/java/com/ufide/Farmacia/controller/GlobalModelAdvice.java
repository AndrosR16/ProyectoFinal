package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ufide.Farmacia.service.CarritoService;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAdvice {

    private final CarritoService carritoService;

    public GlobalModelAdvice(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @ModelAttribute("cantidadCarritoGlobal")
    public Integer cantidadCarritoGlobal() {
        return carritoService.calcularCantidadTotal();
    }
}
