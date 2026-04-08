package com.example.demo.mappers;
import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Remedio;


public class RemedioMapper {
    
     public static RemedioDTO toDTO(Remedio remedio) {
        if(remedio == null) return null;

        RemedioDTO dto = new RemedioDTO();
        dto.setId(remedio.getId());
        dto.setNome(remedio.getNome());
        dto.setObservacao(remedio.getObservacao());


        return dto;
    }

    public static Remedio toEntity(RemedioDTO dto) {
        if (dto == null) return null;

        Remedio remedio = new Remedio();
        remedio.setId(dto.getId());
        remedio.setNome(dto.getNome());
        remedio.setObservacao(dto.getObservacao());

        return remedio;
    }

    public static void updateEntity(Remedio remedio, RemedioDTO dto) {
        if (dto == null) return;

        remedio.setNome(dto.getNome());
        remedio.setObservacao(dto.getObservacao());
    }

}
