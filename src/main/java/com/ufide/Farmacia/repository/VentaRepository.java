package com.ufide.Farmacia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaDesc();

    List<Venta> findByUsuarioRegistroOrderByFechaDesc(String usuarioRegistro);
}
