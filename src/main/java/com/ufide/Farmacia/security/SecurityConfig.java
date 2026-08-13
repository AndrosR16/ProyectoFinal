package com.ufide.Farmacia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        BasicAuthenticationEntryPoint apiEntryPoint =
                new BasicAuthenticationEntryPoint();

        apiEntryPoint.setRealmName("Farmacia API");

        AccessDeniedHandlerImpl webDeniedHandler =
                new AccessDeniedHandlerImpl();

        webDeniedHandler.setErrorPage("/acceso-denegado");

        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/",
                        "/shop",
                        "/login",
                        "/registro",
                        "/acceso-denegado",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**",
                        "/favicon.svg")
                .permitAll()

                .requestMatchers("/api/clientes/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/**")
                .hasAnyRole(
                        "ADMIN",
                        "EMPLEADO",
                        "CLIENTE"
                )

                .requestMatchers("/api/**")
                .hasRole("ADMIN")

                .requestMatchers("/dashboard")
                .hasAnyRole(
                        "ADMIN",
                        "EMPLEADO"
                )

                .requestMatchers("/carrito/**")
                .hasAnyRole(
                        "ADMIN",
                        "EMPLEADO",
                        "CLIENTE"
                )

                .requestMatchers("/medicamentos/**")
                .hasRole("ADMIN")

                .requestMatchers("/facturas/**")
                .hasAnyRole(
                        "ADMIN",
                        "EMPLEADO",
                        "CLIENTE"
                )

                .requestMatchers(
                        "/clientes/**",
                        "/proveedores/**",
                        "/inventario/**")
                .hasAnyRole(
                        "ADMIN",
                        "EMPLEADO"
                )

                .requestMatchers(
                        "/reportes/**",
                        "/usuarios/**")
                .hasRole("ADMIN")

                .anyRequest()
                .authenticated())

                .httpBasic(Customizer.withDefaults())

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/shop", false)
                        .failureUrl("/login?error")
                        .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/shop")
                        .permitAll())

                .exceptionHandling(exception -> exception

                        .defaultAuthenticationEntryPointFor(
                                apiEntryPoint,
                                PathPatternRequestMatcher
                                        .pathPattern("/api/**"))

                        .defaultAccessDeniedHandlerFor(
                                new AccessDeniedHandlerImpl(),
                                PathPatternRequestMatcher
                                        .pathPattern("/api/**"))

                        .defaultAccessDeniedHandlerFor(
                                webDeniedHandler,
                                AnyRequestMatcher.INSTANCE));

        http.addFilterAfter(
                (request, response, chain) -> {

                    CsrfToken csrfToken =
                            (CsrfToken) request.getAttribute(
                                    CsrfToken.class.getName()
                            );

                    if (csrfToken != null) {
                        csrfToken.getToken();
                    }

                    chain.doFilter(request, response);

                },
                CsrfFilter.class
        );

        return http.build();
    }
}