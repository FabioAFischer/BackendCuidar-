package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.prescricao;
import static com.example.demo.support.TestDataFactory.prescricaoDTO;
import static com.example.demo.support.TestDataFactory.remedio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.AlertasRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;

@ExtendWith(MockitoExtension.class)
class PrescricaoServiceTest {

    @Mock
    private PrescricaoRepository prescricaoRepository;

    @Mock
    private RemedioRepository remedioRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @Mock
    private AlertasRepository alertasRepository;

    @InjectMocks
    private PrescricaoService service;



    @Test
    void deveListarPrescricoesAtivasQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Prescricao prescricao = prescricao(1, remedio(), idoso(), Status.ATIVO);
        Page<Prescricao> pagina = new PageImpl<>(List.of(prescricao), pageable, 1);

        when(prescricaoRepository.findAtivasNaoVencidas(any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pagina);

        Page<PrescricaoDTO> resultado = service.listarPrescricoesAtivas(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().get(0).getId());
        verify(prescricaoRepository).findAtivasNaoVencidas(any(Status.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void deveListarPrescricoesPorIdosoQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Prescricao prescricao = prescricao(1, remedio(), idoso(), Status.ATIVO);
        Page<Prescricao> pagina = new PageImpl<>(List.of(prescricao), pageable, 1);

        when(prescricaoRepository.findAtivasNaoVencidasPorIdoso(any(Integer.class), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pagina);

        Page<PrescricaoDTO> resultado = service.listarPrescricoesPorIdoso(20, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(20, resultado.getContent().get(0).getIdosoId());
        verify(prescricaoRepository).findAtivasNaoVencidasPorIdoso(any(Integer.class), any(Status.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void deveCriarPrescricao() {
        PrescricaoDTO dto = prescricaoDTO();
        Remedio remedio = remedio();
        Idoso idoso = idoso();
        Prescricao salva = prescricao(1, remedio, idoso, Status.ATIVO);
        LocalDateTime inicio = LocalDateTime.of(2026, 6, 11, 8, 0);
        salva.setData_criacao(inicio);
        salva.setData_fim(inicio.plusHours(16));

        when(remedioRepository.findById(10)).thenReturn(Optional.of(remedio));
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(prescricaoRepository.save(any(Prescricao.class))).thenReturn(salva);

        PrescricaoDTO resultado = service.criarPrescricao(dto);

        assertEquals(1, resultado.getId());
        assertEquals(10, resultado.getRemedioId());
        assertEquals(20, resultado.getIdosoId());
        assertEquals("Dipirona", resultado.getRemedioNome());
        assertEquals("Maria", resultado.getIdosoNome());
        assertEquals(Status.ATIVO, resultado.getStatus());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Alertas>> alertasCaptor = ArgumentCaptor.forClass(List.class);
        verify(alertasRepository).saveAll(alertasCaptor.capture());

        List<Alertas> alertas = alertasCaptor.getValue();
        assertEquals(3, alertas.size());
        assertEquals(TipoAlerta.REMEDIO, alertas.get(0).getTipoAlerta());
        assertEquals(StatusAlertas.AGENDADO, alertas.get(0).getStatusAlertas());
        assertEquals(salva, alertas.get(0).getPrescricao());
        assertEquals(idoso, alertas.get(0).getIdoso());
        assertEquals(salva.getData_criacao(), alertas.get(0).getData_agendade());
        assertEquals(salva.getData_criacao().plusHours(8), alertas.get(1).getData_agendade());
    }

    @Test
    void deveAtualizarPrescricao() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDosagem("2 comprimidos");
        Remedio remedio = remedio();
        Idoso idoso = idoso();
        Prescricao existente = prescricao(1, remedio, idoso, Status.ATIVO);
        existente.setData_criacao(LocalDateTime.now().minusHours(8));
        dto.setDataFim(LocalDateTime.now().plusHours(16));
        Alertas alertaAgendado = criarAlertaAgendado(existente, idoso);

        when(prescricaoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(remedioRepository.findById(10)).thenReturn(Optional.of(remedio));
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(alertasRepository.findByPrescricaoIdAndStatusAlertas(1, StatusAlertas.AGENDADO))
                .thenReturn(List.of(alertaAgendado));
        when(prescricaoRepository.save(existente)).thenReturn(existente);

        PrescricaoDTO resultado = service.atualizarPrescricao(1, dto);

        assertEquals("2 Comprimidos", resultado.getDosagem());
        assertEquals(StatusAlertas.CANCELADO, alertaAgendado.getStatusAlertas());
        assertNotNull(alertaAgendado.getData_atualizacao());
        verify(prescricaoRepository).save(existente);
        verify(alertasRepository, times(2)).saveAll(any());
    }

    @Test
    void deveInativarPrescricao() {
        Prescricao prescricao = prescricao(1, remedio(), idoso(), Status.ATIVO);
        Alertas alertaAgendado = criarAlertaAgendado(prescricao, prescricao.getIdoso());

        when(prescricaoRepository.findById(1)).thenReturn(Optional.of(prescricao));
        when(alertasRepository.findByPrescricaoIdAndStatusAlertas(1, StatusAlertas.AGENDADO))
                .thenReturn(List.of(alertaAgendado));

        service.inativarPrescricao(1);

        ArgumentCaptor<Prescricao> captor = ArgumentCaptor.forClass(Prescricao.class);
        verify(prescricaoRepository).save(captor.capture());
        assertEquals(Status.INATIVO, captor.getValue().getStatus());
        assertEquals(StatusAlertas.CANCELADO, alertaAgendado.getStatusAlertas());
        assertNotNull(alertaAgendado.getData_atualizacao());
        verify(alertasRepository).saveAll(List.of(alertaAgendado));
    }

    @Test
    void deveFalharAoCriarComDtoNulo() {
        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(null));
    }

    @Test
    void deveFalharAoCriarSemRemedio() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setRemedioId(null);

        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarSemIdoso() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setIdosoId(null);

        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarSemDosagem() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDosagem(" ");

        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarComIntervaloInvalido() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setIntervalo(0.0);

        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarSemDataFinal() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDataFim(null);

        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarComDataFinalNoPassado() {
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDataFim(LocalDateTime.now().minusMinutes(1));

        assertThrows(InvalidRequestException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarComRemedioInexistente() {
        PrescricaoDTO dto = prescricaoDTO();

        when(remedioRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoCriarComIdosoInexistente() {
        PrescricaoDTO dto = prescricaoDTO();

        when(remedioRepository.findById(10)).thenReturn(Optional.of(remedio()));
        when(idosoRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criarPrescricao(dto));
    }

    @Test
    void deveFalharAoBuscarPrescricaoInexistente() {
        when(prescricaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPrescricaoPorId(99));
    }

    private Alertas criarAlertaAgendado(Prescricao prescricao, Idoso idoso) {
        Alertas alerta = new Alertas();
        alerta.setId(30);
        alerta.setPrescricao(prescricao);
        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(TipoAlerta.REMEDIO);
        alerta.setStatusAlertas(StatusAlertas.AGENDADO);
        alerta.setData_criacao(LocalDateTime.now().minusHours(1));
        alerta.setData_agendade(LocalDateTime.now().plusHours(1));
        return alerta;
    }
}
