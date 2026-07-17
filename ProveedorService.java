
package com.ufide.Farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ufide.Farmacia.entity.Proveedor;
import com.ufide.Farmacia.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public List<Proveedor> listar() {
        return repository.findAll();
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Proveedor guardar(Proveedor proveedor) {

        boolean existe;

        if (proveedor.getId() == null) {
            existe = repository.existsByCorreo(proveedor.getCorreo());
        } else {
            existe = repository.existsByCorreoAndIdNot(
                    proveedor.getCorreo(),
                    proveedor.getId());
        }

        if (existe) {
            throw new RuntimeException("Ya existe un proveedor con ese correo.");
        }

        return repository.save(proveedor);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

}