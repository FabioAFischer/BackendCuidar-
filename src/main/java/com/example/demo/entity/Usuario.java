package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "usuario")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "{erro.usuario.nome.obrigatorio}")
    @Size(max = 200, message = "{erro.usuario.nome.tamanho}")
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @NotBlank(message = "{erro.usuario.data.obrigatorio}")
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime data_criacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime data_atualizacao;
}
