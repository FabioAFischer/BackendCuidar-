// InstituicaoService.java
package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.InstituicaoMapper;
import com.example.demo.repository.InstituicaoRepository;

@Service
public class InstituicaoService {

    private final InstituicaoRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SenhaService senhaService;

    public InstituicaoService(InstituicaoRepository repository, PasswordEncoder passwordEncoder, SenhaService senhaService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.senhaService = senhaService;
    }

    public Page<InstituicaoDTO> listarTodas(Pageable pageable) {
    return repository.findAll(pageable)
            .map(InstituicaoMapper::toDTO);
}

    public InstituicaoDTO buscarPorId(Integer id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));
        return InstituicaoMapper.toDTO(instituicao);
    }

    public InstituicaoDTO criar(InstituicaoDTO dto) {
        if (repository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("Já existe uma instituição com esse CNPJ");
        }
        Instituicao instituicao = InstituicaoMapper.toEntity(dto);
        senhaService.validar(dto.getSenha());
        instituicao.setSenha(passwordEncoder.encode(dto.getSenha()));
        return InstituicaoMapper.toDTO(repository.save(instituicao));
    }

    public InstituicaoDTO atualizar(Integer id, InstituicaoDTO dto) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));

        if (!instituicao.getCnpj().equals(dto.getCnpj()) && repository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("CNPJ já está em uso");
        }

        instituicao.setNome(dto.getNome());
        instituicao.setCnpj(dto.getCnpj());
        instituicao.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validar(dto.getSenha());
            instituicao.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        instituicao.setBairro(dto.getBairro());
        instituicao.setUf(dto.getUf());
        instituicao.setNumero(dto.getNumero());
        instituicao.setCep(dto.getCep());
        instituicao.setData_atualizacao(LocalDateTime.now());

        return InstituicaoMapper.toDTO(repository.save(instituicao));
    }

    public void inativar(Integer id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));
        instituicao.setStatus(Status.INATIVO);
        instituicao.setData_atualizacao(LocalDateTime.now());
        repository.save(instituicao);
    }

    public void ativar(Integer id) {
    Instituicao instituicao = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));

    instituicao.setStatus(Status.ATIVO);
    instituicao.setData_atualizacao(LocalDateTime.now());

    repository.save(instituicao);
}
}
