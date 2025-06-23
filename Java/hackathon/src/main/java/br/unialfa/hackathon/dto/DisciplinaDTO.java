package br.unialfa.hackathon.dto;

import lombok.Data;

@Data
public class DisciplinaDTO {
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private Integer cargaHoraria;
    private Boolean ativa;
}