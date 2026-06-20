package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.AdministradorDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.AdministradorMapper;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SenhaService senhaService;
    private final EmailValidationService emailValidationService;

    public AdministradorService(
            AdministradorRepository repository,
            PasswordEncoder passwordEncoder,
            SenhaService senhaService,
            EmailValidationService emailValidationService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.senhaService = senhaService;
        this.emailValidationService = emailValidationService;
    }

    public Page<AdministradorDTO> listarAdministradoresAtivos(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(AdministradorMapper::converterAdministradorParaDTO);
    }

    public AdministradorDTO buscarAdministradorPorId(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id.longValue()));

        return AdministradorMapper.converterAdministradorParaDTO(administrador);
    }

    public AdministradorDTO cadastrarAdministrador(AdministradorDTO dto) {
        String cpfLimpo = normalizarDocumento(dto.getCpf());
        String email = emailValidationService.validarEmailParaCriacao(dto.getEmail());
        if (repository.existsByCpf(cpfLimpo)) {
            throw new DuplicateResourceException("Já existe um administrador com esse CPF");
        }

        Administrador administrador = AdministradorMapper.converterDTOParaAdministrador(dto);
        administrador.setEmail(email);
        senhaService.validarSenha(dto.getSenha());
        administrador.setSenha(passwordEncoder.encode(dto.getSenha()));
        Administrador salvo = repository.save(administrador);

        return AdministradorMapper.converterAdministradorParaDTO(salvo);
    }

    public AdministradorDTO atualizarAdministrador(Integer id, AdministradorDTO dto) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id.longValue()));

        String cpfLimpo = normalizarDocumento(dto.getCpf());
        if (!administrador.getCpf().equals(cpfLimpo)
                && repository.existsByCpf(cpfLimpo)) {
            throw new DuplicateResourceException("CPF já está em uso");
        }

        String email = emailValidationService.validarEmailParaAtualizacao(dto.getEmail(), administrador.getId());

        AdministradorMapper.atualizarAdministradorComDTO(administrador, dto);
        administrador.setEmail(email);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validarSenha(dto.getSenha());
            administrador.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Administrador atualizado = repository.save(administrador);
        return AdministradorMapper.converterAdministradorParaDTO(atualizado);
    }

    public void inativarAdministrador(Integer id) {
        Administrador administrador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id.longValue()));

        AdministradorMapper.inativarAdministrador(administrador);
        repository.save(administrador);
    }

    private String normalizarDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }
}
