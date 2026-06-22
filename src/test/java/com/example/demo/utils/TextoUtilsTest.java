package com.example.demo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TextoUtilsTest {

    @Test
    void deveNormalizarTextoParaBanco() {
        assertEquals("JOS\u00c9 DA SILVA", TextoUtils.normalizarTextoParaBanco("  Jos\u00e9 da Silva  "));
        assertEquals("REM\u00c9DIO D\u00d3RFLEX", TextoUtils.normalizarTextoParaBanco("Rem\u00e9dio D\u00f3rflex"));
    }

    @Test
    void deveFormatarTextoParaExibicao() {
        assertEquals("Jos\u00e9 Da Silva", TextoUtils.formatarTextoParaExibicao("JOS\u00c9 DA SILVA"));
        assertEquals("Rem\u00e9dio D\u00f3rflex", TextoUtils.formatarTextoParaExibicao("REM\u00c9DIO D\u00d3RFLEX"));
        assertEquals("A B C", TextoUtils.formatarTextoParaExibicao("A B C"));
        assertEquals("Tomar Após O Almoço", TextoUtils.formatarTextoParaExibicao("TOMAR APÓS O ALMOÇO"));
    }

    @Test
    void deveManterTextoLivreSemAlteracao() {
        assertEquals("  Observação livre COM acento!  ", TextoUtils.manterTextoLivre("  Observação livre COM acento!  "));
        assertNull(TextoUtils.manterTextoLivre(null));
    }

    @Test
    void deveLimparDocumentosENumeros() {
        assertEquals("12345678900", TextoUtils.normalizarDocumento("123.456.789-00"));
        assertEquals("11999998888", TextoUtils.normalizarNumero("(11) 99999-8888"));
    }

    @Test
    void deveNormalizarUfQuandoValorForInformado() {
        assertEquals("SP", TextoUtils.normalizarUf(" sp "));
    }

    @Test
    void deveRetornarNuloQuandoNormalizarUfNula() {
        assertNull(TextoUtils.normalizarUf(null));
    }

}
