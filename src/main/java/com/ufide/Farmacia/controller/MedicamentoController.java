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

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.service.MedicamentoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicamentos", service.listar());
        return "medicamentos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        return "medicamentos/form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute Medicamento medicamento,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "medicamentos/form";
        }

        service.guardar(medicamento);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Medicamento registrado correctamente");

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
                    "El medicamento no fue encontrado");

            return "redirect:/medicamentos";
        }

        model.addAttribute("medicamento", medicamento);
        return "medicamentos/form";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute Medicamento medicamento,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            medicamento.setId(id);
            return "medicamentos/form";
        }

        medicamento.setId(id);
        service.guardar(medicamento);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Medicamento actualizado correctamente");

        return "redirect:/medicamentos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (service.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El medicamento no fue encontrado");

            return "redirect:/medicamentos";
        }

        service.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "ok",
                "Medicamento eliminado correctamente");

        return "redirect:/medicamentos";
    }
}