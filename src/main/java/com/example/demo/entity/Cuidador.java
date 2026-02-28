package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"contato", "instituicao"})
@Table(name = "cuidador")
public class Cuidador extends Usuario {

    @NotNull(message = "CPF deve ser preenchido")
    @Column(length = 11, nullable = false, unique = true)
    private Long cpf;

    @NotBlank
    @Column(length = 300, nullable = false)
    private String login;

    @NotBlank
    @Column(length = 300, nullable = false)
    private String senha;

    @OneToOne(mappedBy = "cuidador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contato contato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;
}