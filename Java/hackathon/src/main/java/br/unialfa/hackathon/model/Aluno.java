package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;
    private Long ra;
    private Long tipo;
    private Boolean status;
    private Date dataDeCadastro;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;
}
