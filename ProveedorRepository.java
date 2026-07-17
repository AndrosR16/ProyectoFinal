package com.ufide.Farmacia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);

}