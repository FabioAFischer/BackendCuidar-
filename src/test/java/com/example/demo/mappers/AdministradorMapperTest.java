package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.administrador;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

class AdministradorMapperTest {

    @Test
    void deveConverterAdministradorParaDTOQuandoAdministradorForValido() {
        AdministradorDTO dto = AdministradorMapper.converterAdministradorParaDTO(administrador());

        assertEquals(1, dto.getId());
        assertEquals("Admin", dto.getNome());
        assertEquals("12345678901", dto.getCpf());
    }

    @Test
    void deveConverterDTOParaAdministradorQuandoDTOForValido() {
        AdministradorDTO dto = dto();

        Administrador administrador = AdministradorMapper.converterDTOParaAdministrador(dto);

        assertEquals("ADMIN", administrador.getNome());
        assertEquals(Perfil.ADMINISTRADOR, administrador.getPerfil());
        assertEquals(Status.ATIVO, administrador.getStatus());
        assertNotNull(administrador.getData_criacao());
    }

    @Test
    void deveAtualizarAdministradorQuandoDTOForValido() {
        Administrador administrador = administrador();
        AdministradorDTO dto = dto();
        dto.setNome("Outro Admin");

        AdministradorMapper.atualizarAdministradorComDTO(administrador, dto);

        assertEquals("OUTRO ADMIN", administrador.getNome());
        assertNotNull(administrador.getData_atualizacao());
    }

    @Test
    void deveConverterListaVaziaQuandoListaForNula() {
        assertEquals(0, AdministradorMapper.converterAdministradoresParaDTOs(null).size());
    }

    @Test
    void deveConverterListaQuandoHouverAdministradores() {
        assertEquals(1, AdministradorMapper.converterAdministradoresParaDTOs(List.of(administrador())).size());
    }

    @Test
    void deveRetornarNuloQuandoAdministradorForNulo() {
        assertNull(AdministradorMapper.converterAdministradorParaDTO(null));
    }

    private AdministradorDTO dto() {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setNome("Admin");
        dto.setCpf("123.456.789-01");
        dto.setEmail("admin@email.com");
        dto.setSenha("Senha@123");
        return dto;
    }
}
