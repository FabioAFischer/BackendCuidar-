package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.instituicao;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.CuidadorDTO;
import com.example.demo.entity.Cuidador;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

class CuidadorMapperTest {

    @Test
    void deveConverterCuidadorParaDTOQuandoCuidadorForValido() {
        Cuidador cuidador = cuidador();
        cuidador.setInstituicao(instituicao());
        cuidador.setContato(contato(5, "11", "999999999"));

        CuidadorDTO dto = CuidadorMapper.converterCuidadorParaDTO(cuidador);

        assertEquals(2, dto.getId());
        assertEquals(10, dto.getInstituicaoId());
        assertEquals(5, dto.getContato().getId());
    }

    @Test
    void deveConverterDTOParaCuidadorQuandoDTOForValido() {
        Cuidador cuidador = CuidadorMapper.converterDTOParaCuidador(dto());

        assertEquals("CUIDADOR", cuidador.getNome());
        assertEquals("12345678901", cuidador.getCpf());
        assertEquals(Perfil.CUIDADOR, cuidador.getPerfil());
        assertEquals(Status.ATIVO, cuidador.getStatus());
        assertNotNull(cuidador.getContato());
    }

    @Test
    void deveConverterListaVaziaQuandoListaForNula() {
        assertEquals(0, CuidadorMapper.converterCuidadoresParaDTOs(null).size());
    }

    @Test
    void deveConverterListaQuandoHouverCuidadores() {
        assertEquals(1, CuidadorMapper.converterCuidadoresParaDTOs(List.of(cuidador())).size());
    }

    @Test
    void deveRetornarNuloQuandoCuidadorForNulo() {
        assertNull(CuidadorMapper.converterCuidadorParaDTO(null));
    }

    private CuidadorDTO dto() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setNome("Cuidador");
        dto.setCpf("123.456.789-01");
        dto.setEmail("cuidador@email.com");
        dto.setSenha("Senha@123");
        dto.setInstituicaoId(10);
        ContatoDTO contato = new ContatoDTO();
        contato.setDdd("(11)");
        contato.setTelefone("99999-9999");
        dto.setContato(contato);
        return dto;
    }
}
