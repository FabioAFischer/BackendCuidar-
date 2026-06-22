package com.example.demo.utils;

import java.util.Locale;
import java.util.Set;

public final class TextoUtils {

    private static final Set<String> CONECTIVOS_MINUSCULOS = Set.of(
            "a", "as", "o", "os",
            "ao", "aos", "à", "às",
            "com", "contra",
            "da", "das", "de", "desde", "do", "dos",
            "e", "em", "entre",
            "na", "nas", "no", "nos",
            "num", "numa", "nuns", "numas",
            "para", "pela", "pelas", "pelo", "pelos", "por",
            "sem", "sob", "sobre"
    );

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

        for (int i = 0; i < palavras.length; i++) {
            String palavra = palavras[i];

            if (resultado.length() > 0) {
                resultado.append(" ");
            }

            if (i > 0 && CONECTIVOS_MINUSCULOS.contains(palavra)) {
                resultado.append(palavra);
            } else if (palavra.length() == 1) {
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
