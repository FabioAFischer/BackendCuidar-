package com.example.demo.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;

public class ContatoMapper {

    public static ContatoDTO converterContatoParaDTO(Contato contato) {
        if (contato == null) return null;

        ContatoDTO dto = new ContatoDTO();
        dto.setId(contato.getId());
        dto.setDdd(contato.getDdd());
        dto.setTelefone(contato.getTelefone());

        if (contato.getCuidador() != null) {
            dto.setCuidadorId(contato.getCuidador().getId());
        }

        if (contato.getIdosos() != null) {
            List<Integer> idososIds = contato.getIdosos()
                    .stream()
                    .map(Idoso::getId)
                    .collect(Collectors.toList());

            dto.setIdosos(idososIds);
        }

        return dto;
    }

    public static Contato converterDTOParaContato(ContatoDTO dto, Cuidador cuidador, List<Idoso> idosos) {
        if (dto == null) return null;

        Contato contato = new Contato();
        
        contato.setDdd(normalizarNumero(dto.getDdd()));
        contato.setTelefone(normalizarNumero(dto.getTelefone()));
        contato.setCuidador(cuidador);
        contato.setIdosos(idosos);

        return contato;
    }

    public static void atualizarContatoComDTO(Contato contato, ContatoDTO dto, Cuidador cuidador, List<Idoso> idosos) {
        if (dto == null) return;

        contato.setDdd(normalizarNumero(dto.getDdd()));
        contato.setTelefone(normalizarNumero(dto.getTelefone()));
        contato.setCuidador(cuidador);
        contato.setIdosos(idosos);
    }

    private static String normalizarNumero(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }
}
