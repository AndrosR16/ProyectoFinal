package com.ufide.Farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistroForm {

    @NotBlank(message = "{validacion.usuario.username.obligatorio}")
    @Size(max = 50, message = "{validacion.usuario.username.tamano}")
    private String username;

    @NotBlank(message = "{validacion.nombre.obligatorio}")
    @Size(max = 100, message = "{validacion.nombre.tamano}")
    private String nombre;

    @NotBlank(message = "{validacion.correo.obligatorio}")
    @Email(message = "{validacion.correo.invalido}")
    @Size(max = 120, message = "{validacion.correo.tamano}")
    private String correo;

    @NotBlank(message = "{validacion.password.obligatoria}")
    @Size(min = 6, max = 64, message = "{validacion.registro.password.tamano}")
    private String password;

    @NotBlank(message = "{validacion.registro.confirmar.password.obligatorio}")
    private String confirmPassword;

    public RegistroForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
