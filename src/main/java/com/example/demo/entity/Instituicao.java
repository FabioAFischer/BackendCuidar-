package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@ToString(callSuper = true, exclude = "cuidadores")
@Table(name = "instituicao")
public class Instituicao extends Usuario {

    @CNPJ
    @NotBlank(message = "CNPJ deve ser preenchido")
    @Column(length = 14, nullable = false, unique = true)
    private String cnpj;

    @Column(length = 200)
    private String bairro;

    @Column(length = 2)
    private String uf;

    private int numero;
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Column(length = 8)
    private String cep;

    @OneToMany(mappedBy = "instituicao", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Idoso> idosos = new ArrayList<>();

    @OneToMany(mappedBy = "instituicao", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cuidador> cuidadores = new ArrayList<>();
}
