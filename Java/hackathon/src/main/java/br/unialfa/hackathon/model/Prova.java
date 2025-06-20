package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "provas")
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @ManyToOne
    private Turma turma;

    @ManyToOne
    private Disciplina disciplina;

    private LocalDate dataAplicacao;

    @Lob
    private String gabarito; // Ex: "ABCDEBADEC"
}
