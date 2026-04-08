package com.example.demo.mappers;

import org.springframework.stereotype.Component;

import com.example.demo.dtos.AlertaDTO;
import com.example.demo.entity.Alerta;
import com.example.demo.entity.Idoso;

@Component
public class AlertaMapper {

    public Alerta toEntity(AlertaDTO dto, Idoso idoso) {
        Alerta alerta = new Alerta();

        alerta.setId(dto.getId());
        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(dto.getTipoAlerta());
        alerta.setStatusAlertas(dto.getStatusAlertas());
        alerta.setData_criacao(dto.getData_Criacao());
        alerta.setData_atualizacao(dto.getData_Atualizacao());
        alerta.setData_agendada(dto.getData_Agendada());

        return alerta;
    }

    public AlertaDTO toDTO(Alerta alerta) {
        AlertaDTO dto = new AlertaDTO();

        dto.setId(alerta.getId());
        dto.setIdosoId(alerta.getIdoso().getId());
        dto.setTipoAlerta(alerta.getTipoAlerta());
        dto.setStatusAlertas(alerta.getStatusAlertas());
        dto.setData_Criacao(alerta.getData_criacao());
        dto.setData_Atualizacao(alerta.getData_atualizacao());
        dto.setData_Agendada(alerta.getData_agendada());

        return dto;
    }
}