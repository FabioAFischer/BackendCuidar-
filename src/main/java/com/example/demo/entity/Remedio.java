package com.example.demo.entity;


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
@Table(name = "remedio")
public class Remedio {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Nome Obrigatorio")
    @Size(max = 200, message = "Tamanho do nome do usuário excedido")
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Size(max = 500, message = "Tamanho da observação excedido")
    @Column(name = "nome", length = 500)
    private String observacao;


}
