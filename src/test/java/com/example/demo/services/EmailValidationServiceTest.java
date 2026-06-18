package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Administrador;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Instituicao;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class EmailValidationServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @InjectMocks
    private EmailValidationService service;

    @Test
    void ValidarParaCriacao_emailValido_normalizaEmail() {
        when(cuidadorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());
        when(instituicaoRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());

        String resultado = service.validarParaCriacao(" Usuario@Email.COM ");

        assertEquals("usuario@email.com", resultado);
    }

    @Test
    void ValidarParaCriacao_emailInvalido_lancaInvalidRequest() {
        assertThrows(InvalidRequestException.class, () -> service.validarParaCriacao("email-invalido"));
    }

    @Test
    void ValidarParaCriacao_emailDeCuidadorExistente_lancaDuplicateResource() {
        Cuidador cuidador = new Cuidador();
        cuidador.setId(2);

        when(cuidadorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(cuidador));

        assertThrows(DuplicateResourceException.class, () -> service.validarParaCriacao("usuario@email.com"));
    }

    @Test
    void ValidarParaCriacao_emailDeInstituicaoExistente_lancaDuplicateResource() {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(3);

        when(cuidadorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());
        when(instituicaoRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(instituicao));

        assertThrows(DuplicateResourceException.class, () -> service.validarParaCriacao("usuario@email.com"));
    }

    @Test
    void ValidarParaCriacao_emailDeAdministradorExistente_lancaDuplicateResource() {
        Administrador administrador = new Administrador();
        administrador.setId(1);

        when(cuidadorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());
        when(instituicaoRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(administrador));

        assertThrows(DuplicateResourceException.class, () -> service.validarParaCriacao("usuario@email.com"));
    }

    @Test
    void ValidarParaAtualizacao_emailDoMesmoUsuario_permaneceDisponivel() {
        Cuidador cuidador = new Cuidador();
        cuidador.setId(2);

        when(cuidadorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(cuidador));
        when(instituicaoRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByEmail("usuario@email.com")).thenReturn(Optional.empty());

        String resultado = service.validarParaAtualizacao("usuario@email.com", 2);

        assertEquals("usuario@email.com", resultado);
    }
}
