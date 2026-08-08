package com.ufide.Farmacia.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public AuthController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
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
            result.rejectValue("username", "validacion.registro.duplicado.general", "No se pudo crear la cuenta, verifique los datos ingresados");
            return "registro";
        }
        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.registro.exitoso", null, LocaleContextHolder.getLocale()));
        return "redirect:/login";
    }
}
