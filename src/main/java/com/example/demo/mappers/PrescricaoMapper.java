package com.example.demo.mappers;

import java.time.LocalDateTime;

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;

public class PrescricaoMapper {

    private PrescricaoMapper() {}

    public static PrescricaoDTO toDTO(Prescricao prescricao) {
        if (prescricao == null) return null;

        PrescricaoDTO dto = new PrescricaoDTO();
        dto.setId(prescricao.getId());

        if (prescricao.getRemedio() != null) {
            dto.setRemedioId(prescricao.getRemedio().getId());
            dto.setRemedioNome(prescricao.getRemedio().getNome());
        }

        if (prescricao.getIdoso() != null) {
            dto.setIdosoId(prescricao.getIdoso().getId());
            dto.setIdosoNome(prescricao.getIdoso().getNome());
        }

        dto.setData_criacao(prescricao.getData_criacao());
        dto.setStatus(prescricao.getStatus());
        dto.setData_fim(prescricao.getData_fim());
        dto.setInstrucao(prescricao.getInstrucao());
        dto.setIntervalo(prescricao.getIntervalo());
        dto.setDosagem(prescricao.getDosagem());

        return dto;
    }

    public static Prescricao toEntity(PrescricaoDTO dto, Remedio remedio, Idoso idoso) {
        if (dto == null) return null;

        Prescricao prescricao = new Prescricao();
        updateEntity(prescricao, dto, remedio, idoso);

        if (dto.getData_criacao() != null) {
            prescricao.setData_criacao(dto.getData_criacao());
        } else {
            prescricao.setData_criacao(LocalDateTime.now());
        }

        if (dto.getStatus() != null) {
            prescricao.setStatus(dto.getStatus());
        } else {
            prescricao.setStatus(Status.ATIVO);
        }

        return prescricao;
    }

    public static void updateEntity(Prescricao prescricao, PrescricaoDTO dto, Remedio remedio, Idoso idoso) {
        if (dto == null || prescricao == null) return;

        prescricao.setRemedio(remedio);
        prescricao.setIdoso(idoso);

        if (dto.getData_criacao() != null) {
            prescricao.setData_criacao(dto.getData_criacao());
        }

        if (dto.getStatus() != null) {
            prescricao.setStatus(dto.getStatus());
        }

        prescricao.setData_fim(dto.getData_fim());
        prescricao.setInstrucao(dto.getInstrucao());
        prescricao.setIntervalo(dto.getIntervalo());
        prescricao.setDosagem(dto.getDosagem());
    }
}
