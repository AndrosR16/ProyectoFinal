package com.ufide.Farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FacturaForm {

    @NotBlank(message = "{validacion.factura.nombre.obligatorio}")
    private String compradorNombre;

    @NotBlank(message = "{validacion.factura.identificacion.obligatorio}")
    private String compradorIdentificacion;

    @Email(message = "{validacion.correo.invalido}")
    private String compradorCorreo;

    /* Opcional, pero si viene debe ser un número tico de 8 dígitos. El guion
       es tolerado porque la máscara del navegador lo inserta; Venta lo
       normaliza al persistir. Sin esto, un POST sin JS guarda cualquier cosa. */
    @Pattern(regexp = "^$|^[0-9]{4}-?[0-9]{4}$",
            message = "{validacion.cliente.telefono.formato}")
    private String compradorTelefono;

    @NotBlank(message = "{validacion.factura.metodopago.obligatorio}")
    private String metodoPago = "EFECTIVO";

    public FacturaForm() {
    }

    public String getCompradorNombre() {
        return compradorNombre;
    }

    public void setCompradorNombre(String compradorNombre) {
        this.compradorNombre = compradorNombre;
    }

    public String getCompradorIdentificacion() {
        return compradorIdentificacion;
    }

    public void setCompradorIdentificacion(String compradorIdentificacion) {
        this.compradorIdentificacion = compradorIdentificacion;
    }

    public String getCompradorCorreo() {
        return compradorCorreo;
    }

    public void setCompradorCorreo(String compradorCorreo) {
        this.compradorCorreo = compradorCorreo;
    }

    public String getCompradorTelefono() {
        return compradorTelefono;
    }

    public void setCompradorTelefono(String compradorTelefono) {
        this.compradorTelefono = compradorTelefono;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
