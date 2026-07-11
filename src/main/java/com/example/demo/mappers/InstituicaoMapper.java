package com.example.demo.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;
import com.example.demo.utils.TextoUtils;

public class InstituicaoMapper {

    private InstituicaoMapper() {
    }

    public static InstituicaoDTO converterInstituicaoParaDTO(Instituicao instituicao) {
        if (instituicao == null) {
            return null;
        }

        InstituicaoDTO dto = new InstituicaoDTO();

        dto.setId(instituicao.getId());
        dto.setNome(TextoUtils.formatarTextoParaExibicao(instituicao.getNome()));
        dto.setCnpj(instituicao.getCnpj());
        dto.setEmail(instituicao.getEmail());
        dto.setRua(TextoUtils.formatarTextoParaExibicao(instituicao.getRua()));
        dto.setBairro(TextoUtils.formatarTextoParaExibicao(instituicao.getBairro()));
        dto.setComplemento(TextoUtils.formatarTextoParaExibicao(instituicao.getComplemento()));
        dto.setUf(TextoUtils.normalizarUf(instituicao.getUf()));
        dto.setNumero(instituicao.getNumero());
        dto.setCep(instituicao.getCep());
        dto.setStatus(instituicao.getStatus());

        return dto;
    }

    public static Instituicao converterDTOParaInstituicao(InstituicaoDTO dto) {
        if (dto == null) {
            return null;
        }

        Instituicao instituicao = new Instituicao();
        
        instituicao.setNome(TextoUtils.normalizarTextoParaBanco(dto.getNome()));
        instituicao.setCnpj(normalizarDocumento(dto.getCnpj()));
        instituicao.setEmail(dto.getEmail());
        instituicao.setSenha(dto.getSenha());
        instituicao.setRua(TextoUtils.normalizarTextoParaBanco(dto.getRua()));
        instituicao.setBairro(TextoUtils.normalizarTextoParaBanco(dto.getBairro()));
        instituicao.setComplemento(TextoUtils.normalizarTextoParaBanco(dto.getComplemento()));
        instituicao.setUf(TextoUtils.normalizarTextoParaBanco(dto.getUf()));
        instituicao.setNumero(dto.getNumero());
        instituicao.setCep(normalizarDocumento(dto.getCep()));

        instituicao.setData_criacao(LocalDateTime.now());
        instituicao.setPerfil(Perfil.INSTITUICAO);
        instituicao.setStatus(Status.ATIVO);

        return instituicao;
    }

    public static List<InstituicaoDTO> converterInstituicoesParaDTOs(List<Instituicao> instituicoes) {
        List<InstituicaoDTO> lista = new ArrayList<>();

        if (instituicoes == null) {
            return lista;
        }

        for (Instituicao instituicao : instituicoes) {
            lista.add(converterInstituicaoParaDTO(instituicao));
        }

        return lista;
    }

    private static String normalizarDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }
}
