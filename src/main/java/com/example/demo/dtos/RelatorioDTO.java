package com.example.demo.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDTO {

    private LocalDateTime geradoEm;
    private SecaoInstituicaoDTO instituicoes;
    private SecaoCuidadorDTO cuidadores;
    private SecaoIdosoDTO idosos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecaoInstituicaoDTO {
        private long total;
        private long ativas;
        private long inativas;
        private List<ItemInstituicaoDTO> lista;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecaoCuidadorDTO {
        private long total;
        private long ativos;
        private long inativos;
        private List<ItemCuidadorDTO> lista;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecaoIdosoDTO {
        private long total;
        private long ativos;
        private long inativos;
        private List<ItemIdosoDTO> lista;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemInstituicaoDTO {
        private Integer id;
        private String nome;
        private String cnpj;
        private String email;
        private String uf;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemCuidadorDTO {
        private Integer id;
        private String nome;
        private String email;
        private String cpf;
        private String status;
        private String instituicaoNome;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemIdosoDTO {
        private Integer id;
        private String nome;
        private String cpf;
        private String status;
    }
}