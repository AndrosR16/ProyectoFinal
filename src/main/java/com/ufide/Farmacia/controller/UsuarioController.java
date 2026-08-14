package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.Farmacia.dto.RegistroForm;
import com.ufide.Farmacia.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioService.listarTodos()
        );

        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute(
                "registroForm",
                new RegistroForm()
        );

        model.addAttribute(
                "rolSeleccionado",
                "EMPLEADO"
        );

        return "usuarios/form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("registroForm") RegistroForm form,
            BindingResult result,
            @RequestParam String rol,
            Model model,
            RedirectAttributes redirectAttributes) {

        usuarioService.validarRegistro(
                form,
                result
        );

        if (result.hasErrors()) {

            model.addAttribute(
                    "rolSeleccionado",
                    rol
            );

            return "usuarios/form";
        }

        try {

            usuarioService.registrarConRol(
                    form,
                    rol
            );

            redirectAttributes.addFlashAttribute(
                    "ok",
                    "Usuario registrado correctamente"
            );

            return "redirect:/usuarios";

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "rolSeleccionado",
                    rol
            );

            model.addAttribute(
                    "error",
                    ex.getMessage()
            );

            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/rol")
    public String cambiarRol(
            @PathVariable Long id,
            @RequestParam String rol,
            RedirectAttributes redirectAttributes) {

        try {

            usuarioService.cambiarRol(
                    id,
                    rol
            );

            redirectAttributes.addFlashAttribute(
                    "ok",
                    "Rol actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/usuarios";
    }
}