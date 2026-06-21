package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.demo.entity.CodigoVerificacao;
import com.example.demo.events.Codigo2faGeradoEvent;
import com.example.demo.exceptions.VerificationCodeException;
import com.example.demo.repository.CodigoVerificacaoRepository;

@ExtendWith(MockitoExtension.class)
class TwoFactorServiceTest {

    @Mock
    private CodigoVerificacaoRepository repository;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private TwoFactorService service;

    @Test
    void devePublicarEventoAposGerarCodigo() {
        service.enviarCodigoDoisFatores("cuidador@email.com");

        verify(repository).deleteByEmail("cuidador@email.com");
        verify(repository).save(any(CodigoVerificacao.class));

        ArgumentCaptor<Codigo2faGeradoEvent> captor = ArgumentCaptor.forClass(Codigo2faGeradoEvent.class);
        verify(publisher).publishEvent(captor.capture());

        Codigo2faGeradoEvent event = captor.getValue();
        assertEquals("cuidador@email.com", event.email());
        assertEquals(6, Objects.requireNonNull(event.codigo()).length());
    }

    @Test
    void deveValidarCodigoQuandoCodigoForValido() {
        CodigoVerificacao verificacao = codigoVerificacao("cuidador@email.com", "123456", LocalDateTime.now().plusMinutes(5));

        when(repository.findTopByEmailAndUsadoFalseOrderByExpiracaoDesc("cuidador@email.com"))
                .thenReturn(Optional.of(verificacao));

        service.validarCodigoDoisFatores("cuidador@email.com", "123456");

        assertEquals(true, verificacao.isUsado());
        verify(repository).save(verificacao);
    }

    @Test
    void deveLancarExcecaoQuandoCodigoNaoExistir() {
        when(repository.findTopByEmailAndUsadoFalseOrderByExpiracaoDesc("cuidador@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(VerificationCodeException.class,
                () -> service.validarCodigoDoisFatores("cuidador@email.com", "123456"));
    }

    @Test
    void deveLancarExcecaoQuandoCodigoEstiverExpirado() {
        CodigoVerificacao verificacao = codigoVerificacao("cuidador@email.com", "123456", LocalDateTime.now().minusMinutes(1));

        when(repository.findTopByEmailAndUsadoFalseOrderByExpiracaoDesc("cuidador@email.com"))
                .thenReturn(Optional.of(verificacao));

        assertThrows(VerificationCodeException.class,
                () -> service.validarCodigoDoisFatores("cuidador@email.com", "123456"));
    }

    @Test
    void deveLancarExcecaoQuandoCodigoForInvalido() {
        CodigoVerificacao verificacao = codigoVerificacao("cuidador@email.com", "123456", LocalDateTime.now().plusMinutes(5));

        when(repository.findTopByEmailAndUsadoFalseOrderByExpiracaoDesc("cuidador@email.com"))
                .thenReturn(Optional.of(verificacao));

        assertThrows(VerificationCodeException.class,
                () -> service.validarCodigoDoisFatores("cuidador@email.com", "000000"));
    }

    private CodigoVerificacao codigoVerificacao(String email, String codigo, LocalDateTime expiracao) {
        CodigoVerificacao verificacao = new CodigoVerificacao();
        verificacao.setEmail(email);
        verificacao.setCodigo(codigo);
        verificacao.setExpiracao(expiracao);
        verificacao.setUsado(false);
        return verificacao;
    }

}
