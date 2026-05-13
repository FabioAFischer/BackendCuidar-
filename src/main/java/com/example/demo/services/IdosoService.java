package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.IdosoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
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
        return repository.findByStatus(Status.ATIVO, pageable).map(IdosoMapper::toDTO);
    }

    public IdosoDTO buscarPorId(Integer id) {
        Idoso idoso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idoso", id.longValue()));

        return IdosoMapper.toDTO(idoso);
    }

    public IdosoDTO buscarPorCpf(String cpf) {
        Idoso idoso = buscarEntidadePorCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Idoso não encontrado com CPF informado"));

        return IdosoMapper.toDTO(idoso);
    }

    public IdosoDTO criar(IdosoDTO dto) {
        Optional<Idoso> idosoExistente = buscarEntidadePorCpf(dto.getCpf());

        if (idosoExistente.isPresent() && idosoExistente.get().getStatus() == Status.ATIVO) {
            throw new DuplicateResourceException("Já existe um idoso ativo com esse CPF");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));

        Contato contato = resolverContato(dto);

        if (idosoExistente.isPresent()) {
            Idoso idoso = idosoExistente.get();
            IdosoMapper.atualizarIdoso(idoso, dto, instituicao);
            idoso.setContato(contato);
            idoso.setStatus(Status.ATIVO);
            idoso.setData_atualizacao(LocalDateTime.now());
            return IdosoMapper.toDTO(repository.save(idoso));
        }

        Idoso idoso = IdosoMapper.toEntity(dto);
        idoso.setInstituicao(instituicao);
        idoso.setContato(contato);

        return IdosoMapper.toDTO(repository.save(idoso));
    }

    public IdosoDTO atualizar(Integer id, IdosoDTO dto) {
        Idoso idoso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idoso", id.longValue()));

        if (!idoso.getCpf().equals(dto.getCpf()) && repository.existsByCpf(dto.getCpf())) {
            throw new DuplicateResourceException("CPF já está em uso");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));

        Contato contato = null;
        ContatoDTO contatoDTO = dto.getContato();

        if (contatoDTO != null) {
            if (contatoDTO.getId() != null) {
                contato = contatoRepository.findById(contatoDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Contato", contatoDTO.getId().longValue()));
                ContatoMapper.atualizarContato(contato, contatoDTO, null, null);
                contato = contatoRepository.save(contato);
            } else {
                if (contatoDTO.getDdd() == null || contatoDTO.getTelefone() == null) {
                    throw new BusinessException("Dados de contato incompletos");
                }
                contato = ContatoMapper.toEntity(contatoDTO, null, java.util.List.of());
                contato = contatoRepository.save(contato);
            }
        } else if (dto.getContatoId() != null) {
            contato = contatoRepository.findById(dto.getContatoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contato", dto.getContatoId().longValue()));
        }

        IdosoMapper.atualizarIdoso(idoso, dto, instituicao);
        if (contato != null) idoso.setContato(contato);
        idoso.setData_atualizacao(LocalDateTime.now());

        return IdosoMapper.toDTO(repository.save(idoso));
    }

    public void inativar(Integer id) {
        Idoso idoso = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idoso", id.longValue()));

        idoso.setStatus(Status.INATIVO);
        idoso.setData_atualizacao(LocalDateTime.now());
        repository.save(idoso);
    }

    private Contato resolverContato(IdosoDTO dto) {
        ContatoDTO contatoDTO = dto.getContato();

        if (contatoDTO != null) {
            if (contatoDTO.getDdd() == null || contatoDTO.getTelefone() == null) {
                throw new BusinessException("Dados de contato incompletos");
            }
            Contato contato = ContatoMapper.toEntity(contatoDTO, null, java.util.List.of());
            return contatoRepository.save(contato);
        }

        if (dto.getContatoId() != null) {
            return contatoRepository.findById(dto.getContatoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contato", dto.getContatoId().longValue()));
        }

        throw new BusinessException("Contato é obrigatório");
    }

    private Optional<Idoso> buscarEntidadePorCpf(String cpf) {
        String cpfLimpo = limparDocumento(cpf);
        return cpfLimpo == null ? Optional.empty() : repository.findByCpf(cpfLimpo);
    }

    private String limparDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }
}
