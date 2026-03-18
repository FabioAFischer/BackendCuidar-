package com.example.demo.entity;

<<<<<<< HEAD
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
=======
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
>>>>>>> 5c81f87296b574421e2da467fce6f8ae1583c2b9

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@ToString(callSuper = true, exclude = {"contato", "instituicao"})
=======
@ToString
>>>>>>> 5c81f87296b574421e2da467fce6f8ae1583c2b9
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
<<<<<<< HEAD

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;
=======
>>>>>>> 5c81f87296b574421e2da467fce6f8ae1583c2b9
}