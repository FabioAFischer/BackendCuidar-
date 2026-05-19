// RemedioService.java
package com.example.demo.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.RemedioMapper;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;
import com.example.demo.utils.TextoUtils;

@Service
public class RemedioService {

    private final RemedioRepository repository;
    private final PrescricaoRepository prescricaoRepository;

    public RemedioService(RemedioRepository repository, PrescricaoRepository prescricaoRepository) {
        this.repository = repository;
        this.prescricaoRepository = prescricaoRepository;
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
        String nomeNormalizado = TextoUtils.paraBanco(dto.getNome());
        Optional<Remedio> remedioExistente = repository.findByNome(nomeNormalizado);

        if (remedioExistente.isPresent() && remedioExistente.get().getStatus() == Status.ATIVO) {
            throw new DuplicateResourceException("Já existe um remédio ativo com esse nome");
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

        String nomeNormalizado = TextoUtils.paraBanco(dto.getNome());
        if (!remedio.getNome().equals(nomeNormalizado) && repository.existsByNome(nomeNormalizado)) {
            throw new DuplicateResourceException("Nome já está em uso");
        }

        RemedioMapper.updateEntity(remedio, dto);
        return RemedioMapper.toDTO(repository.save(remedio));
    }

    @Transactional
    public void inativar(int id) {
        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remédio", (long) id));
        for (Prescricao prescricao : prescricaoRepository.findByRemedioIdAndStatus(id, Status.ATIVO)) {
            prescricao.setStatus(Status.INATIVO);
        }

        remedio.setStatus(Status.INATIVO);
        repository.save(remedio);
    }
}
