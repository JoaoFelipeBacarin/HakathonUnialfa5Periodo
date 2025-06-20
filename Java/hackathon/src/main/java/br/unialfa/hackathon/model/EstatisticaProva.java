package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstatisticaProva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Prova prova;

    private Double media;
    private Double notaMinima;
    private Double notaMaxima;
}
