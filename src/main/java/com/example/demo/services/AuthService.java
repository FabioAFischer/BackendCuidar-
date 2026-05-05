package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Administrador;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Instituicao;
import com.example.demo.entity.Usuario;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.InstituicaoRepository;

@Service
public class AuthService {

    private final AdministradorRepository administradorRepository;
    private final CuidadorRepository cuidadorRepository;
    private final InstituicaoRepository instituicaoRepository;

    public AuthService(
            AdministradorRepository administradorRepository,
            CuidadorRepository cuidadorRepository,
            InstituicaoRepository instituicaoRepository) {
        this.administradorRepository = administradorRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    public Map<String, Object> login(Map<String, String> dados) {
        if (dados == null) {
            throw new RuntimeException("Dados de login nao informados");
        }

        String identificador = primeiroValor(dados, "identificador", "cpfCnpj", "cpf", "cnpj", "login");
        String senha = primeiroValor(dados, "senha", "password");
        Perfil perfil = parsePerfil(dados.get("perfil"));

        if (identificador == null || identificador.isBlank()) {
            throw new RuntimeException("Informe CPF, CNPJ ou login");
        }

        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Informe a senha");
        }

        Usuario usuario = buscarUsuario(perfil, identificador);
        String senhaSalva = senhaDoUsuario(usuario);

        if (!senha.equals(senhaSalva)) {
            throw new RuntimeException("Credenciais invalidas");
        }

        if (usuario.getStatus() != Status.ATIVO) {
            throw new RuntimeException("Usuario inativo");
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getNome());
        resposta.put("perfil", usuario.getPerfil());
        resposta.put("autenticado", true);

        return resposta;
    }

    private Usuario buscarUsuario(Perfil perfil, String identificador) {
        String documento = limparDocumento(identificador);

        return switch (perfil) {
            case ADMINISTRADOR -> administradorRepository.findByCpf(documento)
                    .orElseThrow(() -> new RuntimeException("Credenciais invalidas"));
            case CUIDADOR -> cuidadorRepository.findByCpf(documento)
                    .or(() -> cuidadorRepository.findByLogin(identificador))
                    .orElseThrow(() -> new RuntimeException("Credenciais invalidas"));
            case INSTITUICAO -> instituicaoRepository.findByCnpj(documento)
                    .orElseThrow(() -> new RuntimeException("Credenciais invalidas"));
            default -> throw new RuntimeException("Perfil nao permitido para login");
        };
    }

    private String senhaDoUsuario(Usuario usuario) {
        if (usuario instanceof Administrador administrador) {
            return administrador.getSenha();
        }

        if (usuario instanceof Cuidador cuidador) {
            return cuidador.getSenha();
        }

        if (usuario instanceof Instituicao instituicao) {
            return instituicao.getSenha();
        }

        throw new RuntimeException("Perfil nao permitido para login");
    }

    private Perfil parsePerfil(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            throw new RuntimeException("Informe o perfil");
        }

        try {
            return Perfil.valueOf(perfil.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Perfil invalido");
        }
    }

    private String primeiroValor(Map<String, String> dados, String... chaves) {
        for (String chave : chaves) {
            String valor = dados.get(chave);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }

        return null;
    }

    private String limparDocumento(String valor) {
        return valor.replaceAll("\\D", "");
    }
}
