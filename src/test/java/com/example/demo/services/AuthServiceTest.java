package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.Administrador;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.BusinessException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.InstituicaoRepository;
import com.example.demo.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TwoFactorService twoFactorService;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private AuthService service;

    @Test
    void deveLogarAdministradorSemMfa() {
        Administrador administrador = administrador();

        when(administradorRepository.findByCpf("12345678901")).thenReturn(Optional.of(administrador));
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);
        when(jwtService.gerarToken(administrador)).thenReturn("token-admin");

        Map<String, Object> resposta = service.login(dadosLogin("123.456.789-01", "senha", "ADMINISTRADOR"));

        assertEquals(1, resposta.get("id"));
        assertEquals("Admin", resposta.get("nome"));
        assertEquals(Perfil.ADMINISTRADOR, resposta.get("perfil"));
        assertEquals("token-admin", resposta.get("token"));
        assertEquals("Bearer", resposta.get("tipo"));
        assertEquals(true, resposta.get("autenticado"));
    }

    @Test
    void deveExigirMfaParaCuidador() {
        Cuidador cuidador = cuidador();

        when(cuidadorRepository.findByCpf("12345678901")).thenReturn(Optional.of(cuidador));
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);

        Map<String, Object> resposta = service.login(dadosLogin("12345678901", "senha", "CUIDADOR"));

        assertEquals(true, resposta.get("requer2fa"));
        assertEquals("cu***@email.com", resposta.get("email"));
        verify(twoFactorService).enviarCodigo("cuidador@email.com");
    }

    @Test
    void deveExigirMfaParaInstituicao() {
        Instituicao instituicao = instituicao();

        when(instituicaoRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(instituicao));
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);

        Map<String, Object> resposta = service.login(dadosLogin("12.345.678/0001-99", "senha", "INSTITUICAO"));

        assertEquals(true, resposta.get("requer2fa"));
        assertEquals("in***@email.com", resposta.get("email"));
        verify(twoFactorService).enviarCodigo("instituicao@email.com");
    }

    @Test
    void deveFalharLoginComSenhaIncorreta() {
        Administrador administrador = administrador();

        when(administradorRepository.findByCpf("12345678901")).thenReturn(Optional.of(administrador));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> service.login(dadosLogin("12345678901", "errada", "ADMINISTRADOR")));
    }

    @Test
    void deveFalharLoginComUsuarioInativo() {
        Administrador administrador = administrador();
        administrador.setStatus(Status.INATIVO);

        when(administradorRepository.findByCpf("12345678901")).thenReturn(Optional.of(administrador));
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);

        assertThrows(UnauthorizedException.class,
                () -> service.login(dadosLogin("12345678901", "senha", "ADMINISTRADOR")));
    }

    @Test
    void deveFalharLoginSemPerfil() {
        Map<String, String> dados = dadosLogin("12345678901", "senha", "");

        assertThrows(BusinessException.class, () -> service.login(dados));
    }

    @Test
    void deveFalharLoginComPerfilInvalido() {
        Map<String, String> dados = dadosLogin("12345678901", "senha", "INVALIDO");

        assertThrows(BusinessException.class, () -> service.login(dados));
    }

    @Test
    void deveVerificarMfaDeCuidadorEGerarToken() {
        Cuidador cuidador = cuidador();

        when(cuidadorRepository.findByEmail("cuidador@email.com")).thenReturn(Optional.of(cuidador));
        when(jwtService.gerarToken(cuidador)).thenReturn("token-cuidador");

        Map<String, Object> resposta = service.verificar2fa("cuidador@email.com", "123456", "CUIDADOR");

        verify(twoFactorService).validarCodigo("cuidador@email.com", "123456");
        assertEquals("token-cuidador", resposta.get("token"));
        assertEquals(true, resposta.get("autenticado"));
    }

    @Test
    void deveIniciarRecuperacaoDeSenhaParaCuidador() {
        Cuidador cuidador = cuidador();

        when(cuidadorRepository.findByCpf("12345678901")).thenReturn(Optional.of(cuidador));

        Map<String, Object> resposta = service.recuperarSenha("123.456.789-01");

        assertEquals("cu***@email.com", resposta.get("email"));
        verify(twoFactorService).enviarCodigo("cuidador@email.com");
    }

    @Test
    void deveTrocarSenhaDeCuidador() {
        Cuidador cuidador = cuidador();

        when(cuidadorRepository.findByEmail("cuidador@email.com")).thenReturn(Optional.of(cuidador));
        when(passwordEncoder.encode("Nova@123")).thenReturn("nova-hash");

        service.novaSenha("cuidador@email.com", "Nova@123");

        verify(senhaService).validar("Nova@123");
        verify(cuidadorRepository).save(cuidador);
        assertEquals("nova-hash", cuidador.getSenha());
    }

    @Test
    void deveVerificarCodigoDeRecuperacao() {
        Map<String, Object> resposta = service.verificarRecuperacao("cuidador@email.com", "123456");

        verify(twoFactorService).validarCodigo("cuidador@email.com", "123456");
        assertTrue((Boolean) resposta.get("valido"));
        assertEquals("cuidador@email.com", resposta.get("email"));
    }

    private Map<String, String> dadosLogin(String identificador, String senha, String perfil) {
        return Map.of(
                "identificador", identificador,
                "senha", senha,
                "perfil", perfil);
    }

    private Administrador administrador() {
        Administrador administrador = new Administrador();
        administrador.setId(1);
        administrador.setNome("Admin");
        administrador.setCpf("12345678901");
        administrador.setEmail("admin@email.com");
        administrador.setSenha("hash");
        administrador.setPerfil(Perfil.ADMINISTRADOR);
        administrador.setStatus(Status.ATIVO);
        administrador.setData_criacao(LocalDateTime.now());
        return administrador;
    }

    private Cuidador cuidador() {
        Cuidador cuidador = new Cuidador();
        cuidador.setId(2);
        cuidador.setNome("Cuidador");
        cuidador.setCpf("12345678901");
        cuidador.setEmail("cuidador@email.com");
        cuidador.setSenha("hash");
        cuidador.setPerfil(Perfil.CUIDADOR);
        cuidador.setStatus(Status.ATIVO);
        cuidador.setData_criacao(LocalDateTime.now());
        return cuidador;
    }

    private Instituicao instituicao() {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(3);
        instituicao.setNome("Instituicao");
        instituicao.setCnpj("12345678000199");
        instituicao.setEmail("instituicao@email.com");
        instituicao.setSenha("hash");
        instituicao.setPerfil(Perfil.INSTITUICAO);
        instituicao.setStatus(Status.ATIVO);
        instituicao.setData_criacao(LocalDateTime.now());
        return instituicao;
    }
}
