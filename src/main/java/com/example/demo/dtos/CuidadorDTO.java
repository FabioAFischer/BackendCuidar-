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

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private Integer instituicaoId;
    private ContatoDTO contato;
}
