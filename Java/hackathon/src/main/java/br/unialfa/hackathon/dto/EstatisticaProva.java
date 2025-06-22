package br.unialfa.hackathon.dto;

import br.unialfa.hackathon.model.Resultado;
import lombok.Data;

import java.util.List;

@Data
public class EstatisticaProva {
    private Long provaId;
    private Integer totalAlunos = 0;
    private Double media = 0.0;
    private Double notaMaxima = 0.0;
    private Double notaMinima = 0.0;
    private Integer aprovados = 0;
    private Integer reprovados = 0;
    private List<Resultado> resultados;

    public Double getPercentualAprovacao() {
        if (totalAlunos == 0) return 0.0;
        return (aprovados.doubleValue() / totalAlunos) * 100;
    }
}