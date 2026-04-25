package com.example.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.AlertaDTO;
import com.example.demo.entity.Alerta;
import com.example.demo.entity.Idoso;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.mappers.AlertaMapper;
import com.example.demo.repository.AlertaRepository;
import com.example.demo.repository.IdosoRepository;

@Service
public class AlertaService {

    private final AlertaRepository repository;
    private final IdosoRepository idosoRepository;

    public AlertaService(AlertaRepository repository, IdosoRepository idosoRepository) {
        this.repository = repository;
        this.idosoRepository = idosoRepository;
    }

    public Page<AlertaDTO> listarAtivos(Pageable pageable) {
        return repository.findByStatusAlertas(StatusAlertas.AGENDADO, pageable)
                .map(AlertaMapper::toDTO);
    }

    public AlertaDTO buscarPorId(Integer id) {
        Alerta alerta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));

        return AlertaMapper.toDTO(alerta);
    }

    public AlertaDTO criar(AlertaDTO dto) {
        Idoso idoso = idosoRepository.findById(dto.getIdosoId())
                .orElseThrow(() -> new RuntimeException("Idoso não encontrado"));

        Alerta alerta = AlertaMapper.toEntity(dto, idoso);
        Alerta salvo = repository.save(alerta);

        return AlertaMapper.toDTO(salvo);
    }

    public AlertaDTO atualizar(Integer id, AlertaDTO dto) {
        Alerta alerta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));

        Idoso idoso = idosoRepository.findById(dto.getIdosoId())
                .orElseThrow(() -> new RuntimeException("Idoso não encontrado"));

        AlertaMapper.updateEntity(alerta, dto, idoso);

        Alerta atualizado = repository.save(alerta);
        return AlertaMapper.toDTO(atualizado);
    }

    public void inativar(Integer id) {
        Alerta alerta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado"));

        AlertaMapper.inativarEntity(alerta);

        repository.save(alerta);
    }
}
