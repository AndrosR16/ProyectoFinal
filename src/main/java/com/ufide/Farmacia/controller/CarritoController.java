package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.service.CarritoService;
import com.ufide.Farmacia.service.MedicamentoService;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final MedicamentoService medicamentoService;

    public CarritoController(
            CarritoService carritoService,
            MedicamentoService medicamentoService) {

        this.carritoService = carritoService;
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public String mostrarCarrito(Model model) {

        model.addAttribute(
                "items",
                carritoService.listar()
        );

        model.addAttribute(
                "total",
                carritoService.calcularTotal()
        );

        model.addAttribute(
                "cantidadTotal",
                carritoService.calcularCantidadTotal()
        );

        return "carrito/lista";
    }

    @PostMapping("/agregar/{id}")
    public String agregar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1")
            Integer cantidad,
            RedirectAttributes redirectAttributes) {

        Medicamento medicamento =
                medicamentoService.buscarPorId(id)
                        .orElse(null);

        if (medicamento == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El medicamento no fue encontrado"
            );

            return "redirect:/medicamentos";
        }

        if (cantidad == null || cantidad < 1) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "La cantidad debe ser mayor que cero"
            );

            return "redirect:/medicamentos";
        }

        if (medicamento.getStock() <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El medicamento no tiene stock disponible"
            );

            return "redirect:/medicamentos";
        }

        if (cantidad > medicamento.getStock()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "La cantidad supera el stock disponible"
            );

            return "redirect:/medicamentos";
        }

        boolean agregado =
                carritoService.agregar(medicamento, cantidad);

        if (!agregado) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "La cantidad total supera el stock disponible"
            );

            return "redirect:/medicamentos";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                "Medicamento agregado al carrito"
        );

        return "redirect:/medicamentos";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCantidad(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {

        boolean actualizado =
                carritoService.actualizarCantidad(id, cantidad);

        if (!actualizado) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "La cantidad ingresada no es válida"
            );

            return "redirect:/carrito";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                "Cantidad actualizada correctamente"
        );

        return "redirect:/carrito";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        carritoService.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Medicamento eliminado del carrito"
        );

        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciar(
            RedirectAttributes redirectAttributes) {

        carritoService.vaciar();

        redirectAttributes.addFlashAttribute(
                "ok",
                "El carrito fue vaciado"
        );

        return "redirect:/carrito";
    }
}