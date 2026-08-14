package com.ufide.Farmacia.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

import com.ufide.Farmacia.dto.FacturaForm;
import com.ufide.Farmacia.entity.Venta;
import com.ufide.Farmacia.service.CarritoService;
import com.ufide.Farmacia.service.ClienteService;
import com.ufide.Farmacia.service.VentaService;
import com.ufide.Farmacia.service.exception.CarritoVacioException;
import com.ufide.Farmacia.service.exception.StockInsuficienteException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    private final VentaService ventaService;
    private final CarritoService carritoService;
    private final ClienteService clienteService;
    private final MessageSource messageSource;

    public FacturaController(
            VentaService ventaService,
            CarritoService carritoService,
            ClienteService clienteService,
            MessageSource messageSource) {

        this.ventaService = ventaService;
        this.carritoService = carritoService;
        this.clienteService = clienteService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {

        if (esPersonalFarmacia(auth)) {

            model.addAttribute(
                    "facturas",
                    ventaService.listarTodas()
            );

        } else {

            model.addAttribute(
                    "facturas",
                    ventaService.listarDeUsuario(auth.getName())
            );
        }

        return "factura/lista";
    }

    @GetMapping("/nueva")
    public String mostrarCheckout(
            Model model,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        if (carritoService.estaVacio()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    mensaje("flash.factura.carrito.vacio")
            );

            return "redirect:/carrito";
        }

        model.addAttribute(
                "facturaForm",
                new FacturaForm()
        );

        model.addAttribute(
                "items",
                carritoService.listar()
        );

        model.addAttribute(
                "total",
                carritoService.calcularTotal()
        );

        if (esPersonalFarmacia(auth)) {
            model.addAttribute(
                    "clientes",
                    clienteService.listar()
            );
        }

        return "factura/checkout";
    }

    @PostMapping
    public String generar(
            @Valid @ModelAttribute("facturaForm") FacturaForm form,
            BindingResult result,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "items",
                    carritoService.listar()
            );

            model.addAttribute(
                    "total",
                    carritoService.calcularTotal()
            );

            if (esPersonalFarmacia(auth)) {
                model.addAttribute(
                        "clientes",
                        clienteService.listar()
                );
            }

            return "factura/checkout";
        }

        try {

            Venta venta = ventaService.generarFactura(
                    form,
                    auth.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "ok",
                    mensaje("flash.factura.generada")
            );

            return "redirect:/facturas/" + venta.getId();

        } catch (CarritoVacioException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    mensaje("flash.factura.carrito.vacio")
            );

            return "redirect:/carrito";

        } catch (StockInsuficienteException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    mensaje("flash.factura.stock.insuficiente")
            );

            return "redirect:/carrito";
        }
    }

    @GetMapping("/{id}")
    public String verDetalle(
            @PathVariable Long id,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        Venta venta = ventaService
                .buscarPorId(id)
                .orElse(null);

        if (venta == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    mensaje("flash.factura.no.encontrada")
            );

            return "redirect:/facturas";
        }

        if (!esPersonalFarmacia(auth)
                && !venta.getUsuarioRegistro().equals(auth.getName())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    mensaje("flash.factura.sin.acceso")
            );

            return "redirect:/facturas";
        }

        model.addAttribute(
                "factura",
                venta
        );

        return "factura/detalle";
    }

    @PostMapping("/{id}/anular")
    public String anularFactura(
            @PathVariable Long id,
            @RequestParam String motivo,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        if (!esAdmin(auth)) {
            return "redirect:/acceso-denegado";
        }

        try {

            ventaService.anularFactura(
                    id,
                    motivo
            );

            redirectAttributes.addFlashAttribute(
                    "ok",
                    "La factura fue anulada correctamente y el inventario fue restaurado."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/facturas/" + id;
    }

    private boolean esPersonalFarmacia(Authentication auth) {

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol ->
                        rol.equals("ROLE_ADMIN")
                                || rol.equals("ROLE_EMPLEADO"));
    }

    private boolean esAdmin(Authentication auth) {

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private String mensaje(String clave) {

        return messageSource.getMessage(
                clave,
                null,
                LocaleContextHolder.getLocale()
        );
    }
}