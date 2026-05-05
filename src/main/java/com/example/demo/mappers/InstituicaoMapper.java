package com.example.demo.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

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
        dto.setSenha(instituicao.getSenha());
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
        
        instituicao.setNome(dto.getNome());
        instituicao.setCnpj(dto.getCnpj());
        instituicao.setSenha(dto.getSenha());
        instituicao.setBairro(dto.getBairro());
        instituicao.setUf(dto.getUf());
        instituicao.setNumero(dto.getNumero());
        instituicao.setCep(dto.getCep());

        instituicao.setData_criacao(LocalDateTime.now());
        instituicao.setPerfil(Perfil.INSTITUICAO);
        instituicao.setStatus(Status.ATIVO);

        return instituicao;
    }

    public static List<InstituicaoDTO> toDTOList(List<Instituicao> instituicoes) {
        List<InstituicaoDTO> lista = new ArrayList<>();

        if (instituicoes == null) {
            return lista;
        }

        for (Instituicao instituicao : instituicoes) {
            lista.add(toDTO(instituicao));
        }

        return lista;
    }
}
