package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.demo.exceptions.EmailSendingException;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @Test
    void deveEnviarEmailCodigoVerificacaoQuandoDadosForemValidos() {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        service.enviarEmailCodigoVerificacao("cuidador@email.com", "123456");

        verify(mailSender).send(message);
    }

    @Test
    void deveLancarExcecaoQuandoEnvioDeEmailFalhar() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("falha"));

        assertThrows(EmailSendingException.class,
                () -> service.enviarEmailCodigoVerificacao("cuidador@email.com", "123456"));
    }
}
