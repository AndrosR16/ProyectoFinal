package com.ufide.Farmacia.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.ufide.Farmacia.dto.ItemCarrito;
import com.ufide.Farmacia.entity.Medicamento;

@Service
@SessionScope
public class CarritoService {

    private final List<ItemCarrito> items = new ArrayList<>();

    public List<ItemCarrito> listar() {
        return items;
    }

    public boolean agregar(Medicamento medicamento, Integer cantidad) {

        ItemCarrito itemExistente = buscarPorMedicamentoId(
                medicamento.getId()
        );

        if (itemExistente != null) {

            int nuevaCantidad =
                    itemExistente.getCantidad() + cantidad;

            if (nuevaCantidad > medicamento.getStock()) {
                return false;
            }

            itemExistente.setCantidad(nuevaCantidad);
            itemExistente.setStockDisponible(medicamento.getStock());

            return true;
        }

        ItemCarrito nuevoItem = new ItemCarrito(
                medicamento.getId(),
                medicamento.getNombre(),
                medicamento.getPrecio(),
                cantidad,
                medicamento.getStock()
        );

        items.add(nuevoItem);

        return true;
    }

    public boolean actualizarCantidad(
            Long medicamentoId,
            Integer cantidad) {

        ItemCarrito item = buscarPorMedicamentoId(medicamentoId);

        if (item == null) {
            return false;
        }

        if (cantidad < 1 ||
                cantidad > item.getStockDisponible()) {
            return false;
        }

        item.setCantidad(cantidad);

        return true;
    }

    public void eliminar(Long medicamentoId) {

        items.removeIf(item ->
                item.getMedicamentoId().equals(medicamentoId));
    }

    public void vaciar() {
        items.clear();
    }

    public Double calcularTotal() {

        return items.stream()
                .mapToDouble(ItemCarrito::getSubtotal)
                .sum();
    }

    public Integer calcularCantidadTotal() {

        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    private ItemCarrito buscarPorMedicamentoId(
            Long medicamentoId) {

        return items.stream()
                .filter(item ->
                        item.getMedicamentoId()
                                .equals(medicamentoId))
                .findFirst()
                .orElse(null);
    }
}