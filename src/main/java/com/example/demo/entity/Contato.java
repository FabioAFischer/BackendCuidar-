package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@ToString(exclude = {"cuidador", "idosos"})
@Table(name = "contato")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Um DDD deve ser inserido")
    @Column(length = 3, nullable = false)
    private String ddd;

    @NotBlank(message = "Um número de contato deve ser preenchido")
    @Column(length = 15, nullable = false)
    private String telefone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuidador_id", unique = true)
    private Cuidador cuidador;

    @OneToMany(mappedBy = "contato")
    private List<Idoso> idosos = new ArrayList<>();
}