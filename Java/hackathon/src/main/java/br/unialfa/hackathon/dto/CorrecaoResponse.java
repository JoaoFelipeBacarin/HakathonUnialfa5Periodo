package br.unialfa.hackathon.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CorrecaoResponse {
    private boolean success;
    private String message;
    private Long resultadoId;
    private Long alunoId;
    private String alunoNome;
    private Long provaId;
    private String provaTitulo;
    private Double nota;
    private Integer acertos;
    private Integer erros;
    private Integer totalQuestoes;
    private Boolean aprovado;
    private LocalDateTime dataCorrecao;
}