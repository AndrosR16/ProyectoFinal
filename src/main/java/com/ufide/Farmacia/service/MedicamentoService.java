package com.ufide.Farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.repository.CarritoItemRepository;
import com.ufide.Farmacia.repository.MedicamentoRepository;

@Service
public class MedicamentoService {

    private static final int UMBRAL_STOCK_BAJO = 5;

    private final MedicamentoRepository repository;
    private final CarritoItemRepository carritoItemRepository;

    public MedicamentoService(
            MedicamentoRepository repository,
            CarritoItemRepository carritoItemRepository) {

        this.repository = repository;
        this.carritoItemRepository = carritoItemRepository;
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

    @Transactional
    public void eliminar(Long id) {
        // purga las filas de carrito de cualquier usuario antes de borrar (FK a medicamentos)
        carritoItemRepository.deleteByMedicamentoId(id);
        repository.deleteById(id);
    }
}