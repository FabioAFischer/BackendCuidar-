package com.example.demo.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.AlertaDTO;
import com.example.demo.entity.Alerta;
import com.example.demo.entity.Idoso;
import com.example.demo.enums.StatusAlertas;

public class AlertaMapper {

    private AlertaMapper() {
    }

    public static Alerta toEntity(AlertaDTO dto, Idoso idoso) {
        if (dto == null) {
            return null;
        }

        Alerta alerta = new Alerta();

        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(dto.getTipoAlerta());
        alerta.setData_criacao(LocalDateTime.now());
        alerta.setData_agendada(dto.getDataAgendada());

        if (dto.getStatusAlertas() != null) {
            alerta.setStatusAlertas(dto.getStatusAlertas());
        } else {
            alerta.setStatusAlertas(StatusAlertas.AGENDADO);
        }

        return alerta;
    }

    public static AlertaDTO toDTO(Alerta alerta) {
        if (alerta == null) {
            return null;
        }

        AlertaDTO dto = new AlertaDTO();

        dto.setId(alerta.getId());
        if (alerta.getIdoso() != null) {
            dto.setIdosoId(alerta.getIdoso().getId());
        }
        dto.setTipoAlerta(alerta.getTipoAlerta());
        dto.setStatusAlertas(alerta.getStatusAlertas());
        dto.setDataCriacao(alerta.getData_criacao());
        dto.setDataAtualizacao(alerta.getData_atualizacao());
        dto.setDataAgendada(alerta.getData_agendada());

        return dto;
    }

    public static void updateEntity(Alerta alerta, AlertaDTO dto, Idoso idoso) {
        if (alerta == null || dto == null) {
            return;
        }

        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(dto.getTipoAlerta());
        alerta.setStatusAlertas(dto.getStatusAlertas());
        alerta.setData_agendada(dto.getDataAgendada());
        alerta.setData_atualizacao(LocalDateTime.now());
    }

    public static void inativarEntity(Alerta alerta) {
        if (alerta == null) {
            return;
        }

        alerta.setStatusAlertas(StatusAlertas.CANCELADO);
        alerta.setData_atualizacao(LocalDateTime.now());
    }

    public static List<AlertaDTO> toDTOList(List<Alerta> alertas) {
        List<AlertaDTO> lista = new ArrayList<>();

        if (alertas == null) {
            return lista;
        }

        for (Alerta alerta : alertas) {
            lista.add(toDTO(alerta));
        }

        return lista;
    }
}
