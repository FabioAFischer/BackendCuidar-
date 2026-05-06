// RemedioService.java
package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.RemedioMapper;
import com.example.demo.repository.RemedioRepository;

@Service
public class RemedioService {

    private final RemedioRepository repository;

    public RemedioService(RemedioRepository repository) {
        this.repository = repository;
    }

    public Page<RemedioDTO> listarAtivas(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable).map(RemedioMapper::toDTO);
    }

    public RemedioDTO buscarPorId(int id) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remédio", (long) id));
        return RemedioMapper.toDTO(remedio);
    }

    public RemedioDTO criar(RemedioDTO dto) {
        if (repository.existsByNome(dto.getNome())) {
            throw new BusinessException("Já existe um remédio com esse nome");
        }
        return RemedioMapper.toDTO(repository.save(RemedioMapper.toEntity(dto)));
    }

    public RemedioDTO atualizar(int id, RemedioDTO dto) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remédio", (long) id));

        if (!remedio.getNome().equals(dto.getNome()) && repository.existsByNome(dto.getNome())) {
            throw new BusinessException("Nome já está em uso");
        }

        RemedioMapper.updateEntity(remedio, dto);
        return RemedioMapper.toDTO(repository.save(remedio));
    }

    public void inativar(int id) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remédio", (long) id));
        remedio.setStatus(Status.INATIVO);
        repository.save(remedio);
    }
}