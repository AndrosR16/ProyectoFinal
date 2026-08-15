package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ufide.Farmacia.service.ClienteService;
import com.ufide.Farmacia.service.MedicamentoService;
import com.ufide.Farmacia.service.ProveedorService;

@Controller
public class HomeController {

    private final MedicamentoService medicamentoService;
    private final ClienteService clienteService;
    private final ProveedorService proveedorService;

    public HomeController(
            MedicamentoService medicamentoService,
            ClienteService clienteService,
            ProveedorService proveedorService) {

        this.medicamentoService = medicamentoService;
        this.clienteService = clienteService;
        this.proveedorService = proveedorService;
    }

    @GetMapping("/")
    public String mostrarInicio(Model model) {

        model.addAttribute(
                "totalMedicamentos",
                medicamentoService.contar()
        );

        model.addAttribute(
                "destacados",
                medicamentoService.listarDestacados()
        );

        return "home";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {

        model.addAttribute(
                "totalMedicamentos",
                medicamentoService.contar()
        );

        model.addAttribute(
                "medicamentosStockBajo",
                medicamentoService.contarStockBajo()
        );

        model.addAttribute(
                "totalClientes",
                clienteService.contar()
        );

        model.addAttribute(
                "totalProveedores",
                proveedorService.contar()
        );

        return "dashboard";
    }

    @GetMapping("/acceso-denegado")
    public String mostrarAccesoDenegado() {
        return "acceso-denegado";
    }
}