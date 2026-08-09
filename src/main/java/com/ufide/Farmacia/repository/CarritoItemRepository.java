package com.ufide.Farmacia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ufide.Farmacia.entity.CarritoItem;

public interface CarritoItemRepository
        extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByUsuarioOrderByIdAsc(String usuario);

    Optional<CarritoItem> findByUsuarioAndMedicamentoId(String usuario, Long medicamentoId);

    void deleteByUsuario(String usuario);

    void deleteByUsuarioAndMedicamentoId(String usuario, Long medicamentoId);

    void deleteByMedicamentoId(Long medicamentoId);
}
