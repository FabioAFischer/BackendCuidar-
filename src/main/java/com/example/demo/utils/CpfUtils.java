package com.example.demo.utils;

import java.util.function.Predicate;

import com.example.demo.exceptions.DuplicateResourceException;

public final class CpfUtils {

    public static final String MENSAGEM_CPF_EM_USO = "CPF já está em uso";

    private CpfUtils() {
    }

    public static String normalizar(String cpf) {
        return TextoUtils.limparDocumento(cpf);
    }

    @SafeVarargs
    public static boolean estaEmUso(String cpf, Predicate<String>... verificadores) {
        String cpfLimpo = normalizar(cpf);

        if (cpfLimpo == null) {
            return false;
        }

        for (Predicate<String> verificador : verificadores) {
            if (verificador != null && verificador.test(cpfLimpo)) {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public static void validarDisponivel(String cpf, Predicate<String>... verificadores) {
        if (estaEmUso(cpf, verificadores)) {
            throw new DuplicateResourceException(MENSAGEM_CPF_EM_USO);
        }
    }
}
