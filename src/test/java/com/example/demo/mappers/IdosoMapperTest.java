package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.idosoDTO;
import static com.example.demo.support.TestDataFactory.instituicao;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.IdosoDTO;
import com.example.demo.entity.Idoso;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

class IdosoMapperTest {

    @Test
    void deveConverterIdosoParaDTOQuandoIdosoForValido() {
        Idoso idoso = idoso(20, "MARIA", "12345678901", Status.ATIVO);
        idoso.setSenhaAcessoCriptografada("hash");
        idoso.setObservacoes("ALERGIA forte a dipirona");

        IdosoDTO dto = IdosoMapper.converterIdosoParaDTO(idoso);

        assertEquals(20, dto.getId());
        assertEquals("Maria", dto.getNome());
        assertEquals("ALERGIA forte a dipirona", dto.getObservacoes());
        assertTrue(dto.getSenhaAcessoGerada());
        assertEquals(10, dto.getInstituicaoId());
    }

    @Test
    void deveConverterDTOParaIdosoQuandoDTOForValido() {
        IdosoDTO dto = idosoDTO();
        dto.setContatoId(5);

        Idoso idoso = IdosoMapper.converterDTOParaIdoso(dto);

        assertEquals("MARIA", idoso.getNome());
        assertEquals("12345678901", idoso.getCpf());
        assertEquals(dto.getObservacoes(), idoso.getObservacoes());
        assertEquals(Perfil.IDOSO, idoso.getPerfil());
        assertEquals(Status.ATIVO, idoso.getStatus());
        assertNotNull(idoso.getData_criacao());
    }

    @Test
    void deveAtualizarIdosoQuandoDTOForValido() {
        Idoso idoso = idoso(20, "Maria", "12345678901", Status.ATIVO);
        IdosoDTO dto = idosoDTO();
        dto.setNome("Maria Atualizada");
        dto.setContatoId(5);

        IdosoMapper.atualizarIdosoComDTO(idoso, dto, instituicao());

        assertEquals("MARIA ATUALIZADA", idoso.getNome());
        assertEquals(dto.getObservacoes(), idoso.getObservacoes());
        assertEquals(10, idoso.getInstituicao().getId());
        assertEquals(5, idoso.getContato().getId());
    }

    @Test
    void deveRetornarNuloQuandoIdosoForNulo() {
        assertNull(IdosoMapper.converterIdosoParaDTO(null));
    }
}
