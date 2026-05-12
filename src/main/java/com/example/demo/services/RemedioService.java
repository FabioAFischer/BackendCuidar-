// RemedioService.java
package com.example.demo.services;

import java.util.Optional;

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
        Optional<Remedio> remedioExistente = repository.findByNome(dto.getNome());

        if (remedioExistente.isPresent() && remedioExistente.get().getStatus() == Status.ATIVO) {
            throw new BusinessException("Já existe um remédio ativo com esse nome");
        }

        if (remedioExistente.isPresent()) {
            Remedio remedio = remedioExistente.get();
            RemedioMapper.updateEntity(remedio, dto);
            remedio.setStatus(Status.ATIVO);
            return RemedioMapper.toDTO(repository.save(remedio));
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