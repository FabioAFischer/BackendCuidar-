package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.mappers.PrescricaoMapper;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;

@Service
public class PrescricaoService {

    private final PrescricaoRepository repository;
    private final RemedioRepository remedioRepository;
    private final IdosoRepository idosoRepository;

    public PrescricaoService(PrescricaoRepository repository, RemedioRepository remedioRepository, IdosoRepository idosoRepository) {
        this.repository = repository;
        this.remedioRepository = remedioRepository;
        this.idosoRepository = idosoRepository;
    }

    @Transactional(readOnly = true)
    public Page<PrescricaoDTO> listarAtivas(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(PrescricaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PrescricaoDTO> listarPorIdoso(Integer idosoId, Pageable pageable) {
        return repository.findByIdoso_Id(idosoId, pageable)
                .map(PrescricaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PrescricaoDTO> listarPorRemedio(Integer remedioId, Pageable pageable) {
        return repository.findByRemedio_Id(remedioId, pageable)
                .map(PrescricaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PrescricaoDTO buscarPorId(Integer id) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescricao nao encontrada"));

        return PrescricaoMapper.toDTO(prescricao);
    }

    @Transactional
    public PrescricaoDTO criar(PrescricaoDTO dto) {
        Remedio remedio = buscarRemedio(dto.getRemedioId());
        Idoso idoso = buscarIdoso(dto.getIdosoId());

        Prescricao prescricao = PrescricaoMapper.toEntity(dto, remedio, idoso);
        Prescricao salva = repository.save(prescricao);

        return PrescricaoMapper.toDTO(salva);
    }

    @Transactional
    public PrescricaoDTO atualizar(Integer id, PrescricaoDTO dto) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescricao nao encontrada"));

        Remedio remedio = buscarRemedio(dto.getRemedioId());
        Idoso idoso = buscarIdoso(dto.getIdosoId());

        PrescricaoMapper.updateEntity(prescricao, dto, remedio, idoso);

        Prescricao atualizado = repository.save(prescricao);
        return PrescricaoMapper.toDTO(atualizado);
    }

    @Transactional
    public void inativar(Integer id) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescricao nao encontrada"));

        prescricao.setStatus(Status.INATIVO);
        repository.save(prescricao);
    }

    private Remedio buscarRemedio(Integer remedioId) {
        if (remedioId == null) {
            throw new RuntimeException("Remedio e obrigatorio");
        }

        return remedioRepository.findById(remedioId)
                .orElseThrow(() -> new RuntimeException("Remedio nao encontrado"));
    }

    private Idoso buscarIdoso(Integer idosoId) {
        if (idosoId == null) {
            throw new RuntimeException("Idoso e obrigatorio");
        }

        return idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RuntimeException("Idoso nao encontrado"));
    }
}
