package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.VinculoDTO;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Vinculo;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.VinculoMapper;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.VinculoRepository;

@Service
public class VinculoService {

    private final VinculoRepository repository;
    private final IdosoRepository idosoRepository;
    private final CuidadorRepository cuidadorRepository;

    public VinculoService(
            VinculoRepository repository,
            IdosoRepository idosoRepository,
            CuidadorRepository cuidadorRepository) {
        this.repository = repository;
        this.idosoRepository = idosoRepository;
        this.cuidadorRepository = cuidadorRepository;
    }

    public Page<VinculoDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(VinculoMapper::toDTO);
    }

    public Page<VinculoDTO> listarPorIdoso(Integer idosoId, Pageable pageable) {
        return repository.findByIdosoId(idosoId, pageable).map(VinculoMapper::toDTO);
    }

    public Page<VinculoDTO> listarPorCuidador(Integer cuidadorId, Pageable pageable) {
        return repository.findByCuidadorId(cuidadorId, pageable).map(VinculoMapper::toDTO);
    }

    public VinculoDTO buscarPorId(Integer id) {
        Vinculo vinculo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo", id.longValue()));
        return VinculoMapper.toDTO(vinculo);
    }

    public VinculoDTO criar(VinculoDTO dto) {
        if (dto.getIdosoId() == null || dto.getCuidadorId() == null) {
            throw new BusinessException("Idoso e Cuidador são obrigatórios para criar um vínculo");
        }

        if (repository.existsByIdosoIdAndCuidadorId(dto.getIdosoId(), dto.getCuidadorId())) {
            throw new BusinessException("Já existe um vínculo entre este idoso e este cuidador");
        }

        Idoso idoso = idosoRepository.findById(dto.getIdosoId())
                .orElseThrow(() -> new ResourceNotFoundException("Idoso", dto.getIdosoId().longValue()));

        Cuidador cuidador = cuidadorRepository.findById(dto.getCuidadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", dto.getCuidadorId().longValue()));

        Vinculo vinculo = VinculoMapper.toEntity(dto, idoso, cuidador);
        return VinculoMapper.toDTO(repository.save(vinculo));
    }

    public void deletar(Integer id) {
        Vinculo vinculo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo", id.longValue()));
        repository.delete(vinculo);
    }
}