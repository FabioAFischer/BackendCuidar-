package com.example.demo.mappers;

import java.time.LocalDateTime;

import com.example.demo.dtos.AlertasDTO;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Idoso;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.utils.TextoUtils;

public class AlertasMapper {

    public static AlertasDTO toDTO(Alertas alerta) {
        if (alerta == null) {
            return null;
        }

        AlertasDTO dto = new AlertasDTO();
        dto.setId(alerta.getId());
        dto.setIdosoId(alerta.getIdoso() != null ? alerta.getIdoso().getId() : null);
        dto.setIdosoNome(alerta.getIdoso() != null ? TextoUtils.paraExibicao(alerta.getIdoso().getNome()) : null);
        dto.setTipoAlerta(alerta.getTipoAlerta());
        dto.setStatusAlertas(alerta.getStatusAlertas());
        dto.setDataCriacao(alerta.getData_criacao());
        dto.setDataAtualizacao(alerta.getData_atualizacao());
        dto.setDataAgendada(alerta.getData_agendade());

        return dto;
    }

    public static Alertas toEntity(AlertasDTO dto, Idoso idoso) {
        if (dto == null) {
            return null;
        }

        Alertas alerta = new Alertas();
        alerta.setData_criacao(LocalDateTime.now());
        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(dto.getTipoAlerta());
        alerta.setStatusAlertas(dto.getStatusAlertas() != null ? dto.getStatusAlertas() : StatusAlertas.AGENDADO);
        alerta.setData_agendade(dto.getDataAgendada());

        return alerta;
    }

    public static void updateEntity(Alertas alerta, AlertasDTO dto, Idoso idoso) {
        if (alerta == null || dto == null) {
            return;
        }

        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(dto.getTipoAlerta());
        alerta.setData_agendade(dto.getDataAgendada());
        alerta.setData_atualizacao(LocalDateTime.now());

        if (dto.getStatusAlertas() != null) {
            alerta.setStatusAlertas(dto.getStatusAlertas());
        }
    }
}
