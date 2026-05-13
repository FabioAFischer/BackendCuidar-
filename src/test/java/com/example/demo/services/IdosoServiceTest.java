package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.idosoDTO;
import static com.example.demo.support.TestDataFactory.instituicao;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dtos.IdosoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Idoso;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.ContatoRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class IdosoServiceTest {

    @Mock
    private IdosoRepository idosoRepository;

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

        when(idosoRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(contatoRepository.save(any(Contato.class))).thenReturn(contatoSalvo);
        when(idosoRepository.save(any(Idoso.class))).thenAnswer(invocation -> {
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

        when(idosoRepository.findByCpf("12345678901")).thenReturn(Optional.of(existente));

        assertThrows(BusinessException.class, () -> service.criar(idosoDTO()));
    }

    @Test
    void deveReativarIdosoInativoAoCriarComMesmoCpf() {
        IdosoDTO dto = idosoDTO();
        Idoso existente = idoso(1, "Maria Antiga", "12345678901", Status.INATIVO);
        Contato contatoSalvo = contato(5, "11", "999999999");

        when(idosoRepository.findByCpf("12345678901")).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(contatoRepository.save(any(Contato.class))).thenReturn(contatoSalvo);
        when(idosoRepository.save(existente)).thenReturn(existente);

        IdosoDTO resultado = service.criar(dto);

        assertEquals(1, resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals(Status.ATIVO, resultado.getStatus());
        assertEquals(5, resultado.getContatoId());
    }

    @Test
    void deveFalharAoCriarComInstituicaoInexistente() {
        when(idosoRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criar(idosoDTO()));
    }

    @Test
    void deveFalharAoCriarComContatoIncompleto() {
        IdosoDTO dto = idosoDTO();
        dto.getContato().setTelefone(null);

        when(idosoRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarSemContato() {
        IdosoDTO dto = idosoDTO();
        dto.setContato(null);
        dto.setContatoId(null);

        when(idosoRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
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

        when(idosoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao()));
        when(contatoRepository.findById(5)).thenReturn(Optional.of(contato(5, "11", "999999999")));
        when(idosoRepository.save(existente)).thenReturn(existente);

        IdosoDTO resultado = service.atualizar(1, dto);

        assertEquals("Maria Atualizada", resultado.getNome());
        assertEquals(5, resultado.getContatoId());
        verify(idosoRepository).save(existente);
    }

    @Test
    void deveBloquearAtualizacaoComCpfJaEmUso() {
        IdosoDTO dto = idosoDTO();
        dto.setCpf("10987654321");
        Idoso existente = idoso(1, "Maria", "12345678901", Status.ATIVO);

        when(idosoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(idosoRepository.existsByCpf("10987654321")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.atualizar(1, dto));
    }

    @Test
    void deveInativarIdoso() {
        Idoso existente = idoso(1, "Maria", "12345678901", Status.ATIVO);

        when(idosoRepository.findById(1)).thenReturn(Optional.of(existente));

        service.inativar(1);

        ArgumentCaptor<Idoso> captor = ArgumentCaptor.forClass(Idoso.class);
        verify(idosoRepository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
    }

    @Test
    void deveFalharAoBuscarIdosoInexistente() {
        when(idosoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99));
    }
}
