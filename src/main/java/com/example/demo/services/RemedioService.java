// RemedioService.java
package com.example.demo.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.RemedioMapper;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;
import com.example.demo.utils.TextoUtils;

@Service
public class RemedioService {

    private final RemedioRepository repository;
    private final PrescricaoRepository prescricaoRepository;
    private final CuidadorRepository cuidadorRepository;

    public RemedioService(
            RemedioRepository repository,
            PrescricaoRepository prescricaoRepository,
            CuidadorRepository cuidadorRepository) {
        this.repository = repository;
        this.prescricaoRepository = prescricaoRepository;
        this.cuidadorRepository = cuidadorRepository;
    }

    public Page<RemedioDTO> listarRemediosAtivos(Integer cuidadorId, Pageable pageable) {
        return repository.findByCuidadorIdAndStatus(cuidadorId, Status.ATIVO, pageable).map(RemedioMapper::converterRemedioParaDTO);
    }

    public RemedioDTO buscarRemedioPorId(int id, Integer cuidadorId) {
        Remedio remedio = repository.findByIdAndCuidadorId(id, cuidadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Remedio", (long) id));
        return RemedioMapper.converterRemedioParaDTO(remedio);
    }

    public RemedioDTO criarRemedio(RemedioDTO dto, Integer cuidadorId) {
        String nomeNormalizado = TextoUtils.normalizarTextoParaBanco(dto.getNome());
        Optional<Remedio> remedioExistente = repository.findByNomeAndCuidadorId(nomeNormalizado, cuidadorId);

        if (remedioExistente.isPresent() && remedioExistente.get().getStatus() == Status.ATIVO) {
            throw new DuplicateResourceException("Ja existe um remedio ativo com esse nome");
        }

        if (remedioExistente.isPresent()) {
            Remedio remedio = remedioExistente.get();
            RemedioMapper.atualizarRemedioComDTO(remedio, dto);
            remedio.setStatus(Status.ATIVO);
            return RemedioMapper.converterRemedioParaDTO(repository.save(remedio));
        }

        Cuidador cuidador = buscarCuidadorPorId(cuidadorId);
        Remedio remedio = RemedioMapper.converterDTOParaRemedio(dto);
        remedio.setCuidador(cuidador);
        return RemedioMapper.converterRemedioParaDTO(repository.save(remedio));
    }

    public RemedioDTO atualizarRemedio(int id, RemedioDTO dto, Integer cuidadorId) {
        Remedio remedio = repository.findByIdAndCuidadorId(id, cuidadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Remedio", (long) id));

        String nomeNormalizado = TextoUtils.normalizarTextoParaBanco(dto.getNome());
        if (!remedio.getNome().equals(nomeNormalizado)
                && repository.existsByNomeAndCuidadorId(nomeNormalizado, cuidadorId)) {
            throw new DuplicateResourceException("Nome ja esta em uso");
        }

        RemedioMapper.atualizarRemedioComDTO(remedio, dto);
        return RemedioMapper.converterRemedioParaDTO(repository.save(remedio));
    }

    @Transactional
    public void inativarRemedio(int id, Integer cuidadorId) {
        Remedio remedio = repository.findByIdAndCuidadorId(id, cuidadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Remedio", (long) id));
        for (Prescricao prescricao : prescricaoRepository.findByRemedioIdAndStatus(id, Status.ATIVO)) {
            prescricao.setStatus(Status.INATIVO);
        }

        remedio.setStatus(Status.INATIVO);
        repository.save(remedio);
    }

    private Cuidador buscarCuidadorPorId(Integer cuidadorId) {
        return cuidadorRepository.findById(cuidadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", cuidadorId.longValue()));
    }
}
