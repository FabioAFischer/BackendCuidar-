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
    void Listar_ativos_retornaPaginaDeCuidadoresAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Cuidador ativo = cuidadorCompleto();

        when(cuidadorRepository.findByStatus(Status.ATIVO, pageable))
                .thenReturn(new PageImpl<>(List.of(ativo)));

        var resultado = service.listarAtivos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Cuidador", resultado.getContent().get(0).getNome());
        assertEquals(Status.ATIVO, resultado.getContent().get(0).getStatus());
        verify(cuidadorRepository).findByStatus(Status.ATIVO, pageable);
    }

    @Test
    void Listar_porInstituicaoSemCpf_retornaPaginaDaInstituicao() {
        Pageable pageable = PageRequest.of(0, 10);
        Cuidador ativo = cuidadorCompleto();

        when(cuidadorRepository.findByStatusAndInstituicaoId(Status.ATIVO, 10, pageable))
                .thenReturn(new PageImpl<>(List.of(ativo)));

        var resultado = service.listarAtivosPorInstituicao(10, null, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(10, resultado.getContent().get(0).getInstituicaoId());
        verify(cuidadorRepository).findByStatusAndInstituicaoId(Status.ATIVO, 10, pageable);
    }

    @Test
    void Listar_porInstituicaoComCpf_retornaPaginaFiltradaPorCpfLimpo() {
        Pageable pageable = PageRequest.of(0, 10);
        Cuidador ativo = cuidadorCompleto();

        when(cuidadorRepository.findByStatusAndInstituicaoIdAndCpf(
                Status.ATIVO, 10, "12345678901", pageable))
                .thenReturn(new PageImpl<>(List.of(ativo)));

        var resultado = service.listarAtivosPorInstituicao(10, "123.456.789-01", pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("12345678901", resultado.getContent().get(0).getCpf());
        verify(cuidadorRepository).findByStatusAndInstituicaoIdAndCpf(
                Status.ATIVO, 10, "12345678901", pageable);
    }

    @Test
    void Buscar_porIdExistente_retornaCuidador() {
        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(cuidadorCompleto()));

        CuidadorDTO resultado = service.buscarPorId(2);

        assertEquals(2, resultado.getId());
        assertEquals("Cuidador", resultado.getNome());
        assertEquals("12345678901", resultado.getCpf());
    }

    @Test
    void Buscar_porIdInexistente_lancaResourceNotFound() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99));
    }

    @Test
    void Criar_dadosValidos_salvaCuidadorComSenhaCriptografada() {
        CuidadorDTO dto = cuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(false);
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash-gerado");
        when(cuidadorRepository.save(any(Cuidador.class))).thenAnswer(invocation -> {
            Cuidador salvo = invocation.getArgument(0);
            salvo.setId(2);
            return salvo;
        });

        CuidadorDTO resultado = service.criar(dto);

        assertEquals(2, resultado.getId());
        assertEquals("12345678901", resultado.getCpf());
        assertEquals(10, resultado.getInstituicaoId());
        assertEquals(Status.ATIVO, resultado.getStatus());

        ArgumentCaptor<Cuidador> captor = ArgumentCaptor.forClass(Cuidador.class);
        verify(cuidadorRepository).save(captor.capture());
        assertEquals("hash-gerado", captor.getValue().getSenha());
        assertEquals("11", captor.getValue().getContato().getDdd());
        assertSame(captor.getValue(), captor.getValue().getContato().getCuidador());
        verify(senhaService).validar("Senha@123");
    }

    @Test
    void Criar_cpfDeCuidador_lancaDuplicateResource() {
        CuidadorDTO dto = cuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.criar(dto));
        verifyNoInteractions(instituicaoRepository, passwordEncoder);
    }

    @Test
    void Criar_cpfDeIdoso_lancaDuplicateResource() {
        CuidadorDTO dto = cuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.criar(dto));
        verifyNoInteractions(instituicaoRepository, passwordEncoder);
    }

    @Test
    void Criar_semContato_lancaInvalidRequest() {
        CuidadorDTO dto = cuidadorDTO();
        dto.setContato(null);

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(false);

        assertThrows(InvalidRequestException.class, () -> service.criar(dto));
        verifyNoInteractions(instituicaoRepository, passwordEncoder);
    }

    @Test
    void Criar_instituicaoInexistente_lancaResourceNotFound() {
        CuidadorDTO dto = cuidadorDTO();

        when(cuidadorRepository.existsByCpf("12345678901")).thenReturn(false);
        when(idosoRepository.existsByCpf("12345678901")).thenReturn(false);
        when(instituicaoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criar(dto));
        verify(cuidadorRepository, never()).save(any(Cuidador.class));
    }

    @Test
    void Atualizar_dadosValidos_atualizaCuidadorComContatoExistente() {
        CuidadorDTO dto = cuidadorDTO();
        dto.setNome("Cuidador Atualizado");
        dto.setCpf("987.654.321-00");
        dto.getContato().setTelefone("(11) 98888-7777");
        Cuidador existente = cuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.existsByCpf("98765432100")).thenReturn(false);
        when(idosoRepository.existsByCpf("98765432100")).thenReturn(false);
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(passwordEncoder.encode("Senha@123")).thenReturn("nova-hash");
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        CuidadorDTO resultado = service.atualizar(2, dto);

        assertEquals("Cuidador Atualizado", resultado.getNome());
        assertEquals("98765432100", resultado.getCpf());
        assertEquals("nova-hash", existente.getSenha());
        assertEquals("11988887777", existente.getContato().getTelefone());
        assertNotNull(existente.getData_atualizacao());
        verify(senhaService).validar("Senha@123");
        verify(cuidadorRepository).save(existente);
    }

    @Test
    void Atualizar_semSenha_mantemSenhaAtual() {
        CuidadorDTO dto = cuidadorDTO();
        dto.setSenha(" ");
        Cuidador existente = cuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        service.atualizar(2, dto);

        assertEquals("hash", existente.getSenha());
        verifyNoInteractions(passwordEncoder, senhaService);
    }

    @Test
    void Atualizar_cuidadorInexistente_lancaResourceNotFound() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizar(99, cuidadorDTO()));
    }

    @Test
    void Atualizar_cpfDeIdoso_lancaDuplicateResource() {
        CuidadorDTO dto = cuidadorDTO();
        dto.setCpf("10987654321");
        Cuidador existente = cuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.existsByCpf("10987654321")).thenReturn(false);
        when(idosoRepository.existsByCpf("10987654321")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.atualizar(2, dto));
    }

    @Test
    void Atualizar_instituicaoInexistente_lancaResourceNotFound() {
        CuidadorDTO dto = cuidadorDTO();
        Cuidador existente = cuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.atualizar(2, dto));
    }

    @Test
    void Reativar_semDto_ativaCuidador() {
        Cuidador existente = cuidadorCompleto();
        existente.setStatus(Status.INATIVO);

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        CuidadorDTO resultado = service.reativar(2, null);

        assertEquals(Status.ATIVO, resultado.getStatus());
        assertNotNull(existente.getData_atualizacao());
        verify(cuidadorRepository).save(existente);
    }

    @Test
    void Reativar_comCamposParciais_atualizaCamposEnviados() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setNome("Cuidador Reativado");
        dto.setCpf("987.654.321-00");
        dto.setEmail("reativado@email.com");
        dto.setSenha("Nova@123");
        dto.setInstituicaoId(10);
        dto.setContato(contatoDTO());
        dto.getContato().setDdd("(21)");
        Cuidador existente = cuidadorCompleto();
        existente.setStatus(Status.INATIVO);

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(emailValidationService.validarParaAtualizacao("reativado@email.com", 2))
                .thenReturn("reativado@email.com");
        when(cuidadorRepository.existsByCpf("98765432100")).thenReturn(false);
        when(idosoRepository.existsByCpf("98765432100")).thenReturn(false);
        when(passwordEncoder.encode("Nova@123")).thenReturn("hash-nova");
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(cuidadorRepository.save(existente)).thenReturn(existente);

        CuidadorDTO resultado = service.reativar(2, dto);

        assertEquals(Status.ATIVO, resultado.getStatus());
        assertEquals("Cuidador Reativado", resultado.getNome());
        assertEquals("98765432100", resultado.getCpf());
        assertEquals("reativado@email.com", resultado.getEmail());
        assertEquals("hash-nova", existente.getSenha());
        assertEquals("21", existente.getContato().getDdd());
        verify(senhaService).validar("Nova@123");
    }

    @Test
    void Reativar_cpfDeIdoso_lancaDuplicateResource() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setCpf("10987654321");
        Cuidador existente = cuidadorCompleto();
        existente.setStatus(Status.INATIVO);

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));
        when(cuidadorRepository.existsByCpf("10987654321")).thenReturn(false);
        when(idosoRepository.existsByCpf("10987654321")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.reativar(2, dto));
    }

    @Test
    void Reativar_cuidadorInexistente_lancaResourceNotFound() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.reativar(99, null));
    }

    @Test
    void Inativar_cuidadorExistente_salvaStatusInativo() {
        Cuidador existente = cuidadorCompleto();

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(existente));

        service.inativar(2);

        ArgumentCaptor<Cuidador> captor = ArgumentCaptor.forClass(Cuidador.class);
        verify(cuidadorRepository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getData_atualizacao());
    }

    @Test
    void Inativar_cuidadorInexistente_lancaResourceNotFound() {
        when(cuidadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.inativar(99));
    }

    private CuidadorDTO cuidadorDTO() {
        CuidadorDTO dto = new CuidadorDTO();
        dto.setNome("Cuidador");
        dto.setCpf("12345678901");
        dto.setEmail("cuidador@email.com");
        dto.setSenha("Senha@123");
        dto.setInstituicaoId(instituicao().getId());
        dto.setContato(contatoDTO());
        return dto;
    }

    private Cuidador cuidadorCompleto() {
        Cuidador cuidador = cuidador();
        cuidador.setInstituicao(instituicao());
        var contato = contato(5, "11", "999999999");
        contato.setCuidador(cuidador);
        cuidador.setContato(contato);
        return cuidador;
    }
}
