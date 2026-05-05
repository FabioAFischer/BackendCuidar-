package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Status;
import com.example.demo.mappers.AdministradorMapper;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<AdministradorDTO> listarAtivos(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(AdministradorMapper::toDTO);
    }

    public AdministradorDTO buscarPorId(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador nao encontrado"));

        return AdministradorMapper.toDTO(administrador);
    }

    public AdministradorDTO criar(AdministradorDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("Ja existe um administrador com esse CPF");
        }

        Administrador administrador = AdministradorMapper.toEntity(dto);
        administrador.setSenha(passwordEncoder.encode(dto.getSenha()));
        Administrador salvo = repository.save(administrador);

        return AdministradorMapper.toDTO(salvo);
    }

    public AdministradorDTO atualizar(Integer id, AdministradorDTO dto) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador nao encontrado"));

        if (!administrador.getCpf().equals(dto.getCpf())
                && repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF ja esta em uso");
        }

        AdministradorMapper.updateEntity(administrador, dto);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            administrador.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Administrador atualizado = repository.save(administrador);
        return AdministradorMapper.toDTO(atualizado);
    }

    public void inativar(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador nao encontrado"));

        AdministradorMapper.inativarEntity(administrador);

        repository.save(administrador);
    }
}
