package com.ufide.Farmacia.controller;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public CarritoController(
            CarritoService carritoService,
            MedicamentoService medicamentoService,
            MessageSource messageSource) {

        this.carritoService = carritoService;
        this.medicamentoService = medicamentoService;
        this.messageSource = messageSource;
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
                    messageSource.getMessage("flash.medicamento.no.encontrado", null, LocaleContextHolder.getLocale())
            );

            return "redirect:/shop";
        }

        if (cantidad == null || cantidad < 1) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("flash.carrito.cantidad.invalida", null, LocaleContextHolder.getLocale())
            );

            return "redirect:/shop";
        }

        if (medicamento.getStock() <= 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("flash.carrito.sin.stock", null, LocaleContextHolder.getLocale())
            );

            return "redirect:/shop";
        }

        if (cantidad > medicamento.getStock()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("flash.carrito.cantidad.excede.stock", null, LocaleContextHolder.getLocale())
            );

            return "redirect:/shop";
        }

        boolean agregado =
                agregarConReintento(medicamento, cantidad);

        if (!agregado) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("flash.carrito.cantidad.total.excede.stock", null, LocaleContextHolder.getLocale())
            );

            return "redirect:/shop";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.carrito.agregado", null, LocaleContextHolder.getLocale())
        );

        return "redirect:/shop";
    }

    /* La constraint única (usuario, medicamento) rechaza el INSERT cuando dos
       peticiones del mismo usuario llegan a la vez por el mismo medicamento
       (doble clic, dos pestañas). El reintento corre en transacción nueva y
       entra por la rama de "ya existe", que suma cantidades. */
    private boolean agregarConReintento(Medicamento medicamento, Integer cantidad) {

        for (int intento = 0; intento < 2; intento++) {
            try {
                return carritoService.agregar(medicamento, cantidad);
            } catch (DataIntegrityViolationException ex) {
                // el siguiente intento ya encuentra la fila creada por la otra petición
            }
        }

        return false;
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
                    messageSource.getMessage("flash.carrito.cantidad.no.valida", null, LocaleContextHolder.getLocale())
            );

            return "redirect:/carrito";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.carrito.actualizado", null, LocaleContextHolder.getLocale())
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
                messageSource.getMessage("flash.carrito.eliminado", null, LocaleContextHolder.getLocale())
        );

        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciar(
            RedirectAttributes redirectAttributes) {

        carritoService.vaciar();

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.carrito.vaciado", null, LocaleContextHolder.getLocale())
        );

        return "redirect:/carrito";
    }
}