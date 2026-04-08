package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Instituicao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.mappers.InstituicaoMapper;
import com.example.demo.mappers.RemedioMapper;

import com.example.demo.repository.RemedioRepository;


@Service
public class RemedioService {

    private final RemedioRepository repository;

    public RemedioService(RemedioRepository repository) {
        this.repository = repository;
    }

    public Page<RemedioDTO> listarAtivas(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(RemedioMapper::toDTO);
    }

    public RemedioDTO buscarPorId(int id) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remédio não encontrado"));

        return RemedioMapper.toDTO(remedio);
    }

    public RemedioDTO criar(RemedioDTO dto) {
        if (repository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Já existe um remédio com esse nome");
        }

        Remedio remedio = RemedioMapper.toEntity(dto);
        Remedio salva = repository.save(remedio);

        return RemedioMapper.toDTO(salva);
    }


    public RemedioDTO atualizar(int id, RemedioDTO dto) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remédio não encontrado"));

        if (!remedio.getNome().equals(dto.getNome())
                && repository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Nome já está em uso");
        }

        remedio.setNome(dto.getNome());
        remedio.setObservacao(dto.getObservacao());
        

        Remedio atualizado = repository.save(remedio);
        return RemedioMapper.toDTO(atualizado);
    }

    public void inativar(int id) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remédio não encontrado"));

        remedio.setStatus(Status.INATIVO);

        repository.save(remedio);
    }


}
