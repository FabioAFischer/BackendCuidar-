package com.example.demo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TextoUtilsTest {

    @Test
    void deveNormalizarTextoParaBanco() {
        assertEquals("JOSE DA SILVA", TextoUtils.paraBanco("  José da Silva  "));
        assertEquals("REMEDIO DORFLEX", TextoUtils.paraBanco("Remédio Dórflex"));
    }

    @Test
    void deveFormatarTextoParaExibicao() {
        assertEquals("Jose Da Silva", TextoUtils.paraExibicao("JOSE DA SILVA"));
        assertEquals("Remedio Dorflex", TextoUtils.paraExibicao("REMEDIO DORFLEX"));
    }

    @Test
    void deveLimparDocumentosENumeros() {
        assertEquals("12345678900", TextoUtils.limparDocumento("123.456.789-00"));
        assertEquals("11999998888", TextoUtils.limparNumero("(11) 99999-8888"));
    }
}
