package com.example.demo.dtos;

import java.time.LocalDateTime;

import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;

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
public class IdosoDTO {

    private Long id;
    private String nome;
    private Long cpf;
    private String observacoes;
    private Long instituicaoId;
    private Long contatoId;
    private com.example.demo.dtos.ContatoDTO contato;
    private LocalDateTime data_criacao;
    private LocalDateTime data_atualizacao;
    private Perfil perfil;
    private Status status;
}
