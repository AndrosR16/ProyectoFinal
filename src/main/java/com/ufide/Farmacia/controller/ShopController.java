package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ufide.Farmacia.service.CarritoService;
import com.ufide.Farmacia.service.MedicamentoService;

@Controller
public class ShopController {

    private final MedicamentoService medicamentoService;
    private final CarritoService carritoService;

    public ShopController(
            MedicamentoService medicamentoService,
            CarritoService carritoService) {

        this.medicamentoService = medicamentoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/shop")
    public String mostrarTienda(Model model) {
        model.addAttribute("medicamentos", medicamentoService.listar());
        model.addAttribute("destacados", medicamentoService.listarDestacados());
        model.addAttribute("cantidadCarrito", carritoService.calcularCantidadTotal());
        return "shop/lista";
    }
}
