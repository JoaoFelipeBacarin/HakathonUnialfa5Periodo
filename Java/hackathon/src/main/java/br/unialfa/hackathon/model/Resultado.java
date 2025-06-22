package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resultados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double nota;

    @Column(nullable = false)
    private Integer acertos;

    @Column(nullable = false)
    private Integer erros;

    @Column
    private LocalDateTime dataCorrecao = LocalDateTime.now();

    @Column
    private String observacoes;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prova_id")
    private Prova prova;

    @OneToMany(mappedBy = "resultado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RespostaAluno> respostas;
}