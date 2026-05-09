package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.CuidadorDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CuidadorMapper;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.InstituicaoRepository;

@Service
public class CuidadorService {

    private final CuidadorRepository repository;
    private final InstituicaoRepository instituicaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SenhaService senhaService;

    public CuidadorService(
            CuidadorRepository repository,
            InstituicaoRepository instituicaoRepository,
            PasswordEncoder passwordEncoder,
            SenhaService senhaService) {
        this.repository = repository;
        this.instituicaoRepository = instituicaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.senhaService = senhaService;
    }

    public Page<CuidadorDTO> listarAtivos(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable).map(CuidadorMapper::toDTO);
    }

    public Page<CuidadorDTO> listarAtivosPorInstituicao(Integer instituicaoId, String cpf, Pageable pageable) {
        String cpfLimpo = limparDocumento(cpf);

        if (cpfLimpo == null) {
            return repository.findByStatusAndInstituicaoId(Status.ATIVO, instituicaoId, pageable)
                    .map(CuidadorMapper::toDTO);
        }

        return repository.findByStatusAndInstituicaoIdAndCpf(Status.ATIVO, instituicaoId, cpfLimpo, pageable)
                .map(CuidadorMapper::toDTO);
    }

    public CuidadorDTO buscarPorId(Integer id) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        return CuidadorMapper.toDTO(cuidador);
    }

    public CuidadorDTO criar(CuidadorDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("Já existe um cuidador com esse CPF");
        }

        if (dto.getContato() == null) {
            throw new BusinessException("O contato do cuidador deve ser informado");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));

        Cuidador cuidador = CuidadorMapper.toEntity(dto);
        senhaService.validar(dto.getSenha());
        cuidador.setSenha(passwordEncoder.encode(dto.getSenha()));
        cuidador.setInstituicao(instituicao);

        return CuidadorMapper.toDTO(repository.save(cuidador));
    }

    public CuidadorDTO atualizar(Integer id, CuidadorDTO dto) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        if (!cuidador.getCpf().equals(dto.getCpf()) && repository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já está em uso");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));

        cuidador.setNome(dto.getNome());
        cuidador.setCpf(dto.getCpf());
        cuidador.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validar(dto.getSenha());
            cuidador.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        cuidador.setInstituicao(instituicao);
        cuidador.setData_atualizacao(LocalDateTime.now());

        if (dto.getContato() != null) {
            Contato contato = cuidador.getContato();
            if (contato == null) {
                contato = new Contato();
                contato.setCuidador(cuidador);
                cuidador.setContato(contato);
            }
            contato.setDdd(dto.getContato().getDdd());
            contato.setTelefone(dto.getContato().getTelefone());
        }

        return CuidadorMapper.toDTO(repository.save(cuidador));
    }

    public CuidadorDTO reativar(Integer id, CuidadorDTO dto) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        aplicarCamposEnviados(cuidador, dto);
        cuidador.setStatus(Status.ATIVO);
        cuidador.setData_atualizacao(LocalDateTime.now());

        return CuidadorMapper.toDTO(repository.save(cuidador));
    }

    public void inativar(Integer id) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        cuidador.setStatus(Status.INATIVO);
        cuidador.setData_atualizacao(LocalDateTime.now());
        repository.save(cuidador);
    }

    private void aplicarCamposEnviados(Cuidador cuidador, CuidadorDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getNome() != null) {
            cuidador.setNome(dto.getNome());
        }

        if (dto.getCpf() != null && !dto.getCpf().equals(cuidador.getCpf())) {
            if (repository.existsByCpf(dto.getCpf())) {
                throw new BusinessException("CPF já está em uso");
            }
            cuidador.setCpf(dto.getCpf());
        }

        if (dto.getEmail() != null) {
            cuidador.setEmail(dto.getEmail());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validar(dto.getSenha());
            cuidador.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        if (dto.getInstituicaoId() != null) {
            Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));
            cuidador.setInstituicao(instituicao);
        }

        if (dto.getContato() != null) {
            Contato contato = cuidador.getContato();
            if (contato == null) {
                contato = new Contato();
                contato.setCuidador(cuidador);
                cuidador.setContato(contato);
            }

            if (dto.getContato().getDdd() != null) {
                contato.setDdd(dto.getContato().getDdd());
            }
            if (dto.getContato().getTelefone() != null) {
                contato.setTelefone(dto.getContato().getTelefone());
            }
        }
    }

    private String limparDocumento(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.replaceAll("\\D", "");
    }
}
