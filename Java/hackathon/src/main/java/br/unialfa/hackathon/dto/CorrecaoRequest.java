package br.unialfa.hackathon.dto;

import lombok.Data;

import java.util.List;

@Data
public class CorrecaoRequest {

    private Long provaId;

    private Long alunoId;

    private List<String> respostas;
}