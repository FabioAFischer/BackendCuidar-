package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.alerta;
import static com.example.demo.support.TestDataFactory.alertaDTO;
import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.prescricao;
import static com.example.demo.support.TestDataFactory.remedio;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.AlertasDTO;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.Status;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;

class AlertasMapperTest {

    @Test
    void deveConverterAlertaParaDTOQuandoAlertaForValido() {
        Prescricao prescricao = prescricao(1, remedio(), idoso(), Status.ATIVO);
        Alertas alerta = alerta(2, idoso(), StatusAlertas.AGENDADO);
        alerta.setPrescricao(prescricao);

        AlertasDTO dto = AlertasMapper.converterAlertaParaDTO(alerta);

        assertEquals(2, dto.getId());
        assertEquals(20, dto.getIdosoId());
        assertEquals(1, dto.getPrescricaoId());
        assertEquals("Dipirona", dto.getRemedioNome());
    }

    @Test
    void deveConverterDTOParaAlertaQuandoDTOForValido() {
        Alertas alerta = AlertasMapper.converterDTOParaAlerta(alertaDTO(), idoso(), null);

        assertEquals(20, alerta.getIdoso().getId());
        assertEquals(TipoAlerta.REMEDIO, alerta.getTipoAlerta());
        assertEquals(StatusAlertas.AGENDADO, alerta.getStatusAlertas());
        assertNotNull(alerta.getData_criacao());
    }

    @Test
    void deveAtualizarAlertaQuandoDTOForValido() {
        Alertas alerta = alerta(2, idoso(), StatusAlertas.AGENDADO);
        AlertasDTO dto = alertaDTO();
        dto.setTipoAlerta(TipoAlerta.CONSULTA);
        dto.setStatusAlertas(StatusAlertas.REALIZADO);

        AlertasMapper.atualizarAlertaComDTO(alerta, dto, idoso(), null);

        assertEquals(TipoAlerta.CONSULTA, alerta.getTipoAlerta());
        assertEquals(StatusAlertas.REALIZADO, alerta.getStatusAlertas());
        assertNotNull(alerta.getData_atualizacao());
    }

    @Test
    void deveRetornarNuloQuandoAlertaForNulo() {
        assertNull(AlertasMapper.converterAlertaParaDTO(null));
    }
}
