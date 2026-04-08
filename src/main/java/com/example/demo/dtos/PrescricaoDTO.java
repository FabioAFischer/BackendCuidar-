package com.example.demo.dtos;
import java.time.LocalDateTime;

import com.example.demo.entity.Idoso;
import com.example.demo.entity.Remedio;
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
    private Remedio remedio;
    private Idoso idoso;
    private LocalDateTime data_criacao;
    private Status status;
    private LocalDateTime data_fim;
    private Boolean necessario_jejum;
    private String instrucao;
    private Double intervalo;
    private String dosagem;


}
