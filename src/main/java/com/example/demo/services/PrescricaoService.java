package com.example.demo.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.Status;
import com.example.demo.mappers.PrescricaoMapper;
import com.example.demo.repository.PrescricaoRepository;

@Service
public class PrescricaoService {

    private final PrescricaoRepository repository;

    public PrescricaoService(PrescricaoRepository repository) {
        this.repository = repository;
    }

    public Page<PrescricaoDTO> listarAtivas(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(PrescricaoMapper::toDTO);
    }

    public PrescricaoDTO buscarPorId(Integer id) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescrição não encontrada"));

        return PrescricaoMapper.toDTO(prescricao);
    }

    public PrescricaoDTO criar(PrescricaoDTO dto) {
        if (repository.existsById(dto.getId())) {
            throw new RuntimeException("Já existe uma prescrição com esse ID");
        }

        Prescricao prescricao = PrescricaoMapper.toEntity(dto);
        Prescricao salva = repository.save(prescricao);

        return PrescricaoMapper.toDTO(salva);
    }

    public PrescricaoDTO atualizar(int id, PrescricaoDTO dto) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescrição não encontrada"));

        if (!prescricao.getRemedio().equals(dto.getRemedio().getId())
                && repository.existsById(dto.getRemedio().getId())) {
            throw new RuntimeException("Remédio já está em uso");
        }

        Prescricao atualizado = repository.save(prescricao);
        return PrescricaoMapper.toDTO(atualizado);
    }


    public void inativar(Integer id) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescrição não encontrada"));

        prescricao.setStatus(Status.INATIVO);

        repository.save(prescricao);
    }
}