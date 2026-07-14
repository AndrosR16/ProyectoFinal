package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.Farmacia.entity.Proveedor;
import com.ufide.Farmacia.service.ProveedorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

private final ProveedorService service;

public ProveedorController(ProveedorService service) {
    this.service = service;
}

    // Mostrar todos los proveedores
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", service.listar());
        return "proveedores/lista";
    }

    // Mostrar formulario para registrar
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/form";
    }

    // Guardar un proveedor nuevo
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute Proveedor proveedor,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "proveedores/form";
        }

        service.guardar(proveedor);
        redirectAttributes.addFlashAttribute(
                "ok",
                "Proveedor registrado correctamente");

        return "redirect:/proveedores";
    }

    // Mostrar formulario para editar
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Proveedor proveedor = service.buscarPorId(id).orElse(null);

        if (proveedor == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El proveedor no fue encontrado");

            return "redirect:/proveedores";
        }

        model.addAttribute("proveedor", proveedor);
        return "proveedores/form";
    }

    // Actualizar proveedor
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute Proveedor proveedor,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            proveedor.setId(id);
            return "proveedores/form";
        }

        proveedor.setId(id);
        service.guardar(proveedor);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Proveedor actualizado correctamente");

        return "redirect:/proveedores";
    }

    // Eliminar proveedor
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (service.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El proveedor no fue encontrado");

            return "redirect:/proveedores";
        }

        service.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Proveedor eliminado correctamente");

        return "redirect:/proveedores";
    }
}