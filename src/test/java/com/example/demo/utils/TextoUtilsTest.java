package com.example.demo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextoUtilsTest {

    @Test
    void deveNormalizarTextoParaBanco() {
        assertEquals("JOS\u00c9 DA SILVA", TextoUtils.paraBanco("  Jos\u00e9 da Silva  "));
        assertEquals("REM\u00c9DIO D\u00d3RFLEX", TextoUtils.paraBanco("Rem\u00e9dio D\u00f3rflex"));
    }

    @Test
    void deveFormatarTextoParaExibicao() {
        assertEquals("Jos\u00e9 Da Silva", TextoUtils.paraExibicao("JOS\u00c9 DA SILVA"));
        assertEquals("Rem\u00e9dio D\u00f3rflex", TextoUtils.paraExibicao("REM\u00c9DIO D\u00d3RFLEX"));
        assertEquals("a b c", TextoUtils.paraExibicao("A B C"));
        assertEquals("Tomar Após o Almoço", TextoUtils.paraExibicao("TOMAR APÓS O ALMOÇO"));
    }

    @Test
    void deveLimparDocumentosENumeros() {
        assertEquals("12345678900", TextoUtils.limparDocumento("123.456.789-00"));
        assertEquals("11999998888", TextoUtils.limparNumero("(11) 99999-8888"));
    }
}
