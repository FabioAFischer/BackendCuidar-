package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.alerta;
import static com.example.demo.support.TestDataFactory.alertaDTO;
import static com.example.demo.support.TestDataFactory.criarPrescricao;
import static com.example.demo.support.TestDataFactory.criarRemedio;
import static com.example.demo.support.TestDataFactory.idoso;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dtos.AlertasDTO;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.repository.AlertasRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.VinculoRepository;

@ExtendWith(MockitoExtension.class)
class AlertasServiceTest {

    @Mock
    private AlertasRepository alertasRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @Mock
    private PrescricaoRepository prescricaoRepository;

    @Mock
    private VinculoRepository vinculoRepository;

    @InjectMocks
    private AlertasService service;

    @Test
    void deveCriarAlertaParaIdosoVinculadoAoCuidador() {
        AlertasDTO dto = alertaDTO();
        dto.setTipoAlerta(TipoAlerta.CONSULTA);
        Idoso idoso = idoso();
        Alertas salvo = alerta(1, idoso, StatusAlertas.AGENDADO);
        salvo.setTipoAlerta(TipoAlerta.CONSULTA);

        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(vinculoRepository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(true);
        when(alertasRepository.save(any(Alertas.class))).thenReturn(salvo);

        AlertasDTO resultado = service.criarAlerta(dto, 2);

        assertEquals(1, resultado.getId());
        assertEquals(20, resultado.getIdosoId());
        assertEquals("Maria", resultado.getIdosoNome());
        assertEquals(TipoAlerta.CONSULTA, resultado.getTipoAlerta());
        assertEquals(StatusAlertas.AGENDADO, resultado.getStatusAlertas());
    }

    @Test
    void deveFalharAoCriarAlertaParaIdosoNaoVinculadoAoCuidador() {
        AlertasDTO dto = alertaDTO();
        Idoso idoso = idoso();

        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(vinculoRepository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> service.criarAlerta(dto, 2));
    }

    @Test
    void deveAtualizarAlertaDeIdosoVinculadoAoCuidador() {
        AlertasDTO dto = alertaDTO();
        dto.setTipoAlerta(TipoAlerta.CONSULTA);
        Idoso idoso = idoso();
        Alertas existente = alerta(1, idoso, StatusAlertas.AGENDADO);

        when(alertasRepository.findById(1)).thenReturn(Optional.of(existente));
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(vinculoRepository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(true);
        when(alertasRepository.save(existente)).thenReturn(existente);

        AlertasDTO resultado = service.atualizarAlerta(1, dto, 2);

        assertEquals(TipoAlerta.CONSULTA, resultado.getTipoAlerta());
        verify(alertasRepository).save(existente);
    }

    @Test
    void deveAtualizarStatusDeAlertaDeRemedioSemReenviarPrescricao() {
        Idoso idoso = idoso();
        Prescricao prescricao = criarPrescricao(5, criarRemedio(), idoso, Status.ATIVO);
        Alertas existente = alerta(1, idoso, StatusAlertas.AGENDADO);
        existente.setPrescricao(prescricao);

        AlertasDTO dto = alertaDTO();
        dto.setPrescricaoId(null);
        dto.setStatusAlertas(StatusAlertas.REALIZADO);

        when(alertasRepository.findById(1)).thenReturn(Optional.of(existente));
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso));
        when(vinculoRepository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(true);
        when(alertasRepository.save(existente)).thenReturn(existente);

        AlertasDTO resultado = service.atualizarAlerta(1, dto, 2);

        assertEquals(StatusAlertas.REALIZADO, resultado.getStatusAlertas());
        assertEquals(5, resultado.getPrescricaoId());
        verify(alertasRepository).save(existente);
    }


    @Test
    void deveListarAlertasAtivosDoCuidadorQuandoCuidadorForInformado() {
        Pageable pageable = PageRequest.of(0, 10);
        Alertas alerta = alerta(1, idoso(), StatusAlertas.AGENDADO);
        Page<Alertas> pagina = new PageImpl<>(List.of(alerta), pageable, 1);

        when(alertasRepository.findNaoCanceladosPorCuidador(2, StatusAlertas.CANCELADO, pageable)).thenReturn(pagina);

        Page<AlertasDTO> resultado = service.listarAlertasAtivosDoCuidador(2, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().get(0).getId());
        verify(alertasRepository).findNaoCanceladosPorCuidador(2, StatusAlertas.CANCELADO, pageable);
    }

    @Test
    void deveListarAlertasPorIdosoQuandoCuidadorPossuirVinculo() {
        Pageable pageable = PageRequest.of(0, 10);
        Alertas alerta = alerta(1, idoso(), StatusAlertas.AGENDADO);
        Page<Alertas> pagina = new PageImpl<>(List.of(alerta), pageable, 1);

        when(vinculoRepository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(true);
        when(alertasRepository.findNaoCanceladosPorIdosoECuidador(20, 2, StatusAlertas.CANCELADO, pageable))
                .thenReturn(pagina);

        Page<AlertasDTO> resultado = service.listarAlertasPorIdoso(20, 2, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(20, resultado.getContent().get(0).getIdosoId());
        verify(alertasRepository).findNaoCanceladosPorIdosoECuidador(20, 2, StatusAlertas.CANCELADO, pageable);
    }

    @Test
    void deveListarAlertasDoIdosoQuandoIdosoForInformado() {
        Pageable pageable = PageRequest.of(0, 10);
        Alertas alerta = alerta(1, idoso(), StatusAlertas.AGENDADO);
        Page<Alertas> pagina = new PageImpl<>(List.of(alerta), pageable, 1);

        when(alertasRepository.findByIdosoIdAndStatusAlertasNot(20, StatusAlertas.CANCELADO, pageable)).thenReturn(pagina);

        Page<AlertasDTO> resultado = service.listarAlertasDoIdoso(20, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(StatusAlertas.AGENDADO, resultado.getContent().get(0).getStatusAlertas());
        verify(alertasRepository).findByIdosoIdAndStatusAlertasNot(20, StatusAlertas.CANCELADO, pageable);
    }

    @Test
    void deveConfirmarAlertaQuandoPertencerAoIdoso() {
        Alertas alerta = alerta(1, idoso(), StatusAlertas.AGENDADO);

        when(alertasRepository.findById(1)).thenReturn(Optional.of(alerta));
        when(alertasRepository.save(alerta)).thenReturn(alerta);

        AlertasDTO resultado = service.confirmarAlerta(1, 20);

        assertEquals(StatusAlertas.REALIZADO, resultado.getStatusAlertas());
        verify(alertasRepository).save(alerta);
    }

    @Test
    void deveLancarExcecaoQuandoConfirmarAlertaDeOutroIdoso() {
        Alertas alerta = alerta(1, idoso(), StatusAlertas.AGENDADO);

        when(alertasRepository.findById(1)).thenReturn(Optional.of(alerta));

        assertThrows(UnauthorizedException.class, () -> service.confirmarAlerta(1, 99));
    }

    @Test
    void deveLancarExcecaoQuandoConfirmarAlertaCancelado() {
        Alertas alerta = alerta(1, idoso(), StatusAlertas.CANCELADO);

        when(alertasRepository.findById(1)).thenReturn(Optional.of(alerta));

        assertThrows(InvalidRequestException.class, () -> service.confirmarAlerta(1, 20));
    }

    @Test
    void deveCancelarAlertaDeIdosoVinculadoAoCuidador() {
        Alertas alerta = alerta(1, idoso(), StatusAlertas.AGENDADO);

        when(alertasRepository.findById(1)).thenReturn(Optional.of(alerta));
        when(vinculoRepository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(true);

        service.cancelarAlerta(1, 2);

        ArgumentCaptor<Alertas> captor = ArgumentCaptor.forClass(Alertas.class);
        verify(alertasRepository).save(captor.capture());
        assertEquals(StatusAlertas.CANCELADO, captor.getValue().getStatusAlertas());
    }

    @Test
    void deveFalharAoCriarComDtoNulo() {
        assertThrows(InvalidRequestException.class, () -> service.criarAlerta(null, 2));
    }

    @Test
    void deveFalharAoCriarSemIdoso() {
        AlertasDTO dto = alertaDTO();
        dto.setIdosoId(null);

        assertThrows(InvalidRequestException.class, () -> service.criarAlerta(dto, 2));
    }

    @Test
    void deveFalharAoCriarSemTipoAlerta() {
        AlertasDTO dto = alertaDTO();
        dto.setTipoAlerta(null);

        assertThrows(InvalidRequestException.class, () -> service.criarAlerta(dto, 2));
    }

    @Test
    void deveFalharAoCriarSemDataAgendada() {
        AlertasDTO dto = alertaDTO();
        dto.setDataAgendada(null);

        assertThrows(InvalidRequestException.class, () -> service.criarAlerta(dto, 2));
    }

    @Test
    void deveFalharAoCriarComIdosoInexistente() {
        AlertasDTO dto = alertaDTO();

        when(idosoRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.criarAlerta(dto, 2));
    }

    @Test
    void deveFalharAoBuscarAlertaInexistente() {
        when(alertasRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarAlertaPorId(99, 2));
    }

    @Test
    void deveFalharSemCuidadorAutenticado() {
        assertThrows(UnauthorizedException.class, () -> service.criarAlerta(alertaDTO(), null));
    }
}
