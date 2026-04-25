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
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        return AdministradorMapper.toDTO(administrador);
    }

    public AdministradorDTO criar(AdministradorDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Já existe um administrador com esse email");
        }

        Administrador administrador = AdministradorMapper.toEntity(dto);
        administrador.setSenha(criptografarSenha(dto.getSenha()));
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
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            administrador.setSenha(criptografarSenha(dto.getSenha()));
        }

        Administrador atualizado = repository.save(administrador);
        return AdministradorMapper.toDTO(atualizado);
    }

    public void inativar(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));

        AdministradorMapper.inativarEntity(administrador);

        repository.save(administrador);
    }

    private String criptografarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Senha é obrigatória");
        }

        if (senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$")) {
            return senha;
        }

        return passwordEncoder.encode(senha);
    }
}
