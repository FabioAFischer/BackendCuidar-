package com.example.demo.mappers;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.utils.TextoUtils;

public class RemedioMapper {

    public static RemedioDTO converterRemedioParaDTO(Remedio remedio) {
        if (remedio == null)
            return null;

        RemedioDTO dto = new RemedioDTO();
        dto.setId(remedio.getId());
        dto.setNome(TextoUtils.formatarTextoParaExibicao(remedio.getNome()));
        dto.setObservacao(TextoUtils.formatarTextoParaExibicao(remedio.getObservacao()));
        dto.setStatus(remedio.getStatus());

        return dto;
    }

    public static Remedio converterDTOParaRemedio(RemedioDTO dto) {
        if (dto == null)
            return null;

        Remedio remedio = new Remedio();

        remedio.setNome(TextoUtils.normalizarTextoParaBanco(dto.getNome()));
        remedio.setObservacao(TextoUtils.normalizarTextoParaBanco(dto.getObservacao()));

        if (dto.getStatus() != null) {
            remedio.setStatus(dto.getStatus());
        } else {
            remedio.setStatus(Status.ATIVO);
        }

        return remedio;
    }

    public static void atualizarRemedioComDTO(Remedio remedio, RemedioDTO dto) {
        if (dto == null)
            return;

        remedio.setNome(TextoUtils.normalizarTextoParaBanco(dto.getNome()));
        remedio.setObservacao(TextoUtils.normalizarTextoParaBanco(dto.getObservacao()));

        if (dto.getStatus() != null) {
            remedio.setStatus(dto.getStatus());
        }
    }
}
