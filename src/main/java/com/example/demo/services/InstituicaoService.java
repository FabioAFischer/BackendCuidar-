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
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.InstituicaoMapper;
import com.example.demo.repository.InstituicaoRepository;
import com.example.demo.utils.TextoUtils;

@Service
public class InstituicaoService {

    private final InstituicaoRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SenhaService senhaService;
    private final EmailValidationService emailValidationService;

    public InstituicaoService(
            InstituicaoRepository repository,
            PasswordEncoder passwordEncoder,
            SenhaService senhaService,
            EmailValidationService emailValidationService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.senhaService = senhaService;
        this.emailValidationService = emailValidationService;
    }

    public Page<InstituicaoDTO> listarInstituicoesAtivas(Pageable pageable) {
    return repository.findAll(pageable)
            .map(InstituicaoMapper::converterInstituicaoParaDTO);
}

    public InstituicaoDTO buscarInstituicaoPorId(Integer id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));
        return InstituicaoMapper.converterInstituicaoParaDTO(instituicao);
    }

    public InstituicaoDTO cadastrarInstituicao(InstituicaoDTO dto) {
        String cnpjLimpo = normalizarDocumento(dto.getCnpj());
        String email = emailValidationService.validarEmailParaCriacao(dto.getEmail());
        if (repository.existsByCnpj(cnpjLimpo)) {
            throw new DuplicateResourceException("Já existe uma instituição com esse CNPJ");
        }
        Instituicao instituicao = InstituicaoMapper.converterDTOParaInstituicao(dto);
        instituicao.setEmail(email);
        senhaService.validarSenha(dto.getSenha());
        instituicao.setSenha(passwordEncoder.encode(dto.getSenha()));
        return InstituicaoMapper.converterInstituicaoParaDTO(repository.save(instituicao));
    }

    public InstituicaoDTO atualizarInstituicao(Integer id, InstituicaoDTO dto) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));

        String cnpjLimpo = normalizarDocumento(dto.getCnpj());
        if (!instituicao.getCnpj().equals(cnpjLimpo) && repository.existsByCnpj(cnpjLimpo)) {
            throw new DuplicateResourceException("CNPJ já está em uso");
        }

        String email = emailValidationService.validarEmailParaAtualizacao(dto.getEmail(), instituicao.getId());

        instituicao.setNome(TextoUtils.normalizarTextoParaBanco(dto.getNome()));
        instituicao.setCnpj(cnpjLimpo);
        instituicao.setEmail(email);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validarSenha(dto.getSenha());
            instituicao.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        instituicao.setRua(TextoUtils.normalizarTextoParaBanco(dto.getRua()));
        instituicao.setBairro(TextoUtils.normalizarTextoParaBanco(dto.getBairro()));
        instituicao.setUf(TextoUtils.normalizarTextoParaBanco(dto.getUf()));
        instituicao.setNumero(dto.getNumero());
        instituicao.setCep(normalizarDocumento(dto.getCep()));
        instituicao.setData_atualizacao(LocalDateTime.now());

        return InstituicaoMapper.converterInstituicaoParaDTO(repository.save(instituicao));
    }

    public void inativarInstituicao(Integer id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));
        instituicao.setStatus(Status.INATIVO);
        instituicao.setData_atualizacao(LocalDateTime.now());
        repository.save(instituicao);
    }

    public void reativarInstituicao(Integer id) {
    Instituicao instituicao = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Instituição", id.longValue()));

    instituicao.setStatus(Status.ATIVO);
    instituicao.setData_atualizacao(LocalDateTime.now());

    repository.save(instituicao);
}

    private String normalizarDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }
}
