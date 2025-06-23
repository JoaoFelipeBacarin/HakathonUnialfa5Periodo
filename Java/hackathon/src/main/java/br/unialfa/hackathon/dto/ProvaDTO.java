package br.unialfa.hackathon.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProvaDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate dataAplicacao;
    private Integer numeroQuestoes;
    private Double valorTotal;
    private Boolean ativa;
    private Long turmaId;
    private String turmaNome;
    private String disciplinaNome;
    private String professorNome;
}