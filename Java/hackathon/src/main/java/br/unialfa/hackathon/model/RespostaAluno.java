package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "respostas_alunos")
public class RespostaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Prova prova;

    @ManyToOne
    private Usuario aluno;

    @Lob
    private String respostas; // Ex: "ABCDDBADDC"

    private Integer acertos;

    private Double nota;
}
