package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;

@ExtendWith(MockitoExtension.class)
class RemedioServiceTest {

    @Mock
    private RemedioRepository repository;

    @Mock
    private PrescricaoRepository prescricaoRepository;

    @InjectMocks
    private RemedioService service;

    @Test
    void deveCriarRemedioNovo() {
        RemedioDTO dto = remedioDTO("Dipirona", "Tomar com agua", null);
        Remedio salvo = remedio(1, "Dipirona", "Tomar com agua", Status.ATIVO);

        when(repository.findByNome("Dipirona")).thenReturn(Optional.empty());
        when(repository.save(any(Remedio.class))).thenReturn(salvo);

        RemedioDTO resultado = service.criar(dto);

        assertEquals(1, resultado.getId());
        assertEquals("Dipirona", resultado.getNome());
        assertEquals(Status.ATIVO, resultado.getStatus());
    }

    @Test
    void deveBloquearCriacaoDeRemedioAtivoDuplicado() {
        RemedioDTO dto = remedioDTO("Dipirona", null, null);
        Remedio existente = remedio(1, "Dipirona", null, Status.ATIVO);

        when(repository.findByNome("Dipirona")).thenReturn(Optional.of(existente));

        assertThrows(BusinessException.class, () -> service.criar(dto));
    }

    @Test
    void deveReativarRemedioInativoAoCriarComMesmoNome() {
        RemedioDTO dto = remedioDTO("Dipirona", "Nova observacao", null);
        Remedio existente = remedio(1, "Dipirona", "Antiga observacao", Status.INATIVO);

        when(repository.findByNome("Dipirona")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        RemedioDTO resultado = service.criar(dto);

        assertEquals(Status.ATIVO, resultado.getStatus());
        assertEquals("Nova observacao", resultado.getObservacao());
        verify(repository).save(existente);
    }

    @Test
    void deveBuscarRemedioPorId() {
        Remedio remedio = remedio(1, "Dipirona", null, Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(remedio));

        RemedioDTO resultado = service.buscarPorId(1);

        assertEquals(1, resultado.getId());
        assertEquals("Dipirona", resultado.getNome());
    }

    @Test
    void deveFalharAoBuscarRemedioInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99));
    }

    @Test
    void deveBloquearAtualizacaoComNomeJaEmUso() {
        RemedioDTO dto = remedioDTO("Paracetamol", null, Status.ATIVO);
        Remedio remedio = remedio(1, "Dipirona", null, Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(remedio));
        when(repository.existsByNome("Paracetamol")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.atualizar(1, dto));
    }

    @Test
    void deveInativarRemedioEPrescricoesAtivas() {
        Remedio remedio = remedio(1, "Dipirona", null, Status.ATIVO);
        Prescricao prescricao = new Prescricao();
        prescricao.setStatus(Status.ATIVO);

        when(repository.findById(1)).thenReturn(Optional.of(remedio));
        when(prescricaoRepository.findByRemedioIdAndStatus(1, Status.ATIVO)).thenReturn(List.of(prescricao));

        service.inativar(1);

        ArgumentCaptor<Remedio> captor = ArgumentCaptor.forClass(Remedio.class);
        verify(repository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
        assertEquals(Status.INATIVO, prescricao.getStatus());
    }

    private RemedioDTO remedioDTO(String nome, String observacao, Status status) {
        RemedioDTO dto = new RemedioDTO();
        dto.setNome(nome);
        dto.setObservacao(observacao);
        dto.setStatus(status);
        return dto;
    }

    private Remedio remedio(int id, String nome, String observacao, Status status) {
        Remedio remedio = new Remedio();
        remedio.setId(id);
        remedio.setNome(nome);
        remedio.setObservacao(observacao);
        remedio.setStatus(status);
        return remedio;
    }
}
