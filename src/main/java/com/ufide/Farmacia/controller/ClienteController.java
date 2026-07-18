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

import com.ufide.Farmacia.entity.Cliente;
import com.ufide.Farmacia.service.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute Cliente cliente,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "clientes/form";
        }

        service.guardar(cliente);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Cliente registrado correctamente"
        );

        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Cliente cliente = service.buscarPorId(id).orElse(null);

        if (cliente == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El cliente no fue encontrado"
            );

            return "redirect:/clientes";
        }

        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute Cliente cliente,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cliente.setId(id);
            return "clientes/form";
        }

        cliente.setId(id);
        service.guardar(cliente);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Cliente actualizado correctamente"
        );

        return "redirect:/clientes";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (service.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El cliente no fue encontrado"
            );

            return "redirect:/clientes";
        }

        service.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Cliente eliminado correctamente"
        );

        return "redirect:/clientes";
    }
}