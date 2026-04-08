package com.example.demo.dtos;

import java.time.LocalDateTime;

import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {

    private int id;

    @NotNull(message = "Idoso é obrigatório")
    private Long idosoId;

    @NotNull(message = "Tipo do alerta é obrigatório")
    private TipoAlerta tipoAlerta;

    @NotNull(message = "Status do alerta é obrigatório")
    private StatusAlertas statusAlertas;

    private LocalDateTime data_Criacao;
    private LocalDateTime data_Atualizacao;
    private LocalDateTime data_Agendada;
}