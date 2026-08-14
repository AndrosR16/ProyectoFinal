package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ufide.Farmacia.service.MedicamentoService;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final MedicamentoService medicamentoService;

    public InventarioController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "medicamentos",
                medicamentoService.listar()
        );

        model.addAttribute(
                "totalMedicamentos",
                medicamentoService.contar()
        );

        model.addAttribute(
                "stockBajo",
                medicamentoService.contarStockBajo()
        );

        return "inventario/lista";
    }
}