package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.enums.Perfil;

import ch.qos.logback.core.status.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name="Prescricao")
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotNull(message = "Remédio obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_remedio", nullable = false, foreignKey = @ForeignKey(name = "fk_remedio_prescricao"))
    private Remedio remedio;

    @NotNull(message = "idoso obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_idoso", nullable = false, foreignKey = @ForeignKey(name = "fk_idoso_prescricao"))
    private Idoso idoso;

    @NotNull(message = "Inicio obrigatório")
    private LocalDateTime data_criacao;

    @Column(nullable = false)
    private Status status;

    private LocalDateTime data_fim;

    @NotNull(message = "Campo vazio")
    private Boolean necessario_jejum;
    private String istrucao;

    @NotNull(message = "Campo intervalo vazio")
    private Double intervalo;

    @NotBlank(message = "Campo dosagem vazio")
    private String dosagem;

    
}
