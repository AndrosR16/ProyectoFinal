package com.ufide.Farmacia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufide.Farmacia.dto.ItemCarrito;
import com.ufide.Farmacia.entity.CarritoItem;
import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.repository.CarritoItemRepository;

@Service
public class CarritoService {

    private final CarritoItemRepository repository;

    public CarritoService(CarritoItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ItemCarrito> listar() {

        String usuario = usuarioActual();

        if (usuario == null) {
            return List.of();
        }

        List<CarritoItem> registros =
                repository.findByUsuarioOrderByIdAsc(usuario);

        List<ItemCarrito> items = new ArrayList<>();

        for (CarritoItem registro : registros) {

            Medicamento medicamento = registro.getMedicamento();

            items.add(new ItemCarrito(
                    medicamento.getId(),
                    medicamento.getNombre(),
                    medicamento.getPrecio(),
                    registro.getCantidad(),
                    medicamento.getStock()
            ));
        }

        return items;
    }

    @Transactional
    public boolean agregar(Medicamento medicamento, Integer cantidad) {

        String usuario = usuarioActual();

        if (usuario == null) {
            return false;
        }

        Optional<CarritoItem> existente = repository
                .findByUsuarioAndMedicamentoId(usuario, medicamento.getId());

        if (existente.isPresent()) {

            CarritoItem item = existente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;

            if (nuevaCantidad > medicamento.getStock()) {
                return false;
            }

            item.setCantidad(nuevaCantidad);
            repository.save(item);

            return true;
        }

        CarritoItem nuevoItem = new CarritoItem(usuario, medicamento, cantidad);
        repository.save(nuevoItem);

        return true;
    }

    @Transactional
    public boolean actualizarCantidad(
            Long medicamentoId,
            Integer cantidad) {

        String usuario = usuarioActual();

        if (usuario == null) {
            return false;
        }

        Optional<CarritoItem> existente = repository
                .findByUsuarioAndMedicamentoId(usuario, medicamentoId);

        if (existente.isEmpty()) {
            return false;
        }

        CarritoItem item = existente.get();

        if (cantidad < 1 ||
                cantidad > item.getMedicamento().getStock()) {
            return false;
        }

        item.setCantidad(cantidad);
        repository.save(item);

        return true;
    }

    @Transactional
    public void eliminar(Long medicamentoId) {

        String usuario = usuarioActual();

        if (usuario == null) {
            return;
        }

        repository.deleteByUsuarioAndMedicamentoId(usuario, medicamentoId);
    }

    @Transactional
    public void vaciar() {

        String usuario = usuarioActual();

        if (usuario == null) {
            return;
        }

        repository.deleteByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public Double calcularTotal() {

        return listar().stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    @Transactional(readOnly = true)
    public Integer calcularCantidadTotal() {

        return listar().stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    @Transactional(readOnly = true)
    public boolean estaVacio() {
        return listar().isEmpty();
    }

    // Anónimo (auth nula, no autenticada o AnonymousAuthenticationToken) no tiene carrito propio en BD
    private String usuarioActual() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return auth.getName();
    }
}
