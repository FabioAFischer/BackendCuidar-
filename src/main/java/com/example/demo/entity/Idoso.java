
package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "idoso")
public class Idoso extends Usuario {

    @NotNull(message = "Instituição é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_idoso_instituicao"))
    private Instituicao instituicao;

    @NotNull(message = "CPF deve ser preenchido")
    @Column(length = 11, nullable = false, unique = true)
    private Long cpf;

    @Column(length = 300)
    private String observacoes;
}