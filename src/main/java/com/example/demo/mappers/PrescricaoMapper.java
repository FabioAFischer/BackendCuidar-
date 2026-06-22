package com.example.demo.mappers;

import java.time.LocalDateTime;

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.utils.TextoUtils;

public class PrescricaoMapper {

    public static PrescricaoDTO converterPrescricaoParaDTO(Prescricao prescricao) {
        if (prescricao == null) {
            return null;
        }

        PrescricaoDTO dto = new PrescricaoDTO();
        dto.setId(prescricao.getId());
        dto.setRemedioId(prescricao.getRemedio() != null ? prescricao.getRemedio().getId() : null);
        dto.setIdosoId(prescricao.getIdoso() != null ? prescricao.getIdoso().getId() : null);
        dto.setRemedioNome(prescricao.getRemedio() != null ? TextoUtils.formatarTextoParaExibicao(prescricao.getRemedio().getNome()) : null);
        dto.setIdosoNome(prescricao.getIdoso() != null ? TextoUtils.formatarTextoParaExibicao(prescricao.getIdoso().getNome()) : null);
        dto.setDataCriacao(prescricao.getData_criacao());
        dto.setDataFim(prescricao.getData_fim());
        dto.setStatus(prescricao.getStatus());
        dto.setNecessarioJejum(prescricao.getNecessario_jejum());
        dto.setInstrucao(TextoUtils.manterTextoLivre(prescricao.getInstrucao()));
        dto.setIntervalo(prescricao.getIntervalo());
        dto.setDosagem(TextoUtils.formatarTextoParaExibicao(prescricao.getDosagem()));

        return dto;
    }

    public static Prescricao converterDTOParaPrescricao(PrescricaoDTO dto, Remedio remedio, Idoso idoso) {
        if (dto == null) {
            return null;
        }

        Prescricao prescricao = new Prescricao();
        prescricao.setData_criacao(LocalDateTime.now());
        prescricao.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.ATIVO);
        atualizarPrescricaoComDTO(prescricao, dto, remedio, idoso);

        return prescricao;
    }

    public static void atualizarPrescricaoComDTO(Prescricao prescricao, PrescricaoDTO dto, Remedio remedio, Idoso idoso) {
        if (prescricao == null || dto == null) {
            return;
        }

        prescricao.setRemedio(remedio);
        prescricao.setIdoso(idoso);
        prescricao.setData_fim(dto.getDataFim());
        prescricao.setNecessario_jejum(Boolean.TRUE.equals(dto.getNecessarioJejum()));
        prescricao.setInstrucao(TextoUtils.manterTextoLivre(dto.getInstrucao()));
        prescricao.setIntervalo(dto.getIntervalo());
        prescricao.setDosagem(TextoUtils.normalizarTextoParaBanco(dto.getDosagem()));

        if (dto.getStatus() != null) {
            prescricao.setStatus(dto.getStatus());
        }
    }
}
