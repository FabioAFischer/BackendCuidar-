package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.IdosoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.mappers.ContatoMapper;
import com.example.demo.mappers.IdosoMapper;
import com.example.demo.repository.ContatoRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;

@Service
public class IdosoService {

    private final IdosoRepository repository;
    private final InstituicaoRepository instituicaoRepository;
    private final ContatoRepository contatoRepository;

    public IdosoService(IdosoRepository repository, InstituicaoRepository instituicaoRepository, ContatoRepository contatoRepository) {
        this.repository = repository;
        this.instituicaoRepository = instituicaoRepository;
        this.contatoRepository = contatoRepository;
    }

    public Page<IdosoDTO> listarAtivos(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(IdosoMapper::toDTO);
    }

    public IdosoDTO buscarPorId(Long id) {
        Idoso idoso = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idoso não encontrado"));

        return IdosoMapper.toDTO(idoso);
    }

    public IdosoDTO criar(IdosoDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("Já existe um idoso com esse CPF");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        Contato contato = null;
        ContatoDTO contatoDTO = dto.getContato();

        if (contatoDTO != null) {
            if (contatoDTO.getDdd() == null || contatoDTO.getTelefone() == null) {
                throw new RuntimeException("Dados de contato incompletos");
            }

            contato = ContatoMapper.toEntity(contatoDTO, null, java.util.List.of());
            contato = contatoRepository.save(contato);
        } else if (dto.getContatoId() != null) {
            contato = contatoRepository.findById(dto.getContatoId())
                    .orElseThrow(() -> new RuntimeException("Contato informado não encontrado"));
        } else {
            throw new RuntimeException("Contato é obrigatório");
        }

        Idoso idoso = IdosoMapper.toEntity(dto);
        idoso.setInstituicao(instituicao);
        idoso.setContato(contato);

        Idoso salvo = repository.save(idoso);
        return IdosoMapper.toDTO(salvo);
    }

    public IdosoDTO atualizar(Long id, IdosoDTO dto) {
        Idoso idoso = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idoso não encontrado"));

        if (!idoso.getCpf().equals(dto.getCpf())
                && repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já está em uso");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        ContatoDTO contatoDTO = dto.getContato();
        Contato contato = null;

        if (contatoDTO != null) {
            if (contatoDTO.getId() != null) {
                contato = contatoRepository.findById(contatoDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Contato para atualização não encontrado"));
                ContatoMapper.atualizarContato(contato, contatoDTO, null, null);
                contato = contatoRepository.save(contato);
            } else {
                if (contatoDTO.getDdd() == null || contatoDTO.getTelefone() == null) {
                    throw new RuntimeException("Dados de contato incompletos");
                }
                contato = ContatoMapper.toEntity(contatoDTO, null, java.util.List.of());
                contato = contatoRepository.save(contato);
            }
        } else if (dto.getContatoId() != null) {
            contato = contatoRepository.findById(dto.getContatoId())
                    .orElseThrow(() -> new RuntimeException("Contato informado não encontrado"));
        }

    IdosoMapper.atualizarIdoso(idoso, dto, instituicao);
        if (contato != null) {
            idoso.setContato(contato);
        }

        idoso.setData_atualizacao(LocalDateTime.now());

        Idoso atualizado = repository.save(idoso);
        return IdosoMapper.toDTO(atualizado);
    }

    public void inativar(Long id) {
        Idoso idoso = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idoso não encontrado"));

        idoso.setStatus(Status.INATIVO);
        idoso.setData_atualizacao(LocalDateTime.now());

        repository.save(idoso);
    }
}
