package com.ufide.Farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.repository.CarritoItemRepository;
import com.ufide.Farmacia.repository.DetalleVentaRepository;
import com.ufide.Farmacia.repository.MedicamentoRepository;

@Service
public class MedicamentoService {

    private static final int UMBRAL_STOCK_BAJO = 5;

    private final MedicamentoRepository repository;
    private final CarritoItemRepository carritoItemRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public MedicamentoService(
            MedicamentoRepository repository,
            CarritoItemRepository carritoItemRepository,
            DetalleVentaRepository detalleVentaRepository) {

        this.repository = repository;
        this.carritoItemRepository = carritoItemRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    public List<Medicamento> listar() {
        return repository.findAll();
    }

    public long contar() {
        return repository.count();
    }

    public long contarStockBajo() {
        return repository.countByStockLessThan(UMBRAL_STOCK_BAJO);
    }

    public List<Medicamento> listarDestacados() {
        return repository.findByDestacadoTrue();
    }

    public Optional<Medicamento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Medicamento guardar(Medicamento medicamento) {
        return repository.save(medicamento);
    }

    public boolean tieneVentas(Long id) {
        return detalleVentaRepository.existsByMedicamentoId(id);
    }

    @Transactional
    public void eliminar(Long id) {

        if (tieneVentas(id)) {
            throw new IllegalStateException("MEDICAMENTO_CON_VENTAS");
        }

        carritoItemRepository.deleteByMedicamentoId(id);

        repository.deleteById(id);
    }
}