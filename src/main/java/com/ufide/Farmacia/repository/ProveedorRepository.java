package com.ufide.Farmacia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}