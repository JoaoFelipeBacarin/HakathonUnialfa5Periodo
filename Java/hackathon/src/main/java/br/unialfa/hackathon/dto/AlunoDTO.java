package br.unialfa.hackathon.dto;

import lombok.Data;
import java.util.List;

@Data
public class AlunoDTO {
    private Long id;
    private String matricula;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private Boolean ativo;
    private List<String> turmas;
}
