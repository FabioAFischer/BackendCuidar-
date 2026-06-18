package com.example.demo.services;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Usuario;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.InstituicaoRepository;

@Service
public class EmailValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    private final AdministradorRepository administradorRepository;
    private final CuidadorRepository cuidadorRepository;
    private final InstituicaoRepository instituicaoRepository;

    public EmailValidationService(
            AdministradorRepository administradorRepository,
            CuidadorRepository cuidadorRepository,
            InstituicaoRepository instituicaoRepository) {
        this.administradorRepository = administradorRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    public String validarParaCriacao(String email) {
        String emailNormalizado = validarFormato(email);
        validarDisponivel(emailNormalizado, null);
        return emailNormalizado;
    }

    public String validarParaAtualizacao(String email, Integer usuarioIdAtual) {
        String emailNormalizado = validarFormato(email);
        validarDisponivel(emailNormalizado, usuarioIdAtual);
        return emailNormalizado;
    }

    private String validarFormato(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("Email deve ser informado");
        }

        String emailNormalizado = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new InvalidRequestException("Email invalido");
        }

        return emailNormalizado;
    }

    private void validarDisponivel(String email, Integer usuarioIdAtual) {
        if (emailPertenceAOutroUsuario(cuidadorRepository.findByEmail(email), usuarioIdAtual)
                || emailPertenceAOutroUsuario(instituicaoRepository.findByEmail(email), usuarioIdAtual)
                || emailPertenceAOutroUsuario(administradorRepository.findByEmail(email), usuarioIdAtual)) {
            throw new DuplicateResourceException("Email ja esta em uso");
        }
    }

    private boolean emailPertenceAOutroUsuario(Optional<? extends Usuario> usuario, Integer usuarioIdAtual) {
        return usuario.isPresent()
                && (usuarioIdAtual == null || usuario.get().getId() != usuarioIdAtual);
    }
}
