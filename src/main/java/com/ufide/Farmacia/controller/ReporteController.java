package com.ufide.Farmacia.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ufide.Farmacia.service.VentaService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final VentaService ventaService;

    public ReporteController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            Model model) {

        LocalDate fechaConsulta = fecha != null
                ? fecha
                : LocalDate.now();

        LocalDate fechaDesde = desde != null
                ? desde
                : LocalDate.now().minusDays(30);

        LocalDate fechaHasta = hasta != null
                ? hasta
                : LocalDate.now();

        model.addAttribute(
                "fecha",
                fechaConsulta
        );

        model.addAttribute(
                "desde",
                fechaDesde
        );

        model.addAttribute(
                "hasta",
                fechaHasta
        );

        model.addAttribute(
                "ventasDia",
                ventaService.listarVentasPorFecha(fechaConsulta)
        );

        model.addAttribute(
                "totalDia",
                ventaService.calcularTotalPorFecha(fechaConsulta)
        );

        model.addAttribute(
                "medicamentosMasVendidos",
                ventaService.obtenerMedicamentosMasVendidos(
                        fechaDesde,
                        fechaHasta
                )
        );

        return "reportes/index";
    }
}