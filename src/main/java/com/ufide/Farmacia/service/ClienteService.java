package com.ufide.Farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ufide.Farmacia.entity.Cliente;
import com.ufide.Farmacia.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> listar() {
        return repository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        return repository.save(cliente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}