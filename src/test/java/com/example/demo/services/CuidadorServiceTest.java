package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.contatoDTO;
import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.instituicao;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dtos.CuidadorDTO;
import com.example.demo.entity.Cuidador;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class CuidadorServiceTest {

    @Mock
    private CuidadorRepository cuidadorRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SenhaService senhaService;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private CuidadorService service;

    @Test
    void deveListarCuidadoresAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Cuidador ativo = criarCuidadorCompleto();

        when(cuidadorRepository.findByStatus(Status.ATIVO, pageable))
                .thenReturn(new PageImpl<>(List.of(ativo)));

        var resultado = service.listarCuidadoresAtivos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Cuidador", resultado.getContent().get(0).getNome());
        assertEquals(Status.ATIVO, resultado.getContent().get(0).getStatus());
        verify(cuidadorRepository).findByStatus(Status.ATIVO, pageable);
    }

    @Test
    void deveListarCuidadoresPorInstituicaoSemCpf() {
        Pageable pageable = PageRequest.of(0, 10);
        Cuidador ativo = criarCuidadorCompleto();

        when(cuidadorRepository.findByStatusAndInstituicaoId(Status.ATIVO, 10, pageable))
                .thenReturn(new PageImpl<>(List.of(ativo)));

        var resultado = service.listarCuidadoresAtivosPorInstituicao(10, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(10, resultado.getContent().get(0).getInstituicaoId());
        verify(cuidadorRepository).findByStatusAndInstituicaoId(Status.ATIVO, 10, pageable);
    }

    @Test
    void deveListarCuidadoresPorInstituicaoFiltradosPorCpf() {
        Pageable pageable = PageRequest.of(0, 10);
        Cuidador ativo = criarCuidadorCompleto();

        when(cuidadorRepository.findByStatusAndInstituicaoIdAndCpf(
                Status.ATIVO, 10, "12345678901", pageable))
                .thenReturn(new PageImpl<>(List.of(ativo)));

        var resultado = service.listarCuidadoresAtivosPorInstituicao(10, "123.456.789-01", pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("12345678901", resultado.getContent().get(0).getCpf());
        verify(cuidadorRepository).findByStatusAndInstituicaoIdAndCpf(
                Status.ATIVO, 10, "12345678901", pageable);
    }

    @Test
    void deveBuscarCuidadorPorIdExistente() {
        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(criarCuidadorCompleto()));

        CuidadorDTO resultado = service.buscarCuidadorPorId(2);

        assertEquals(2, resultado.getId());
        assertEquals("Cuidador", resultado.getNome());
        assertEquals("12345678901", resultado.getCpf());
    }

    @Test
    void deveLancarResourceNotFoundAoBuscarCuidadorInexistente() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarCuidadorPorId(99));
    }

    @Test
    void deveCadastrarCuidadorComSenhaCriptografada() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(false);
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash-gerado");
        when(cuidadorRepository.save(any(Cuidador.class))).thenAnswer(invocation -> {
            Cuidador salvo = invocation.getArgument(0);
            salvo.setId(2);
            return salvo;
        });

        CuidadorDTO resultado = service.cadastrarCuidador(dto);

        assertEquals(2, resultado.getId());
        assertEquals("12345678901", resultado.getCpf());
        assertEquals(10, resultado.getInstituicaoId());
        assertEquals(Status.ATIVO, resultado.getStatus());

        ArgumentCaptor<Cuidador> captor = ArgumentCaptor.forClass(Cuidador.class);
        verify(cuidadorRepository).save(captor.capture());
        assertEquals("hash-gerado", captor.getValue().getSenha());
        assertEquals("11", captor.getValue().getContato().getDdd());
        assertSame(captor.getValue(), captor.getValue().getContato().getCuidador());
        verify(senhaService).validarSenha("Senha@123");
    }

    @Test
    void deveLancarDuplicateResourceAoCadastrarCpfDeCuidadorExistente() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.cadastrarCuidador(dto));
        verifyNoInteractions(instituicaoRepository, passwordEncoder);
    }

    @Test
    void deveLancarDuplicateResourceAoCadastrarCpfDeIdosoExistente() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.cadastrarCuidador(dto));
        verifyNoInteractions(instituicaoRepository, passwordEncoder);
    }

    @Test
    void deveLancarInvalidRequestAoCadastrarCuidadorSemContato() {
        CuidadorDTO dto = criarCuidadorDTO();
        dto.setContato(null);

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> service.cadastrarCuidador(dto));
        verifyNoInteractions(instituicaoRepository, passwordEncoder);
    }

    @Test
    void deveLancarResourceNotFoundAoCadastrarComInstituicaoInexistente() {
        CuidadorDTO dto = criarCuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(false);
        when(instituicaoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.cadastrarCuidador(dto));
        verify(cuidadorRepository, never()).save(any(Cuidador.class));
    }

    @Test
    void deveAtualizarCuidadorComContatoExistente() {
        CuidadorDTO dto = criarCuidadorDTO();
        dto.setNome("Cuidador Atualizado");
        dto.setCpf("987.654.321-00");
        dto.getContato().setTelefone("(11) 98888-7777");
        Cuidador existente = criarCuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.existsByCpf("98765432100")).thenReturn(false);
        when(idosoRepository.existsByCpf("98765432100")).thenReturn(false);
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(passwordEncoder.encode("Senha@123")).thenReturn("nova-hash");
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        CuidadorDTO resultado = service.atualizarCuidador(2, dto);

        assertEquals("Cuidador Atualizado", resultado.getNome());
        assertEquals("98765432100", resultado.getCpf());
        assertEquals("nova-hash", existente.getSenha());
        assertEquals("11988887777", existente.getContato().getTelefone());
        assertNotNull(existente.getData_atualizacao());
        verify(senhaService).validarSenha("Senha@123");
        verify(cuidadorRepository).save(existente);
    }

    @Test
    void deveManterSenhaAtualAoAtualizarSemSenha() {
        CuidadorDTO dto = criarCuidadorDTO();
        dto.setSenha(" ");
        Cuidador existente = criarCuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        service.atualizarCuidador(2, dto);

        assertEquals("hash", existente.getSenha());
        verifyNoInteractions(passwordEncoder, senhaService);
    }

    @Test
    void deveLancarResourceNotFoundAoAtualizarCuidadorInexistente() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizarCuidador(99, criarCuidadorDTO()));
    }

    @Test
    void deveLancarDuplicateResourceAoAtualizarComCpfDeIdoso() {
        CuidadorDTO dto = criarCuidadorDTO();
        dto.setCpf("10987654321");
        Cuidador existente = criarCuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.existsByCpf("10987654321")).thenReturn(false);
        when(idosoRepository.existsByCpf("10987654321")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.atualizarCuidador(2, dto));
    }

    @Test
    void deveLancarResourceNotFoundAoAtualizarComInstituicaoInexistente() {
        CuidadorDTO dto = criarCuidadorDTO();
        Cuidador existente = criarCuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizarCuidador(2, dto));
    }

    @Test
    void deveReativarCuidadorSemDto() {
        Cuidador existente = criarCuidadorCompleto();
        existente.setStatus(Status.INATIVO);

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        CuidadorDTO resultado = service.reativarCuidador(2, null);

        assertEquals(Status.ATIVO, resultado.getStatus());
        assertNotNull(existente.getData_atualizacao());
        verify(cuidadorRepository).save(existente);
    }

    @Test
    void deveReativarCuidadorAtualizandoCamposParciais() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setNome("Cuidador Reativado");
        dto.setCpf("987.654.321-00");
        dto.setEmail("reativado@email.com");
        dto.setSenha("Nova@123");
        dto.setInstituicaoId(10);
        dto.setContato(contatoDTO());
        dto.getContato().setDdd("(21)");
        Cuidador existente = criarCuidadorCompleto();
        existente.setStatus(Status.INATIVO);

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(emailValidationService.validarEmailParaAtualizacao("reativado@email.com", 2))
                .thenReturn("reativado@email.com");
        when(cuidadorRepository.existsByCpf("98765432100")).thenReturn(false);
        when(idosoRepository.existsByCpf("98765432100")).thenReturn(false);
        when(passwordEncoder.encode("Nova@123")).thenReturn("hash-nova");
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        CuidadorDTO resultado = service.reativarCuidador(2, dto);

        assertEquals(Status.ATIVO, resultado.getStatus());
        assertEquals("Cuidador Reativado", resultado.getNome());
        assertEquals("98765432100", resultado.getCpf());
        assertEquals("reativado@email.com", resultado.getEmail());
        assertEquals("hash-nova", existente.getSenha());
        assertEquals("21", existente.getContato().getDdd());
        verify(senhaService).validarSenha("Nova@123");
    }

    @Test
    void deveLancarDuplicateResourceAoReativarComCpfDeIdoso() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setCpf("10987654321");
        Cuidador existente = criarCuidadorCompleto();
        existente.setStatus(Status.INATIVO);

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.existsByCpf("10987654321")).thenReturn(false);
        when(idosoRepository.existsByCpf("10987654321")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.reativarCuidador(2, dto));
    }

    @Test
    void deveLancarResourceNotFoundAoReativarCuidadorInexistente() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.reativarCuidador(99, null));
    }

    @Test
    void deveInativarCuidadorExistente() {
        Cuidador existente = criarCuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));

        service.inativarCuidador(2);

        ArgumentCaptor<Cuidador> captor = ArgumentCaptor.forClass(Cuidador.class);
        verify(cuidadorRepository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getData_atualizacao());
    }

    @Test
    void deveLancarResourceNotFoundAoInativarCuidadorInexistente() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.inativarCuidador(99));
    }

    private CuidadorDTO criarCuidadorDTO() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setNome("Cuidador");
        dto.setCpf("12345678901");
        dto.setEmail("cuidador@email.com");
        dto.setSenha("Senha@123");
        dto.setInstituicaoId(instituicao().getId());
        dto.setContato(contatoDTO());
        return dto;
    }

    private Cuidador criarCuidadorCompleto() {
        Cuidador cuidador = cuidador();
        cuidador.setInstituicao(instituicao());
        var contato = contato(5, "11", "999999999");
        contato.setCuidador(cuidador);
        cuidador.setContato(contato);
        return cuidador;
    }
}
