package com.example.demo.entity;

import java.time.LocalDateTime;

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
@ToString
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "idoso obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_idoso", nullable = false, foreignKey = @ForeignKey(name = "fk_idoso_consulta"))
    private Idoso idoso;

    @NotBlank(message = "Médico obrigatório")
    @Column(name = "medico", nullable = false)
    private String medico;

    @NotBlank(message = "Especialidade obrigatória")
    @Column(name = "especialidade", nullable = false)
    private String especialidade;

    @NotBlank(message = "Local obrigatório")
    @Column(name = "local_consulta", nullable = false)
    private String local;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @NotNull(message = "Data de criação é obrigatória")
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
