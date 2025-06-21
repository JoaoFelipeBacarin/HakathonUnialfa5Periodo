package br.unialfa.hackathon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlunoResponse {
    private Long id;
    private String nome;
    private String turma; // Representará o nome da turma (ex: "3º A")
    private String email; // Representará o email do usuário associado ao aluno
}