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
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CuidadorMapper;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;
import com.example.demo.utils.CpfUtils;
import com.example.demo.utils.TextoUtils;

@Service
public class CuidadorService {

    private final CuidadorRepository repository;
    private final IdosoRepository idosoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SenhaService senhaService;
    private final EmailValidationService emailValidationService;

    public CuidadorService(
            CuidadorRepository repository,
            IdosoRepository idosoRepository,
            InstituicaoRepository instituicaoRepository,
            PasswordEncoder passwordEncoder,
            SenhaService senhaService,
            EmailValidationService emailValidationService) {
        this.repository = repository;
        this.idosoRepository = idosoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.senhaService = senhaService;
        this.emailValidationService = emailValidationService;
    }

    public Page<CuidadorDTO> listarCuidadoresAtivos(Pageable pageable) {
        return repository.findByStatus(Status.ATIVO, pageable).map(CuidadorMapper::converterCuidadorParaDTO);
    }

    public Page<CuidadorDTO> listarCuidadoresAtivosPorInstituicao(Integer instituicaoId, String cpf, Pageable pageable) {
        String cpfLimpo = CpfUtils.normalizarCpf(cpf);

        if (cpfLimpo == null) {
            return repository.findByStatusAndInstituicaoId(Status.ATIVO, instituicaoId, pageable)
                    .map(CuidadorMapper::converterCuidadorParaDTO);
        }

        return repository.findByStatusAndInstituicaoIdAndCpf(Status.ATIVO, instituicaoId, cpfLimpo, pageable)
                .map(CuidadorMapper::converterCuidadorParaDTO);
    }

    public CuidadorDTO buscarCuidadorPorId(Integer id) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        return CuidadorMapper.converterCuidadorParaDTO(cuidador);
    }

    public CuidadorDTO cadastrarCuidador(CuidadorDTO dto) {
        String cpfLimpo = CpfUtils.normalizarCpf(dto.getCpf());
        String email = emailValidationService.validarEmailParaCriacao(dto.getEmail());
        CpfUtils.validarCpfDisponivel(cpfLimpo, repository::existsByCpf, idosoRepository::existsByCpf);

        if (dto.getContato() == null) {
            throw new InvalidRequestException("O contato do cuidador deve ser informado");
        }

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));

        Cuidador cuidador = CuidadorMapper.converterDTOParaCuidador(dto);
        cuidador.setEmail(email);
        senhaService.validarSenha(dto.getSenha());
        cuidador.setSenha(passwordEncoder.encode(dto.getSenha()));
        cuidador.setInstituicao(instituicao);

        return CuidadorMapper.converterCuidadorParaDTO(repository.save(cuidador));
    }

    public CuidadorDTO atualizarCuidador(Integer id, CuidadorDTO dto) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        String cpfLimpo = CpfUtils.normalizarCpf(dto.getCpf());
        if (!cuidador.getCpf().equals(cpfLimpo)) {
            CpfUtils.validarCpfDisponivel(cpfLimpo, repository::existsByCpf, idosoRepository::existsByCpf);
        }

        String email = emailValidationService.validarEmailParaAtualizacao(dto.getEmail(), cuidador.getId());

        Instituicao instituicao = instituicaoRepository.findById(dto.getInstituicaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", dto.getInstituicaoId().longValue()));

        cuidador.setNome(TextoUtils.normalizarTextoParaBanco(dto.getNome()));
        cuidador.setCpf(cpfLimpo);
        cuidador.setEmail(email);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validarSenha(dto.getSenha());
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
            contato.setDdd(TextoUtils.normalizarNumero(dto.getContato().getDdd()));
            contato.setTelefone(TextoUtils.normalizarNumero(dto.getContato().getTelefone()));
        }

        return CuidadorMapper.converterCuidadorParaDTO(repository.save(cuidador));
    }

    public CuidadorDTO reativarCuidador(Integer id, CuidadorDTO dto) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        aplicarCamposEnviadosAoCuidador(cuidador, dto);
        cuidador.setStatus(Status.ATIVO);
        cuidador.setData_atualizacao(LocalDateTime.now());

        return CuidadorMapper.converterCuidadorParaDTO(repository.save(cuidador));
    }

    public void inativarCuidador(Integer id) {
        Cuidador cuidador = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id.longValue()));

        cuidador.setStatus(Status.INATIVO);
        cuidador.setData_atualizacao(LocalDateTime.now());
        repository.save(cuidador);
    }

    private void aplicarCamposEnviadosAoCuidador(Cuidador cuidador, CuidadorDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getNome() != null) {
            cuidador.setNome(TextoUtils.normalizarTextoParaBanco(dto.getNome()));
        }

        String cpfLimpo = CpfUtils.normalizarCpf(dto.getCpf());
        if (cpfLimpo != null && !cpfLimpo.equals(cuidador.getCpf())) {
            CpfUtils.validarCpfDisponivel(cpfLimpo, repository::existsByCpf, idosoRepository::existsByCpf);
            cuidador.setCpf(cpfLimpo);
        }

        if (dto.getEmail() != null) {
            cuidador.setEmail(emailValidationService.validarEmailParaAtualizacao(dto.getEmail(), cuidador.getId()));
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            senhaService.validarSenha(dto.getSenha());
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
                contato.setDdd(TextoUtils.normalizarNumero(dto.getContato().getDdd()));
            }
            if (dto.getContato().getTelefone() != null) {
                contato.setTelefone(TextoUtils.normalizarNumero(dto.getContato().getTelefone()));
            }
        }
    }

}
