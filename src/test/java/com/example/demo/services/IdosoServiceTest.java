package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.IdosoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.ContatoRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class IdosoServiceTest {

    @Mock
    private IdosoRepository repository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private ContatoRepository contatoRepository;

    @InjectMocks
    private IdosoService service;

    @Test
    void deveCriarIdosoComContatoNovo() {
        IdosoDTO dto = idosoDTO();
        Contato contatoSalvo = contato(5, "11", "999999999");

        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(contatoRepository.save(any(Contato.class))).thenReturn(contatoSalvo);
        when(repository.save(any(Idoso.class))).thenAnswer(invocation -> {
            Idoso idoso = invocation.getArgument(0);
            idoso.setId(1);
            return idoso;
        });

        IdosoDTO resultado = service.criar(dto);

        assertEquals(1, resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals("12345678901", resultado.getCpf());
        assertEquals(10, resultado.getInstituicaoId());
        assertEquals(5, resultado.getContatoId());
        assertEquals(Status.ATIVO, resultado.getStatus());
    }

    @Test
    void deveBloquearCriacaoComCpfAtivoDuplicado() {
        Idoso existente = idoso(1, "Maria", "12345678901", Status.ATIVO);

        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(existente));

        assertThrows(BusinessException.class, () -> service.criar(idosoDTO()));
    }

    @Test
    void deveReativarIdosoInativoAoCriarComMesmoCpf() {
        IdosoDTO dto = idosoDTO();
        Idoso existente = idoso(1, "Maria Antiga", "12345678901", Status.INATIVO);
        Contato contatoSalvo = contato(5, "11", "999999999");

        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(contatoRepository.save(any(Contato.class))).thenReturn(contatoSalvo);
        when(repository.save(existente)).thenReturn(existente);

        IdosoDTO resultado = service.criar(dto);

        assertEquals(1, resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals(Status.ATIVO, resultado.getStatus());
        assertEquals(5, resultado.getContatoId());
    }

    @Test
    void deveFalharAoCriarComInstituicaoInexistente() {
        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criar(idosoDTO()));
    }

    @Test
    void deveFalharAoCriarComContatoIncompleto() {
        IdosoDTO dto = idosoDTO();
        dto.getContato().setTelefone(null);

        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarSemContato() {
        IdosoDTO dto = idosoDTO();
        dto.setContato(null);
        dto.setContatoId(null);

        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveAtualizarIdosoComContatoExistente() {
        IdosoDTO dto = idosoDTO();
        dto.setNome("Maria Atualizada");
        dto.setContato(null);
        dto.setContatoId(5);
        Idoso existente = idoso(1, "Maria", "12345678901", Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(contatoRepository.findById(5)).thenReturn(Optional.of(contato(5, "11", "999999999")));
        when(repository.save(existente)).thenReturn(existente);

        IdosoDTO resultado = service.atualizar(1, dto);

        assertEquals("Maria Atualizada", resultado.getNome());
        assertEquals(5, resultado.getContatoId());
        verify(repository).save(existente);
    }

    @Test
    void deveBloquearAtualizacaoComCpfJaEmUso() {
        IdosoDTO dto = idosoDTO();
        dto.setCpf("10987654321");
        Idoso existente = idoso(1, "Maria", "12345678901", Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(repository.existsByCpf("10987654321")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.atualizar(1, dto));
    }

    @Test
    void deveInativarIdoso() {
        Idoso existente = idoso(1, "Maria", "12345678901", Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(existente));

        service.inativar(1);

        ArgumentCaptor<Idoso> captor = ArgumentCaptor.forClass(Idoso.class);
        verify(repository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
    }

    @Test
    void deveFalharAoBuscarIdosoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99));
    }

    private IdosoDTO idosoDTO() {
        IdosoDTO dto = new IdosoDTO();
        dto.setNome("Maria");
        dto.setCpf("12345678901");
        dto.setObservacoes("Alergia a dipirona");
        dto.setInstituicaoId(10);
        dto.setContato(contatoDTO());
        return dto;
    }

    private ContatoDTO contatoDTO() {
        ContatoDTO dto = new ContatoDTO();
        dto.setDdd("11");
        dto.setTelefone("999999999");
        return dto;
    }

    private Idoso idoso(int id, String nome, String cpf, Status status) {
        Idoso idoso = new Idoso();
        idoso.setId(id);
        idoso.setNome(nome);
        idoso.setCpf(cpf);
        idoso.setObservacoes("Alergia a dipirona");
        idoso.setInstituicao(instituicao());
        idoso.setContato(contato(5, "11", "999999999"));
        idoso.setData_criacao(LocalDateTime.now());
        idoso.setPerfil(Perfil.IDOSO);
        idoso.setStatus(status);
        return idoso;
    }

    private Instituicao instituicao() {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(10);
        instituicao.setNome("Instituicao Bom Cuidado");
        instituicao.setStatus(Status.ATIVO);
        return instituicao;
    }

    private Contato contato(Integer id, String ddd, String telefone) {
        Contato contato = new Contato();
        contato.setId(id);
        contato.setDdd(ddd);
        contato.setTelefone(telefone);
        return contato;
    }
}
