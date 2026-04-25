package com.example.demo.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.AdministradorLoginDTO;
import com.example.demo.dtos.LoginRequestDTO;
import com.example.demo.dtos.LoginResponseDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.enums.Status;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.security.AuthException;
import com.example.demo.security.JwtService;

@Service
public class AuthService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO loginAdministrador(LoginRequestDTO dto) {
        if (dto == null || dto.getEmail() == null || dto.getEmail().isBlank()
                || dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new AuthException("Credenciais inválidas", HttpStatus.BAD_REQUEST);
        }

        Administrador administrador = administradorRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("Administrador não encontrado", HttpStatus.NOT_FOUND));

        if (administrador.getStatus() == Status.INATIVO) {
            throw new AuthException("Usuário inativo", HttpStatus.FORBIDDEN);
        }

        if (!senhaValida(dto.getSenha(), administrador.getSenha())) {
            throw new AuthException("Senha inválida", HttpStatus.UNAUTHORIZED);
        }

        atualizarSenhaLegadaSeNecessario(administrador, dto.getSenha());

        String token = jwtService.gerarTokenAdministrador(administrador);
        AdministradorLoginDTO administradorDTO = new AdministradorLoginDTO(
                administrador.getId(),
                administrador.getNome(),
                administrador.getEmail());

        return new LoginResponseDTO(token, "Bearer", administradorDTO);
    }

    private boolean senhaValida(String senhaInformada, String senhaSalva) {
        if (senhaSalva == null || senhaSalva.isBlank()) {
            return false;
        }

        if (senhaEhBCrypt(senhaSalva)) {
            return passwordEncoder.matches(senhaInformada, senhaSalva);
        }

        return senhaInformada.equals(senhaSalva);
    }

    private void atualizarSenhaLegadaSeNecessario(Administrador administrador, String senhaInformada) {
        if (!senhaEhBCrypt(administrador.getSenha())) {
            administrador.setSenha(passwordEncoder.encode(senhaInformada));
            administradorRepository.save(administrador);
        }
    }

    private boolean senhaEhBCrypt(String senha) {
        return senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$");
    }
}
