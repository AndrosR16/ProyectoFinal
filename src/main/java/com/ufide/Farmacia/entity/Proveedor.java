package com.ufide.Farmacia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validacion.nombre.obligatorio}")
    @Size(max = 100, message = "{validacion.nombre.tamano}")
    private String nombre;

    @NotBlank(message = "{validacion.telefono.obligatorio}")
    @Pattern(regexp = "^[0-9]{8}$", message = "{validacion.proveedor.telefono.formato}")
    private String telefono;
    @NotBlank(message = "{validacion.correo.obligatorio}")
    @Email(message = "{validacion.correo.invalido}")
    @Size(max = 120, message = "{validacion.correo.tamano}")
    private String correo;

    public Proveedor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}