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
public class InstituicaoDTO {
    
    private Inte id;
    private String nome;
    private String cnpj;
    private String bairro;
    private String uf;
    private Long numero;
    private String cep;


}
