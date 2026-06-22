package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.idoso;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.entity.Contato;

class ContatoMapperTest {

    @Test
    void deveConverterContatoParaDTOQuandoContatoForValido() {
        Contato contato = contato(5, "11", "999999999");
        contato.setCuidador(cuidador());
        contato.setIdosos(List.of(idoso()));

        ContatoDTO dto = ContatoMapper.converterContatoParaDTO(contato);

        assertEquals(5, dto.getId());
        assertEquals(2, dto.getCuidadorId());
        assertEquals(List.of(20), dto.getIdosos());
    }

    @Test
    void deveConverterDTOParaContatoQuandoDTOForValido() {
        ContatoDTO dto = new ContatoDTO();
        dto.setDdd("(11)");
        dto.setTelefone("99999-9999");

        Contato contato = ContatoMapper.converterDTOParaContato(dto, cuidador(), List.of(idoso()));

        assertEquals("11", contato.getDdd());
        assertEquals("999999999", contato.getTelefone());
        assertEquals(2, contato.getCuidador().getId());
    }

    @Test
    void deveAtualizarContatoQuandoDTOForValido() {
        Contato contato = contato(5, "11", "999999999");
        ContatoDTO dto = new ContatoDTO();
        dto.setDdd("21");
        dto.setTelefone("98888-7777");

        ContatoMapper.atualizarContatoComDTO(contato, dto, null, List.of());

        assertEquals("21", contato.getDdd());
        assertEquals("988887777", contato.getTelefone());
    }

    @Test
    void deveRetornarNuloQuandoContatoForNulo() {
        assertNull(ContatoMapper.converterContatoParaDTO(null));
    }
}
