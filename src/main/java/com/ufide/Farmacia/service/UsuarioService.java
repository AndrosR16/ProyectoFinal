package com.ufide.Farmacia.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.ufide.Farmacia.dto.CambioPasswordForm;
import com.ufide.Farmacia.dto.PerfilForm;
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

    public Usuario buscarPorUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username
                ));
    }

    public boolean existeUsername(String username) {
        return repository.existsByUsername(normalizar(username));
    }

    public boolean existeCorreo(String correo) {
        return repository.existsByCorreo(normalizar(correo));
    }

    public void validarRegistro(RegistroForm form, BindingResult result) {
        if (existeUsername(form.getUsername())) {
            result.rejectValue(
                    "username",
                    "validacion.registro.username.duplicado",
                    "Ese nombre de usuario ya está en uso"
            );
        }

        if (existeCorreo(form.getCorreo())) {
            result.rejectValue(
                    "correo",
                    "validacion.registro.correo.duplicado",
                    "Ese correo ya está registrado"
            );
        }

        if (!java.util.Objects.equals(
                form.getPassword(),
                form.getConfirmPassword())) {

            result.rejectValue(
                    "confirmPassword",
                    "validacion.registro.confirmar.password.no.coincide",
                    "Las contraseñas no coinciden"
            );
        }
    }

    public Usuario registrar(RegistroForm form) {
        Usuario usuario = new Usuario();

        usuario.setUsername(normalizar(form.getUsername()));
        usuario.setNombre(form.getNombre());
        usuario.setCorreo(normalizar(form.getCorreo()));
        usuario.setPassword(passwordEncoder.encode(form.getPassword()));
        usuario.setRol("USER");

        return repository.save(usuario);
    }

    public void actualizarPerfil(String username, PerfilForm form, BindingResult result) {
        Usuario usuario = buscarPorUsername(username);

        if (!result.hasFieldErrors("correo")) {
            String correoNuevo = normalizar(form.getCorreo());

            if (!correoNuevo.equals(usuario.getCorreo()) && existeCorreo(correoNuevo)) {
                // validacion.registro.correo.duplicado: reusada tal cual desde
                // el registro, mismo texto exacto ("Ese correo ya está registrado").
                result.rejectValue(
                        "correo",
                        "validacion.registro.correo.duplicado",
                        "Ese correo ya está registrado"
                );
            }
        }

        if (result.hasErrors()) {
            return;
        }

        usuario.setNombre(form.getNombre());
        usuario.setCorreo(normalizar(form.getCorreo()));

        repository.save(usuario);
    }

    public void cambiarPassword(String username, CambioPasswordForm form, BindingResult result) {
        Usuario usuario = buscarPorUsername(username);

        if (!result.hasFieldErrors("passwordActual")
                && !passwordEncoder.matches(form.getPasswordActual(), usuario.getPassword())) {

            result.rejectValue(
                    "passwordActual",
                    "validacion.perfil.password.actual.incorrecta",
                    "La contraseña actual no es correcta"
            );
        }

        if (!result.hasFieldErrors("passwordNueva")
                && !result.hasFieldErrors("confirmarPassword")
                && !java.util.Objects.equals(form.getPasswordNueva(), form.getConfirmarPassword())) {

            // validacion.registro.confirmar.password.no.coincide: reusada tal
            // cual desde el registro, mismo texto exacto.
            result.rejectValue(
                    "confirmarPassword",
                    "validacion.registro.confirmar.password.no.coincide",
                    "Las contraseñas no coinciden"
            );
        }

        if (result.hasErrors()) {
            return;
        }

        usuario.setPassword(passwordEncoder.encode(form.getPasswordNueva()));
        repository.save(usuario);
    }

    private String normalizar(String valor) {
        return valor == null
                ? null
                : valor.trim().toLowerCase();
    }
}