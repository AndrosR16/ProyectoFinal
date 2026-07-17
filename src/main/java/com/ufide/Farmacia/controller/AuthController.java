package com.ufide.Farmacia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.Farmacia.dto.RegistroForm;
import com.ufide.Farmacia.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroForm", new RegistroForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registroForm") RegistroForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        usuarioService.validarRegistro(form, result);
        if (result.hasErrors()) {
            return "registro";
        }
        try {
            usuarioService.registrar(form);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            result.rejectValue("username", "duplicado", "No se pudo crear la cuenta, verifique los datos ingresados");
            return "registro";
        }
        redirectAttributes.addFlashAttribute("ok", "Cuenta creada correctamente. Ya puede iniciar sesion.");
        return "redirect:/login";
    }
}
