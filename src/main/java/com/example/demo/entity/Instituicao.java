package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="instituicao")
public class Instituicao extends Usuario {

    @NotBlank(message = "CNPJ deve ser prenchido")
    @Column(length = 14, nullable = false, unique = true)
    private String cnpj;

    @Column(length = 200)
    private String bairro;

    @Column(length = 2)
    private String uf;

    private Integer numero;

    @Column(length = 8)
    private String cep;
    @NotNull(message = "Um DDD deve ser inserido")
    @Column(length = 3)
    private String ddd;
    @NotNull(message = "Um número de contato deve ser prenchido")
    @Column(length = 15)
    private String telefone;
}
