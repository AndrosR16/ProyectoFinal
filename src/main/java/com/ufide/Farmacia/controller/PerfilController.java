package com.ufide.Farmacia.controller;

import java.security.Principal;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ufide.Farmacia.dto.CambioPasswordForm;
import com.ufide.Farmacia.dto.PerfilForm;
import com.ufide.Farmacia.entity.Usuario;
import com.ufide.Farmacia.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class PerfilController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public PerfilController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        Usuario usuario = usuarioService.buscarPorUsername(principal.getName());

        model.addAttribute("username", usuario.getUsername());
        model.addAttribute("perfilForm", aPerfilForm(usuario));
        model.addAttribute("cambioPasswordForm", new CambioPasswordForm());

        return "perfil/form";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(
            @Valid @ModelAttribute("perfilForm") PerfilForm form,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        usuarioService.actualizarPerfil(principal.getName(), form, result);

        if (result.hasErrors()) {
            model.addAttribute("username", principal.getName());
            model.addAttribute("cambioPasswordForm", new CambioPasswordForm());
            return "perfil/form";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.perfil.actualizado", null, LocaleContextHolder.getLocale()));

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/password")
    public String cambiarPassword(
            @Valid @ModelAttribute("cambioPasswordForm") CambioPasswordForm form,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        usuarioService.cambiarPassword(principal.getName(), form, result);

        if (result.hasErrors()) {
            Usuario usuario = usuarioService.buscarPorUsername(principal.getName());

            model.addAttribute("username", usuario.getUsername());
            model.addAttribute("perfilForm", aPerfilForm(usuario));
            return "perfil/form";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.perfil.password.actualizada", null, LocaleContextHolder.getLocale()));

        return "redirect:/perfil";
    }

    private PerfilForm aPerfilForm(Usuario usuario) {
        PerfilForm form = new PerfilForm();
        form.setNombre(usuario.getNombre());
        form.setCorreo(usuario.getCorreo());
        return form;
    }
}
