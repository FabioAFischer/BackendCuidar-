package com.example.demo.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;

public class ContatoMapper {

    public static ContatoDTO toDTO(Contato contato) {
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

    public static Contato toEntity(ContatoDTO dto, Cuidador cuidador, List<Idoso> idosos) {
        if (dto == null) return null;

        Contato contato = new Contato();
        contato.setId(dto.getId());
        contato.setDdd(dto.getDdd());
        contato.setTelefone(dto.getTelefone());
        contato.setCuidador(cuidador);
        contato.setIdosos(idosos);

        return contato;
    }

    public static void atualizarContato(Contato contato, ContatoDTO dto, Cuidador cuidador, List<Idoso> idosos) {
        if (dto == null) return;

        contato.setDdd(dto.getDdd());
        contato.setTelefone(dto.getTelefone());
        contato.setCuidador(cuidador);
        contato.setIdosos(idosos);
    }
}