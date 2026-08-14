package com.ufide.Farmacia.entity;

import com.ufide.Farmacia.util.Telefonos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validacion.nombre.obligatorio}")
    @Size(max = 100, message = "{validacion.nombre.tamano}")
    private String nombre;

    @NotBlank(message = "{validacion.cliente.identificacion.obligatoria}")
    @Size(max = 20, message = "{validacion.cliente.identificacion.tamano}")
    private String identificacion;

    @NotBlank(message = "{validacion.telefono.obligatorio}")
    @Pattern(regexp = "^[0-9]{8}$", message = "{validacion.cliente.telefono.formato}")
    private String telefono;

    @NotBlank(message = "{validacion.correo.obligatorio}")
    @Email(message = "{validacion.correo.invalido}")
    @Size(max = 120, message = "{validacion.correo.tamano}")
    private String correo;

    public Cliente() {
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

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion == null
                ? null
                : identificacion.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = Telefonos.normalizar(telefono);
    }

    @Transient
    public String getTelefonoFormateado() {
        return Telefonos.formatear(telefono);
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo == null
                ? null
                : correo.trim().toLowerCase();
    }
}