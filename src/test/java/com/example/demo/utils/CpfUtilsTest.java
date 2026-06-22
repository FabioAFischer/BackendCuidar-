package com.example.demo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.exceptions.DuplicateResourceException;

class CpfUtilsTest {

    @Test
    void deveNormalizarCpfQuandoCpfTiverMascara() {
        assertEquals("12345678901", CpfUtils.normalizarCpf("123.456.789-01"));
    }

    @Test
    void deveRetornarFalsoQuandoCpfForNulo() {
        assertFalse(CpfUtils.verificarCpfEmUso(null, cpf -> true));
    }

    @Test
    void deveRetornarVerdadeiroQuandoAlgumVerificadorEncontrarCpf() {
        assertTrue(CpfUtils.verificarCpfEmUso("123.456.789-01", cpf -> false, cpf -> cpf.equals("12345678901")));
    }

    @Test
    void deveIgnorarVerificadorNuloQuandoVerificarCpfEmUso() {
        assertFalse(CpfUtils.verificarCpfEmUso("123.456.789-01", null, cpf -> false));
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoEstiverDisponivel() {
        assertThrows(DuplicateResourceException.class,
                () -> CpfUtils.validarCpfDisponivel("123.456.789-01", cpf -> true));
    }
}
