package com.example.demo.utils;

import java.util.Locale;

public final class TextoUtils {

    private TextoUtils() {
    }

    public static String normalizarTextoParaBanco(String valor) {
        if (valor == null) {
            return null;
        }

        return valor.trim().toUpperCase(Locale.ROOT);
    }

    public static String formatarTextoParaExibicao(String valor) {
        if (valor == null) {
            return null;
        }

        String texto = valor.trim().toLowerCase(Locale.ROOT);
        if (texto.isEmpty()) {
            return texto;
        }

        String[] palavras = texto.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palavra : palavras) {
            if (resultado.length() > 0) {
                resultado.append(" ");
            }

            if (palavra.length() == 1) {
                resultado.append(Character.toTitleCase(palavra.charAt(0)));
            } else {
                resultado.append(Character.toTitleCase(palavra.charAt(0)));
                resultado.append(palavra.substring(1));
            }
        }

        return resultado.toString();
    }

    public static String manterTextoLivre(String valor) {
        return valor;
    }

    public static String normalizarDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }

    public static String normalizarNumero(String valor) {
        return normalizarDocumento(valor);
    }

    public static String normalizarUf(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim().toUpperCase(Locale.ROOT);
    }
}
