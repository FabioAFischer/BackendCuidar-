package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.mappers.InstituicaoMapper;
import com.example.demo.repository.InstituicaoRepository;

@Service
public class InstituicaoService {

    private final InstituicaoRepository repository;

    public InstituicaoService(InstituicaoRepository repository) {
        this.repository = repository;
    }

    public Page<InstituicaoDTO> listarAtivas(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(InstituicaoMapper::toDTO);
    }

    public InstituicaoDTO buscarPorId(Integer id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        return InstituicaoMapper.toDTO(instituicao);
    }

    public InstituicaoDTO criar(InstituicaoDTO dto) {
        if (repository.existsByCnpj(dto.getCnpj())) {
            throw new RuntimeException("Já existe uma instituição com esse CNPJ");
        }

        Instituicao instituicao = InstituicaoMapper.toEntity(dto);
        Instituicao salva = repository.save(instituicao);

        return InstituicaoMapper.toDTO(salva);
    }

    public InstituicaoDTO atualizar(Integer id, InstituicaoDTO dto) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        if (!instituicao.getCnpj().equals(dto.getCnpj())
                && repository.existsByCnpj(dto.getCnpj())) {
            throw new RuntimeException("CNPJ já está em uso");
        }

        instituicao.setNome(dto.getNome());
        instituicao.setCnpj(dto.getCnpj());
        instituicao.setBairro(dto.getBairro());
        instituicao.setUf(dto.getUf());
        instituicao.setNumero(dto.getNumero());
        instituicao.setCep(dto.getCep());
        instituicao.setData_atualizacao(LocalDateTime.now());

        Instituicao atualizada = repository.save(instituicao);
        return InstituicaoMapper.toDTO(atualizada);
    }

    public void inativar(Integer id) {
        Instituicao instituicao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        instituicao.setStatus(Status.INATIVO);
        instituicao.setData_atualizacao(LocalDateTime.now());

        repository.save(instituicao);
    }
}