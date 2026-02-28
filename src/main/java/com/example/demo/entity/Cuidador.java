package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
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
}