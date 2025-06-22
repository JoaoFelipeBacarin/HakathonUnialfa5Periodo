package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "provas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column
    private String descricao;

    @Column(nullable = false)
    private LocalDate dataAplicacao;

    @Column
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    private Integer numeroQuestoes;

    @Column(nullable = false)
    private Double valorTotal = 10.0;

    @Column
    private Boolean ativa = true;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Usuario professor;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Questao> questoes;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resultado> resultados;
}