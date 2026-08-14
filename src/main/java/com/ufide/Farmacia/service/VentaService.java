package com.ufide.Farmacia.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            Medicamento medicamento = medicamentoService
                    .buscarPorId(item.getMedicamentoId())
                    .orElseThrow(() ->
                            new StockInsuficienteException(item.getNombre()));

            if (medicamento.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        medicamento.getNombre()
                );
            }

            medicamentos.add(medicamento);
        }

        Venta venta = new Venta();

        venta.setUsuarioRegistro(usuarioRegistro);
        venta.setEstado("ACTIVA");

        try {

            venta.setUsuarioNombre(
                    usuarioService
                            .buscarPorUsername(usuarioRegistro)
                            .getNombre()
            );

        } catch (RuntimeException ex) {

            venta.setUsuarioNombre(usuarioRegistro);
        }

        venta.setCompradorNombre(
                form.getCompradorNombre()
        );

        venta.setCompradorIdentificacion(
                form.getCompradorIdentificacion()
        );

        venta.setCompradorCorreo(
                form.getCompradorCorreo()
        );

        venta.setCompradorTelefono(
                form.getCompradorTelefono()
        );

        venta.setMetodoPago(
                form.getMetodoPago()
        );

        double subtotal = 0.0;

        for (int i = 0; i < items.size(); i++) {

            ItemCarrito item = items.get(i);
            Medicamento medicamento = medicamentos.get(i);

            DetalleVenta detalle = new DetalleVenta();

            detalle.setDescripcion(
                    item.getNombre()
            );

            detalle.setPrecioUnitario(
                    item.getPrecio()
            );

            detalle.setCantidad(
                    item.getCantidad()
            );

            detalle.setSubtotal(
                    item.getSubtotal()
            );

            detalle.setMedicamento(
                    medicamento
            );

            venta.agregarDetalle(detalle);

            medicamento.setStock(
                    medicamento.getStock()
                            - item.getCantidad()
            );

            medicamentoService.guardar(
                    medicamento
            );

            subtotal += item.getSubtotal();
        }

        double impuesto = redondear(
                subtotal * IVA
        );

        double total = subtotal + impuesto;

        venta.setSubtotal(
                redondear(subtotal)
        );

        venta.setImpuesto(
                impuesto
        );

        venta.setTotal(
                redondear(total)
        );

        Venta guardada =
                ventaRepository.save(venta);

        carritoService.vaciar();

        return guardada;
    }

    public List<Venta> listarTodas() {

        return ventaRepository
                .findAllByOrderByFechaDesc();
    }

    public List<Venta> listarDeUsuario(
            String usuario) {

        return ventaRepository
                .findByUsuarioRegistroOrderByFechaDesc(
                        usuario
                );
    }

    public Optional<Venta> buscarPorId(
            Long id) {

        return ventaRepository.findById(id);
    }

    @Transactional
    public Venta anularFactura(
            Long id,
            String motivo) {

        Venta venta = ventaRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La factura no existe"
                        ));

        if (venta.isAnulada()) {
            throw new IllegalStateException(
                    "La factura ya se encuentra anulada"
            );
        }

        if (motivo == null
                || motivo.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe indicar el motivo de anulación"
            );
        }

        for (DetalleVenta detalle : venta.getDetalles()) {

            Medicamento medicamento =
                    detalle.getMedicamento();

            if (medicamento == null) {
                continue;
            }

            int cantidadActual =
                    medicamento.getStock();

            int cantidadDevuelta =
                    detalle.getCantidad();

            medicamento.setStock(
                    cantidadActual + cantidadDevuelta
            );

            medicamentoService.guardar(
                    medicamento
            );
        }

        venta.setEstado("ANULADA");

        venta.setMotivoAnulacion(
                motivo.trim()
        );

        venta.setFechaAnulacion(
                LocalDateTime.now()
        );

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentasPorFecha(
            LocalDate fecha) {

        LocalDateTime inicio =
                fecha.atStartOfDay();

        LocalDateTime fin =
                fecha.plusDays(1)
                        .atStartOfDay();

        return ventaRepository
                .findAllByOrderByFechaDesc()
                .stream()
                .filter(Venta::isActiva)
                .filter(venta ->
                        venta.getFecha() != null
                                && !venta.getFecha()
                                .isBefore(inicio)
                                && venta.getFecha()
                                .isBefore(fin))
                .toList();
    }

    @Transactional(readOnly = true)
    public double calcularTotalPorFecha(
            LocalDate fecha) {

        return redondear(
                listarVentasPorFecha(fecha)
                        .stream()
                        .filter(venta ->
                                venta.getTotal() != null)
                        .mapToDouble(
                                Venta::getTotal
                        )
                        .sum()
        );
    }

    @Transactional(readOnly = true)
    public List<MedicamentoMasVendido>
            obtenerMedicamentosMasVendidos(
                    LocalDate desde,
                    LocalDate hasta) {

        LocalDateTime inicio =
                desde.atStartOfDay();

        LocalDateTime fin =
                hasta.plusDays(1)
                        .atStartOfDay();

        Map<String, Integer> cantidades =
                new HashMap<>();

        List<Venta> ventas =
                ventaRepository
                        .findAllByOrderByFechaDesc();

        for (Venta venta : ventas) {

            if (!venta.isActiva()) {
                continue;
            }

            if (venta.getFecha() == null) {
                continue;
            }

            if (venta.getFecha().isBefore(inicio)
                    || !venta.getFecha()
                    .isBefore(fin)) {

                continue;
            }

            for (DetalleVenta detalle :
                    venta.getDetalles()) {

                String nombre =
                        detalle.getDescripcion();

                Integer cantidad =
                        detalle.getCantidad();

                if (nombre == null
                        || cantidad == null) {

                    continue;
                }

                cantidades.merge(
                        nombre,
                        cantidad,
                        Integer::sum
                );
            }
        }

        return cantidades
                .entrySet()
                .stream()
                .map(entry ->
                        new MedicamentoMasVendido(
                                entry.getKey(),
                                entry.getValue()
                        ))
                .sorted((a, b) ->
                        Integer.compare(
                                b.getCantidadVendida(),
                                a.getCantidadVendida()
                        ))
                .toList();
    }

    private double redondear(
            double valor) {

        return Math.round(
                valor * 100.0
        ) / 100.0;
    }

    public static class MedicamentoMasVendido {

        private final String nombre;
        private final Integer cantidadVendida;

        public MedicamentoMasVendido(
                String nombre,
                Integer cantidadVendida) {

            this.nombre = nombre;
            this.cantidadVendida =
                    cantidadVendida;
        }

        public String getNombre() {
            return nombre;
        }

        public Integer getCantidadVendida() {
            return cantidadVendida;
        }
    }
}