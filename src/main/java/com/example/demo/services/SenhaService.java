package com.example.demo.services;

import org.springframework.stereotype.Service;

@Service
public class SenhaService {

    private static final String REGEX_SENHA_FORTE =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

    public void validar(String senha) {
        if (senha == null || !senha.matches(REGEX_SENHA_FORTE)) {
            throw new RuntimeException(
                    "A senha deve ter no minimo 8 caracteres, com letra maiuscula, letra minuscula, numero e caractere especial");
        }
    }
}
