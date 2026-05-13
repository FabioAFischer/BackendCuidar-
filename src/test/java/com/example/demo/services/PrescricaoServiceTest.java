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

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;

@ExtendWith(MockitoExtension.class)
class PrescricaoServiceTest {

    @Mock
    private PrescricaoRepository repository;

    @Mock
    private RemedioRepository remedioRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @InjectMocks
    private PrescricaoService service;

    @Test
    void deveCriarPrescricao() {
        PrescricaoDTO dto = prescricaoDTO();
        Remedio remedio = remedio();
        Idoso idoso = idoso();
        Prescricao salva = prescricao(1, remedio, idoso, Status.ATIVO);

        when(remedioRepository.findById(10)).thenReturn(Optional.of(remedio));
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(repository.save(any(Prescricao.class))).thenReturn(salva);

        PrescricaoDTO resultado = service.criar(dto);

        assertEquals(1, resultado.getId());
        assertEquals(10, resultado.getRemedioId());
        assertEquals(20, resultado.getIdosoId());
        assertEquals("Dipirona", resultado.getRemedioNome());
        assertEquals("Maria", resultado.getIdosoNome());
        assertEquals(Status.ATIVO, resultado.getStatus());
    }

    @Test
    void deveAtualizarPrescricao() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDosagem("2 comprimidos");
        Remedio remedio = remedio();
        Idoso idoso = idoso();
        Prescricao existente = prescricao(1, remedio, idoso, Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(existente));
        when(remedioRepository.findById(10)).thenReturn(Optional.of(remedio));
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(repository.save(existente)).thenReturn(existente);

        PrescricaoDTO resultado = service.atualizar(1, dto);

        assertEquals("2 comprimidos", resultado.getDosagem());
        verify(repository).save(existente);
    }

    @Test
    void deveInativarPrescricao() {
        Prescricao prescricao = prescricao(1, remedio(), idoso(), Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(prescricao));

        service.inativar(1);

        ArgumentCaptor<Prescricao> captor = ArgumentCaptor.forClass(Prescricao.class);
        verify(repository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
    }

    @Test
    void deveFalharAoCriarComDtoNulo() {
        assertThrows(BusinessException.class, () -> service.criar(null));
    }

    @Test
    void deveFalharAoCriarSemRemedio() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setRemedioId(null);

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarSemIdoso() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setIdosoId(null);

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarSemDosagem() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDosagem(" ");

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarComIntervaloInvalido() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setIntervalo(0.0);

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarComRemedioInexistente() {
        PrescricaoDTO dto = prescricaoDTO();

        when(remedioRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoCriarComIdosoInexistente() {
        PrescricaoDTO dto = prescricaoDTO();

        when(remedioRepository.findById(10)).thenReturn(Optional.of(remedio()));
        when(idosoRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criar(dto));
    }

    @Test
    void deveFalharAoBuscarPrescricaoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99));
    }

    private PrescricaoDTO prescricaoDTO() {
        PrescricaoDTO dto = new PrescricaoDTO();
        dto.setRemedioId(10);
        dto.setIdosoId(20);
        dto.setDosagem("1 comprimido");
        dto.setIntervalo(8.0);
        dto.setNecessarioJejum(false);
        dto.setInstrucao("Tomar com agua");
        dto.setDataFim(LocalDateTime.now().plusDays(7));
        return dto;
    }

    private Prescricao prescricao(int id, Remedio remedio, Idoso idoso, Status status) {
        Prescricao prescricao = new Prescricao();
        prescricao.setId(id);
        prescricao.setRemedio(remedio);
        prescricao.setIdoso(idoso);
        prescricao.setData_criacao(LocalDateTime.now());
        prescricao.setData_fim(LocalDateTime.now().plusDays(7));
        prescricao.setStatus(status);
        prescricao.setNecessario_jejum(false);
        prescricao.setInstrucao("Tomar com agua");
        prescricao.setIntervalo(8.0);
        prescricao.setDosagem("1 comprimido");
        return prescricao;
    }

    private Remedio remedio() {
        Remedio remedio = new Remedio();
        remedio.setId(10);
        remedio.setNome("Dipirona");
        remedio.setStatus(Status.ATIVO);
        return remedio;
    }

    private Idoso idoso() {
        Idoso idoso = new Idoso();
        idoso.setId(20);
        idoso.setNome("Maria");
        idoso.setStatus(Status.ATIVO);
        return idoso;
    }
}
