package com.ufide.Farmacia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/login",
                        "/registro",
                        "/css/**",
                        "/js/**",
                        "/webjars/**")
                .permitAll()

                .requestMatchers("/")
                .hasAnyRole("ADMIN", "USER")

                .requestMatchers(HttpMethod.GET, "/medicamentos")
                .hasAnyRole("ADMIN", "USER")

                .requestMatchers("/carrito/**")
                .hasAnyRole("ADMIN", "USER")

                .requestMatchers("/medicamentos/**")
                .hasRole("ADMIN")

                .requestMatchers(
                        "/clientes/**",
                        "/proveedores/**",
                        "/inventario/**",
                        "/facturas/**",
                        "/reportes/**",
                        "/usuarios/**")
                .hasRole("ADMIN")

                .anyRequest()
                .authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}