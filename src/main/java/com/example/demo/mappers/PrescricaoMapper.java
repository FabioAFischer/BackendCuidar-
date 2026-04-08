package com.example.demo.mappers;


import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Prescricao;



public class PrescricaoMapper {

     public static PrescricaoDTO toDTO(Prescricao prescricao) {
        if(prescricao == null) return null;

        PrescricaoDTO dto = new PrescricaoDTO();
        dto.setId(prescricao.getId());
        dto.setRemedio(prescricao.getRemedio());
        dto.setIdoso(prescricao.getIdoso());
        dto.setData_criacao(prescricao.getData_criacao());
        dto.setStatus(prescricao.getStatus());
        dto.setData_fim(prescricao.getData_fim());
        dto.setInstrucao(prescricao.getInstrucao());
        dto.setIntervalo(prescricao.getIntervalo());
        dto.setDosagem(prescricao.getDosagem());

        return dto;
    }

    public static Prescricao toEntity(PrescricaoDTO dto) {
        if (dto == null) return null;

        Prescricao prescricao = new Prescricao();
        prescricao.setId(dto.getId());
        prescricao.setRemedio(dto.getRemedio());
        prescricao.setIdoso(dto.getIdoso());
        prescricao.setData_criacao(dto.getData_criacao());
        prescricao.setStatus(dto.getStatus());
        prescricao.setData_fim(dto.getData_fim());
        prescricao.setInstrucao(dto.getInstrucao());
        prescricao.setIntervalo(dto.getIntervalo());
        prescricao.setDosagem(dto.getDosagem());

        return prescricao;
    }

    public static void updateEntity(Prescricao prescricao, PrescricaoDTO dto) {
        if (dto == null) return;

        prescricao.setRemedio(dto.getRemedio());
        prescricao.setIdoso(dto.getIdoso());
        prescricao.setData_criacao(dto.getData_criacao());
        prescricao.setStatus(dto.getStatus());
        prescricao.setData_fim(dto.getData_fim());
        prescricao.setInstrucao(dto.getInstrucao());
        prescricao.setIntervalo(dto.getIntervalo());
        prescricao.setDosagem(dto.getDosagem());
    }

}
