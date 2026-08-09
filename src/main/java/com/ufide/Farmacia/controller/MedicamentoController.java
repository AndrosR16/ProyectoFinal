package com.ufide.Farmacia.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.service.MedicamentoService;
import com.ufide.Farmacia.service.ProveedorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;
    private final ProveedorService proveedorService;
    private final MessageSource messageSource;

    public MedicamentoController(
            MedicamentoService service,
            ProveedorService proveedorService,
            MessageSource messageSource) {

        this.service = service;
        this.proveedorService = proveedorService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicamentos", service.listar());
        return "medicamentos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        model.addAttribute("proveedores", proveedorService.listar());
        return "medicamentos/form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute Medicamento medicamento,
            BindingResult result,
            @RequestParam(name = "proveedorId", required = false) Long proveedorId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("proveedores", proveedorService.listar());
            return "medicamentos/form";
        }

        medicamento.setProveedor(
                proveedorId != null ? proveedorService.buscarPorId(proveedorId).orElse(null) : null);

        service.guardar(medicamento);

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.medicamento.registrado", null, LocaleContextHolder.getLocale()));

        return "redirect:/medicamentos";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Medicamento medicamento = service.buscarPorId(id).orElse(null);

        if (medicamento == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("flash.medicamento.no.encontrado", null, LocaleContextHolder.getLocale()));

            return "redirect:/medicamentos";
        }

        model.addAttribute("medicamento", medicamento);
        model.addAttribute("proveedores", proveedorService.listar());
        return "medicamentos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute Medicamento medicamento,
            BindingResult result,
            @RequestParam(name = "proveedorId", required = false) Long proveedorId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            medicamento.setId(id);
            model.addAttribute("proveedores", proveedorService.listar());
            return "medicamentos/form";
        }

        medicamento.setId(id);
        medicamento.setProveedor(
                proveedorId != null ? proveedorService.buscarPorId(proveedorId).orElse(null) : null);
        service.guardar(medicamento);

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.medicamento.actualizado", null, LocaleContextHolder.getLocale()));

        return "redirect:/medicamentos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (service.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("flash.medicamento.no.encontrado", null, LocaleContextHolder.getLocale()));

            return "redirect:/medicamentos";
        }

        service.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "ok",
                messageSource.getMessage("flash.medicamento.eliminado", null, LocaleContextHolder.getLocale()));

        return "redirect:/medicamentos";
    }
}