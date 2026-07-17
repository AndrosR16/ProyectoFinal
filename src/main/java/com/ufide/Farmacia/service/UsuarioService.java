package com.ufide.Farmacia.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.ufide.Farmacia.dto.RegistroForm;
import com.ufide.Farmacia.entity.Usuario;
import com.ufide.Farmacia.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existeUsername(String username) {
        return repository.existsByUsername(normalizar(username));
    }

    public boolean existeCorreo(String correo) {
        return repository.existsByCorreo(normalizar(correo));
    }

    public void validarRegistro(RegistroForm form, BindingResult result) {
        if (existeUsername(form.getUsername())) {
            result.rejectValue("username", "duplicado", "Ese nombre de usuario ya esta en uso");
        }
        if (existeCorreo(form.getCorreo())) {
            result.rejectValue("correo", "duplicado", "Ese correo ya esta registrado");
        }
        if (!java.util.Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "noCoincide", "Las contrasenas no coinciden");
        }
    }

    public Usuario registrar(RegistroForm form) {
        Usuario usuario = new Usuario();
        usuario.setUsername(normalizar(form.getUsername()));
        usuario.setNombre(form.getNombre());
        usuario.setCorreo(normalizar(form.getCorreo()));
        usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        return repository.save(usuario);
    }

    private String normalizar(String valor) {
        return valor == null ? null : valor.trim().toLowerCase();
    }
}
