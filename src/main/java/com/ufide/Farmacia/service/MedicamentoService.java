package com.ufide.Farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.repository.MedicamentoRepository;

@Service
public class MedicamentoService {

    private static final int UMBRAL_STOCK_BAJO = 5;

    private final MedicamentoRepository repository;

    public MedicamentoService(MedicamentoRepository repository) {
        this.repository = repository;
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

    public Optional<Medicamento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Medicamento guardar(Medicamento medicamento) {
        return repository.save(medicamento);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}