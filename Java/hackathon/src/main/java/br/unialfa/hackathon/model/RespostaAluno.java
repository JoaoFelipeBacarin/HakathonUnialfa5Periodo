package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "respostas_aluno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numeroQuestao;

    @Column
    @Enumerated(EnumType.STRING)
    private Alternativa respostaMarcada;

    @Column(nullable = false)
    private Boolean acertou;

    @Column
    private Double pontuacao;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_id")
    private Resultado resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id")
    private Questao questao;
}