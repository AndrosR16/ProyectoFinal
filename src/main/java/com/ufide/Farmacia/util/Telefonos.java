package com.ufide.Farmacia.util;

/**
 * Utilidades para normalizar y formatear números de teléfono de Costa Rica.
 * En BD se almacenan solo los 8 dígitos, sin separadores.
 */
public final class Telefonos {

    private Telefonos() {
    }

    /**
     * Deja únicamente los dígitos del valor recibido. Si tras limpiar quedan
     * 11 dígitos con el prefijo de país "506", lo retira para conservar solo
     * los 8 dígitos locales.
     */
    public static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String soloDigitos = valor.replaceAll("[^0-9]", "");
        if (soloDigitos.length() == 11 && soloDigitos.startsWith("506")) {
            soloDigitos = soloDigitos.substring(3);
        }
        return soloDigitos;
    }

    /**
     * Formatea un número ya normalizado como "XXXX-XXXX". Si no tiene
     * exactamente 8 dígitos se devuelve el valor normalizado tal cual.
     */
    public static String formatear(String valor) {
        String normalizado = normalizar(valor);
        if (normalizado == null || normalizado.isEmpty()) {
            return "";
        }
        if (normalizado.length() == 8) {
            return normalizado.substring(0, 4) + "-" + normalizado.substring(4);
        }
        return normalizado;
    }
}
