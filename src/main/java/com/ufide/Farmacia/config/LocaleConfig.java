package com.ufide.Farmacia.config;

import java.time.Duration;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Configuración de internacionalización (español/inglés).
 *
 * El idioma se resuelve por cookie (no por sesión) para que la
 * preferencia sobreviva reinicios del navegador, con español como
 * valor por defecto cuando todavía no existe cookie. El cambio de
 * idioma se dispara agregando el parámetro "lang" a cualquier URL
 * (por ejemplo /medicamentos?lang=en).
 *
 * El bean se llama "localeResolver" a propósito: es el nombre que
 * Spring Boot busca (DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME) para
 * omitir el AcceptHeaderLocaleResolver que registra por defecto.
 *
 * El Validator expuesto en getValidator() resuelve los message="{...}"
 * de las entidades y de RegistroForm contra messages.properties /
 * messages_es.properties según el locale ya resuelto por
 * localeResolver, en lugar de usar los mensajes por defecto de
 * Hibernate Validator.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    private final MessageSource messageSource;

    public LocaleConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("idioma");
        resolver.setDefaultLocale(Locale.of("es"));
        resolver.setCookieMaxAge(Duration.ofDays(30));
        resolver.setCookiePath("/");
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }

    @Override
    public Validator getValidator() {
        return validator();
    }
}
