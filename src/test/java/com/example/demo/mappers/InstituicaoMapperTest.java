package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.instituicaoAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

class InstituicaoMapperTest {

    @Test
    void deveConverterInstituicaoParaDTOQuandoInstituicaoForValida() {
        Instituicao instituicao = instituicaoAuth();
        instituicao.setRua("RUA DAS FLORES");
        instituicao.setBairro("CENTRO");
        instituicao.setUf("sp");

        InstituicaoDTO dto = InstituicaoMapper.converterInstituicaoParaDTO(instituicao);

        assertEquals(3, dto.getId());
        assertEquals("Instituicao", dto.getNome());
        assertEquals("SP", dto.getUf());
    }

    @Test
    void deveConverterDTOParaInstituicaoQuandoDTOForValido() {
        Instituicao instituicao = InstituicaoMapper.converterDTOParaInstituicao(dto());

        assertEquals("INSTITUICAO", instituicao.getNome());
        assertEquals("12345678000199", instituicao.getCnpj());
        assertEquals(Perfil.INSTITUICAO, instituicao.getPerfil());
        assertEquals(Status.ATIVO, instituicao.getStatus());
        assertNotNull(instituicao.getData_criacao());
    }

    @Test
    void deveConverterListaVaziaQuandoListaForNula() {
        assertEquals(0, InstituicaoMapper.converterInstituicoesParaDTOs(null).size());
    }

    @Test
    void deveConverterListaQuandoHouverInstituicoes() {
        assertEquals(1, InstituicaoMapper.converterInstituicoesParaDTOs(List.of(instituicaoAuth())).size());
    }

    @Test
    void deveRetornarNuloQuandoInstituicaoForNula() {
        assertNull(InstituicaoMapper.converterInstituicaoParaDTO(null));
    }

    private InstituicaoDTO dto() {
        InstituicaoDTO dto = new InstituicaoDTO();
        dto.setNome("Instituicao");
        dto.setCnpj("12.345.678/0001-99");
        dto.setEmail("instituicao@email.com");
        dto.setSenha("Senha@123");
        dto.setRua("Rua");
        dto.setBairro("Centro");
        dto.setUf("sp");
        dto.setNumero(100);
        dto.setCep("01310-100");
        return dto;
    }
}
