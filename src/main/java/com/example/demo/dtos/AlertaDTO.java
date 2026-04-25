package com.example.demo.dtos;

import java.time.LocalDateTime;

import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;

import jakarta.validation.constraints.NotNull;
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
public class AlertaDTO {

    private Integer id;

    @NotNull(message = "Idoso é obrigatório")
    private Integer idosoId;

    @NotNull(message = "Tipo do alerta é obrigatório")
    private TipoAlerta tipoAlerta;

    @NotNull(message = "Status do alerta é obrigatório")
    private StatusAlertas statusAlertas;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    @NotNull(message = "Data agendada é obrigatória")
    private LocalDateTime dataAgendada;
}
