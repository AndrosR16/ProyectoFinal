package com.ufide.Farmacia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNombreContainingIgnoreCaseOrIdentificacionContainingIgnoreCase(
            String nombre,
            String identificacion);
}