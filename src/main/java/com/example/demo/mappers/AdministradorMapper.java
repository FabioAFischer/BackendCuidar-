package com.example.demo.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

public class AdministradorMapper {

    private AdministradorMapper() {
    }

    public static AdministradorDTO toDTO(Administrador administrador) {
        if (administrador == null) {
            return null;
        }

        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(administrador.getId());
        dto.setNome(administrador.getNome());
        dto.setCpf(administrador.getCpf());
        dto.setEmail(administrador.getEmail());

        return dto;
    }

    public static Administrador toEntity(AdministradorDTO dto) {
        if (dto == null) {
            return null;
        }

        Administrador administrador = new Administrador();

        administrador.setNome(dto.getNome());
        administrador.setCpf(dto.getCpf());
        administrador.setEmail(dto.getEmail());
        administrador.setSenha(dto.getSenha());

        administrador.setData_criacao(LocalDateTime.now());
        administrador.setPerfil(Perfil.ADMINISTRADOR);
        administrador.setStatus(Status.ATIVO);

        return administrador;
    }

    public static void updateEntity(Administrador administrador, AdministradorDTO dto) {
        if (administrador == null || dto == null) {
            return;
        }

        administrador.setNome(dto.getNome());
        administrador.setCpf(dto.getCpf());
        administrador.setEmail(dto.getEmail());
        administrador.setData_atualizacao(LocalDateTime.now());
    }

    public static void inativarEntity(Administrador administrador) {
        if (administrador == null) {
            return;
        }

        administrador.setStatus(Status.INATIVO);
        administrador.setData_atualizacao(LocalDateTime.now());
    }

    public static List<AdministradorDTO> toDTOList(List<Administrador> administradores) {
        List<AdministradorDTO> lista = new ArrayList<>();

        if (administradores == null) {
            return lista;
        }

        for (Administrador administrador : administradores) {
            lista.add(toDTO(administrador));
        }

        return lista;
    }
}
