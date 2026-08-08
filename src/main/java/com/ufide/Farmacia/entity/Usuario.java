package com.ufide.Farmacia.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validacion.usuario.username.obligatorio}")
    @Size(max = 50, message = "{validacion.usuario.username.tamano}")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "{validacion.nombre.obligatorio}")
    @Size(max = 100, message = "{validacion.nombre.tamano}")
    private String nombre;

    @NotBlank(message = "{validacion.correo.obligatorio}")
    @Email(message = "{validacion.correo.invalido}")
    @Size(max = 120, message = "{validacion.correo.tamano}")
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "{validacion.password.obligatoria}")
    private String password;

    @NotBlank(message = "{validacion.usuario.rol.obligatorio}")
    @Size(max = 20, message = "{validacion.usuario.rol.tamano}")
    @Column(nullable = false)
    private String rol = "USER";

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}