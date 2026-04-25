package com.example.demo.dtos;

import java.time.LocalDateTime;

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
public class PrescricaoDTO {

    private Integer id;
    private Integer remedioId;
    private String remedioNome;
    private Integer idosoId;
    private String idosoNome;
    private LocalDateTime data_criacao;
    private Status status;
    private LocalDateTime data_fim;
    private String instrucao;
    private Double intervalo;
    private String dosagem;
}
