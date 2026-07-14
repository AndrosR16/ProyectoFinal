package com.ufide.Farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufide.Farmacia.entity.Proveedor;
import com.ufide.Farmacia.repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository repository;

    public List<Proveedor> listar() {
        return repository.findAll();
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Proveedor guardar(Proveedor proveedor) {
        return repository.save(proveedor);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}