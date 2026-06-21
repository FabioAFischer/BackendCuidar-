package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.exceptions.PasswordPolicyException;

@ExtendWith(MockitoExtension.class)
class SenhaServiceTest {

    @InjectMocks
    private SenhaService service;

    @Test
    void deveValidarSenhaQuandoSenhaForForte() {
        assertDoesNotThrow(() -> service.validarSenha("Senha@123"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForNula() {
        assertThrows(PasswordPolicyException.class, () -> service.validarSenha(null));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoTiverLetraMaiuscula() {
        assertThrows(PasswordPolicyException.class, () -> service.validarSenha("senha@123"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoTiverNumero() {
        assertThrows(PasswordPolicyException.class, () -> service.validarSenha("Senha@abc"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaNaoTiverCaractereEspecial() {
        assertThrows(PasswordPolicyException.class, () -> service.validarSenha("Senha1234"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaForCurta() {
        assertThrows(PasswordPolicyException.class, () -> service.validarSenha("Se@123"));
    }
}
