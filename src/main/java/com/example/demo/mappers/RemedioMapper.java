package com.example.demo.mappers;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;

public class RemedioMapper {

    public static RemedioDTO toDTO(Remedio remedio) {
        if (remedio == null)
            return null;

        RemedioDTO dto = new RemedioDTO();
        dto.setId(remedio.getId());
        dto.setNome(remedio.getNome());
        dto.setObservacao(remedio.getObservacao());
        dto.setStatus(remedio.getStatus());

        return dto;
    }

    public static Remedio toEntity(RemedioDTO dto) {
        if (dto == null)
            return null;

        Remedio remedio = new Remedio();

        // NÃO SETA ID
        remedio.setNome(dto.getNome());
        remedio.setObservacao(dto.getObservacao());

        if (dto.getStatus() != null) {
            remedio.setStatus(dto.getStatus());
        } else {
            remedio.setStatus(Status.ATIVO);
        }

        return remedio;
    }

    public static void updateEntity(Remedio remedio, RemedioDTO dto) {
        if (dto == null)
            return;

        remedio.setNome(dto.getNome());
        remedio.setObservacao(dto.getObservacao());

        if (dto.getStatus() != null) {
            remedio.setStatus(dto.getStatus());
        }
    }
}