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
        return repository.findByStatus(Status.ATIVO, pageable)
                .map(CuidadorMapper::toDTO);
    }

    public CuidadorDTO buscarPorId(Integer id) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));

        return CuidadorMapper.toDTO(cuidador);
    }

    public CuidadorDTO criar(CuidadorDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("Já existe um cuidador com esse CPF");
        }

        if (repository.existsByLogin(dto.getLogin())) {
            throw new RuntimeException("Já existe um cuidador com esse login");
        }

        if (dto.getContato() == null) {
            throw new RuntimeException("O contato do cuidador deve ser informado");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        Cuidador cuidador = CuidadorMapper.toEntity(dto);
        senhaService.validar(dto.getSenha());
        cuidador.setSenha(passwordEncoder.encode(dto.getSenha()));
        cuidador.setInstituicao(instituicao);

        Cuidador salvo = repository.save(cuidador);
        return CuidadorMapper.toDTO(salvo);
    }

    public CuidadorDTO atualizar(Integer id, CuidadorDTO dto) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));

        if (!cuidador.getCpf().equals(dto.getCpf())
                && repository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já está em uso");
        }

        if (!cuidador.getLogin().equals(dto.getLogin())
                && repository.existsByLogin(dto.getLogin())) {
            throw new RuntimeException("Login já está em uso");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        cuidador.setNome(dto.getNome());
        cuidador.setCpf(dto.getCpf());
        cuidador.setLogin(dto.getLogin());
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

        Cuidador atualizado = repository.save(cuidador);
        return CuidadorMapper.toDTO(atualizado);
    }

    public void inativar(Integer id) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));

        cuidador.setStatus(Status.INATIVO);
        cuidador.setData_atualizacao(LocalDateTime.now());

        repository.save(cuidador);
    }
}
