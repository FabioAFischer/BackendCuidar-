package com.example.demo.dtos;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContatoDTO {

    private Integer id;
    private String ddd;
    private String telefone;
    private Integer cuidadorId;
    private List<Integer> idososIds;

}
