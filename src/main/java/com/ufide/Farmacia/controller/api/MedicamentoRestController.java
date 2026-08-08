package com.ufide.Farmacia.controller.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ufide.Farmacia.entity.Medicamento;
import com.ufide.Farmacia.service.MedicamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoRestController {

    private final MedicamentoService service;

    public MedicamentoRestController(MedicamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Medicamento>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Medicamento> guardar(
            @Valid @RequestBody Medicamento medicamento,
            UriComponentsBuilder uriBuilder) {

        Medicamento creado = service.guardar(medicamento);

        URI ubicacion = uriBuilder
                .path("/api/medicamentos/{id}")
                .buildAndExpand(creado.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Medicamento medicamento) {

        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        medicamento.setId(id);
        return ResponseEntity.ok(service.guardar(medicamento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
