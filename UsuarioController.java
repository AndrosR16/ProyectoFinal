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

import com.ufide.Farmacia.entity.Usuario;
import com.ufide.Farmacia.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // Mostrar todos los usuarios
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios/lista";
    }

    // Mostrar formulario para registrar
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    // Guardar usuario nuevo
    @PostMapping
    public String guardar(
            @Valid @ModelAttribute Usuario usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "usuarios/form";
        }

        service.guardar(usuario, result);

        if (result.hasErrors()) {
            return "usuarios/form";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                "Usuario registrado correctamente");

        return "redirect:/usuarios";
    }

    // Mostrar formulario para editar
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = service.buscarPorId(id).orElse(null);

        if (usuario == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario no fue encontrado");

            return "redirect:/usuarios";
        }

        // Nunca mostrar la contraseña cifrada
        usuario.setPassword("");

        model.addAttribute("usuario", usuario);

        return "usuarios/form";
    }

    // Actualizar usuario
    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute Usuario usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        usuario.setId(id);

        /*
         * Si el único error es que la contraseña está vacía,
         * permitimos continuar para conservar la contraseña anterior.
         */
        if (result.hasErrors()) {

            if (!(result.getFieldErrorCount() == 1
                    && result.hasFieldErrors("password"))) {

                return "usuarios/form";
            }
        }

        service.guardar(usuario, result);

        if (result.hasErrors()) {
            return "usuarios/form";
        }

        redirectAttributes.addFlashAttribute(
                "ok",
                "Usuario actualizado correctamente");

        return "redirect:/usuarios";
    }

    // Eliminar usuario
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (service.buscarPorId(id).isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario no fue encontrado");

            return "redirect:/usuarios";
        }

        service.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Usuario eliminado correctamente");

        return "redirect:/usuarios";
    }

}