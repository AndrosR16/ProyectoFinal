package com.ufide.Farmacia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    boolean existsByMedicamentoId(Long medicamentoId);
}