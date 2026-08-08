package com.ufide.Farmacia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambioPasswordForm {

    @NotBlank(message = "{validacion.password.obligatoria}")
    private String passwordActual;

    @NotBlank(message = "{validacion.password.obligatoria}")
    @Size(min = 6, max = 64, message = "{validacion.registro.password.tamano}")
    private String passwordNueva;

    @NotBlank(message = "{validacion.registro.confirmar.password.obligatorio}")
    private String confirmarPassword;

    public CambioPasswordForm() {
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}
