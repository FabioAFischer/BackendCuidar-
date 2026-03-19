package com.example.demo.mappers;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;

public class InstituicaoMapper {

    private InstituicaoMapper() {
    }

    public static InstituicaoDTO toDTO(Instituicao instituicao) {
        if (instituicao == null) {
            return null;
        }

        InstituicaoDTO dto = new InstituicaoDTO();
        dto.setId(instituicao.getId());
        dto.setNome(instituicao.getNome());
        dto.setCnpj(instituicao.getCnpj());
        dto.setBairro(instituicao.getBairro());
        dto.setUf(instituicao.getUf());
        dto.setNumero(instituicao.getNumero());
        dto.setCep(instituicao.getCep());

        return dto;
    }

    public static Instituicao toEntity(InstituicaoDTO dto) {
        if (dto == null) {
            return null;
        }

        Instituicao instituicao = new Instituicao();
        instituicao.setId(dto.getId());
        instituicao.setNome(dto.getNome());
        instituicao.setCnpj(dto.getCnpj());
        instituicao.setBairro(dto.getBairro());
        instituicao.setUf(dto.getUf());
        instituicao.setNumero(dto.getNumero());
        instituicao.setCep(dto.getCep());

        return instituicao;
    }

}