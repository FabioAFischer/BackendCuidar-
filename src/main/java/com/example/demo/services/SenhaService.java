// SenhaService.java
package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.exceptions.BusinessException;

@Service
public class SenhaService {

    private static final String REGEX_SENHA_FORTE =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

    public void validar(String senha) {
        if (senha == null || !senha.matches(REGEX_SENHA_FORTE)) {
            throw new BusinessException(
                "A senha deve ter no mínimo 8 caracteres, com letra maiúscula, minúscula, número e caractere especial");
        }
    }
}