package com.ufide.Farmacia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.Farmacia.dto.FacturaForm;
import com.ufide.Farmacia.dto.ItemCarrito;
import com.ufide.Farmacia.entity.DetalleVenta;
import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.entity.Venta;
import com.ufide.Farmacia.repository.VentaRepository;
import com.ufide.Farmacia.service.exception.CarritoVacioException;
import com.ufide.Farmacia.service.exception.StockInsuficienteException;

@Service
public class VentaService {

    private static final double IVA = 0.13;

    private final VentaRepository ventaRepository;
    private final CarritoService carritoService;
    private final MedicamentoService medicamentoService;
    private final UsuarioService usuarioService;

    public VentaService(
            VentaRepository ventaRepository,
            CarritoService carritoService,
            MedicamentoService medicamentoService,
            UsuarioService usuarioService) {

        this.ventaRepository = ventaRepository;
        this.carritoService = carritoService;
        this.medicamentoService = medicamentoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Venta generarFactura(FacturaForm form, String usuarioRegistro) {

        List<ItemCarrito> items = carritoService.listar();

        if (items.isEmpty()) {
            throw new CarritoVacioException();
        }

        List<Medicamento> medicamentos = new ArrayList<>();

        for (ItemCarrito item : items) {

            Medicamento medicamento = medicamentoService.buscarPorId(item.getMedicamentoId())
                    .orElseThrow(() -> new StockInsuficienteException(item.getNombre()));

            if (medicamento.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(medicamento.getNombre());
            }

            medicamentos.add(medicamento);
        }

        Venta venta = new Venta();
        venta.setUsuarioRegistro(usuarioRegistro);

        try {
            venta.setUsuarioNombre(usuarioService.buscarPorUsername(usuarioRegistro).getNombre());
        } catch (RuntimeException ex) {
            venta.setUsuarioNombre(usuarioRegistro);
        }

        venta.setCompradorNombre(form.getCompradorNombre());
        venta.setCompradorIdentificacion(form.getCompradorIdentificacion());
        venta.setCompradorCorreo(form.getCompradorCorreo());
        venta.setCompradorTelefono(form.getCompradorTelefono());
        venta.setMetodoPago(form.getMetodoPago());

        double subtotal = 0.0;

        for (int i = 0; i < items.size(); i++) {

            ItemCarrito item = items.get(i);
            Medicamento medicamento = medicamentos.get(i);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setDescripcion(item.getNombre());
            detalle.setPrecioUnitario(item.getPrecio());
            detalle.setCantidad(item.getCantidad());
            detalle.setSubtotal(item.getSubtotal());
            detalle.setMedicamento(medicamento);

            venta.agregarDetalle(detalle);

            medicamento.setStock(medicamento.getStock() - item.getCantidad());
            medicamentoService.guardar(medicamento);

            subtotal += item.getSubtotal();
        }

        double impuesto = redondear(subtotal * IVA);
        double total = subtotal + impuesto;

        venta.setSubtotal(redondear(subtotal));
        venta.setImpuesto(impuesto);
        venta.setTotal(redondear(total));

        Venta guardada = ventaRepository.save(venta);

        carritoService.vaciar();

        return guardada;
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAllByOrderByFechaDesc();
    }

    public List<Venta> listarDeUsuario(String usuario) {
        return ventaRepository.findByUsuarioRegistroOrderByFechaDesc(usuario);
    }

    public Optional<Venta> buscarPorId(Long id) {
        return ventaRepository.findById(id);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
