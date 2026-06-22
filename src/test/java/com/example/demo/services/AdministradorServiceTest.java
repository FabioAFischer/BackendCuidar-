package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.administrador;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.AdministradorRepository;

@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private AdministradorRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SenhaService senhaService;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private AdministradorService service;



    @Test
    void deveListarAdministradoresAtivosQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Administrador> pagina = new PageImpl<>(List.of(administrador()), pageable, 1);

        when(repository.findByStatus(Status.ATIVO, pageable)).thenReturn(pagina);

        Page<AdministradorDTO> resultado = service.listarAdministradoresAtivos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Admin", resultado.getContent().get(0).getNome());
        verify(repository).findByStatus(Status.ATIVO, pageable);
    }

    @Test
    void deveCadastrarAdministradorQuandoDadosForemValidos() {
        AdministradorDTO dto = criarAdministradorDTO();
        Administrador salvo = administrador();

        when(emailValidationService.validarEmailParaCriacao("admin@email.com")).thenReturn("admin@email.com");
        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash");
        when(repository.save(any(Administrador.class))).thenReturn(salvo);

        AdministradorDTO resultado = service.cadastrarAdministrador(dto);

        assertEquals(1, resultado.getId());
        assertEquals("Admin", resultado.getNome());
        verify(senhaService).validarSenha("Senha@123");
    }

    @Test
    void deveAtualizarAdministradorQuandoDadosForemValidos() {
        Administrador existente = administrador();
        AdministradorDTO dto = criarAdministradorDTO();
        dto.setNome("Admin Atualizado");

        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(emailValidationService.validarEmailParaAtualizacao("admin@email.com", 1)).thenReturn("admin@email.com");
        when(passwordEncoder.encode("Senha@123")).thenReturn("nova-hash");
        when(repository.save(existente)).thenReturn(existente);

        AdministradorDTO resultado = service.atualizarAdministrador(1, dto);

        assertEquals("Admin Atualizado", resultado.getNome());
        assertEquals("nova-hash", existente.getSenha());
        verify(repository).save(existente);
    }

    @Test
    void deveInativarAdministradorQuandoAdministradorExistir() {
        Administrador existente = administrador();

        when(repository.findById(1)).thenReturn(Optional.of(existente));

        service.inativarAdministrador(1);

        assertEquals(Status.INATIVO, existente.getStatus());
        verify(repository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoCriarComCpfDuplicado() {
        AdministradorDTO dto = criarAdministradorDTO();

        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.cadastrarAdministrador(dto));
    }

    @Test
    void deveLancarExcecaoAoBuscarAdministradorInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarAdministradorPorId(99));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAdministradorInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizarAdministrador(99, criarAdministradorDTO()));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCpfJaEmUso() {
        Administrador existente = administrador();
        existente.setCpf("00000000000");

        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.atualizarAdministrador(1, criarAdministradorDTO()));
    }

    @Test
    void deveLancarExcecaoAoInativarAdministradorInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.inativarAdministrador(99));
    }

    private AdministradorDTO criarAdministradorDTO() {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setNome("Admin");
        dto.setCpf("12345678901");
        dto.setEmail("admin@email.com");
        dto.setSenha("Senha@123");
        return dto;
    }
}
