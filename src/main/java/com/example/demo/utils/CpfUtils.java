package com.example.demo.utils;

import java.util.function.Predicate;

import com.example.demo.exceptions.DuplicateResourceException;

public final class CpfUtils {

    public static final String MENSAGEM_CPF_EM_USO = "CPF já está em uso";

    private CpfUtils() {
    }

    public static String normalizarCpf(String cpf) {
        return TextoUtils.normalizarDocumento(cpf);
    }

    @SafeVarargs
    public static boolean verificarCpfEmUso(String cpf, Predicate<String>... verificadores) {
        String cpfLimpo = normalizarCpf(cpf);

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
    public static void validarCpfDisponivel(String cpf, Predicate<String>... verificadores) {
        if (verificarCpfEmUso(cpf, verificadores)) {
            throw new DuplicateResourceException(MENSAGEM_CPF_EM_USO);
        }
    }
}
