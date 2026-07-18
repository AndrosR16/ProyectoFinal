package com.ufide.Farmacia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.Medicamento;

public interface MedicamentoRepository
        extends JpaRepository<Medicamento, Long> {
}