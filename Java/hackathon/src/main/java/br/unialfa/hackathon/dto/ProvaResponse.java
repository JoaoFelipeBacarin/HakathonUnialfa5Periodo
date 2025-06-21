package br.unialfa.hackathon.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvaResponse {
    private Long id;
    private String nome;
    private String disciplina;
    private Long turmaId;
}