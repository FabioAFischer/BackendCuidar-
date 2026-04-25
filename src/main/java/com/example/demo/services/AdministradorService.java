package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Status;
import com.example.demo.mappers.AdministradorMapper;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;

    public AdministradorService(AdministradorRepository repository) {
        this.repository = repository;
    }

    public Page<AdministradorDTO> listarAtivos(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(AdministradorMapper::toDTO);
    }

    public AdministradorDTO buscarPorId(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        return AdministradorMapper.toDTO(administrador);
    }

    public AdministradorDTO criar(AdministradorDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe um administrador com esse email");
        }

        Administrador administrador = AdministradorMapper.toEntity(dto);
        Administrador salvo = repository.save(administrador);

        return AdministradorMapper.toDTO(salvo);
    }

    public AdministradorDTO atualizar(Integer id, AdministradorDTO dto) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        if (!administrador.getEmail().equals(dto.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        AdministradorMapper.updateEntity(administrador, dto);

        Administrador atualizado = repository.save(administrador);
        return AdministradorMapper.toDTO(atualizado);
    }

    public void inativar(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        AdministradorMapper.inativarEntity(administrador);

        repository.save(administrador);
    }
}
