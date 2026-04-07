package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CuidadorDTO {

    private Long id;
    private String nome;
    private Long cpf;
    private String login;
    private String senha;
    private Long instituicaoId;
    private ContatoDTO contato;
}