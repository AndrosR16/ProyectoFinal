package com.ufide.Farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PerfilForm {

    @NotBlank(message = "{validacion.nombre.obligatorio}")
    @Size(max = 100, message = "{validacion.nombre.tamano}")
    private String nombre;

    @NotBlank(message = "{validacion.correo.obligatorio}")
    @Email(message = "{validacion.correo.invalido}")
    @Size(max = 120, message = "{validacion.correo.tamano}")
    private String correo;

    public PerfilForm() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
