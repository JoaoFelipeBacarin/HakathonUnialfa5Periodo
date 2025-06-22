package br.unialfa.hackathon.service;

import br.unialfa.hackathon.dto.CorrecaoRequest;
import br.unialfa.hackathon.dto.EstatisticaProva;
import br.unialfa.hackathon.model.*;
import br.unialfa.hackathon.repository.ResultadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorrecaoService {

    private final ResultadoRepository resultadoRepository;
    private final ProvaService provaService;
    private final AlunoService alunoService;
    private final QuestaoService questaoService;

    @Transactional
    public Resultado corrigirProva(CorrecaoRequest correcaoRequest) {
        Prova prova = provaService.findById(correcaoRequest.getProvaId());
        Aluno aluno = alunoService.findById(correcaoRequest.getAlunoId());

        if (prova == null || aluno == null) {
            throw new RuntimeException("Prova ou aluno não encontrado");
        }

        // Verificar se já existe resultado
        Resultado resultadoExistente = resultadoRepository.findByAlunoAndProva(aluno, prova).orElse(null);
        if (resultadoExistente != null) {
            throw new RuntimeException("Prova já foi corrigida para este aluno");
        }

        List<Questao> questoes = questaoService.findByProvaId(prova.getId());
        List<RespostaAluno> respostas = new ArrayList<>();

        int acertos = 0;
        int erros = 0;
        double pontuacaoTotal = 0.0;

        for (int i = 0; i < correcaoRequest.getRespostas().size() && i < questoes.size(); i++) {
            Questao questao = questoes.get(i);
            String respostaMarcada = correcaoRequest.getRespostas().get(i);

            RespostaAluno resposta = new RespostaAluno();
            resposta.setNumeroQuestao(questao.getNumero());
            resposta.setQuestao(questao);

            if (respostaMarcada != null && !respostaMarcada.trim().isEmpty()) {
                try {
                    Alternativa alternativaMarcada = Alternativa.valueOf(respostaMarcada.toUpperCase());
                    resposta.setRespostaMarcada(alternativaMarcada);

                    boolean acertou = alternativaMarcada.equals(questao.getRespostaCorreta());
                    resposta.setAcertou(acertou);

                    if (acertou) {
                        acertos++;
                        resposta.setPontuacao(questao.getPeso());
                        pontuacaoTotal += questao.getPeso();
                    } else {
                        erros++;
                        resposta.setPontuacao(0.0);
                    }
                } catch (IllegalArgumentException e) {
                    // Resposta inválida
                    resposta.setRespostaMarcada(null);
                    resposta.setAcertou(false);
                    resposta.setPontuacao(0.0);
                    erros++;
                }
            } else {
                // Questão em branco
                resposta.setRespostaMarcada(null);
                resposta.setAcertou(false);
                resposta.setPontuacao(0.0);
                erros++;
            }

            respostas.add(resposta);
        }

        // Calcular nota final
        double nota = (pontuacaoTotal / questoes.size()) * prova.getValorTotal();

        // Criar resultado
        Resultado resultado = new Resultado();
        resultado.setAluno(aluno);
        resultado.setProva(prova);
        resultado.setNota(nota);
        resultado.setAcertos(acertos);
        resultado.setErros(erros);

        Resultado resultadoSalvo = resultadoRepository.save(resultado);

        // Associar respostas ao resultado
        for (RespostaAluno resposta : respostas) {
            resposta.setResultado(resultadoSalvo);
        }
        resultadoSalvo.setRespostas(respostas);

        return resultadoRepository.save(resultadoSalvo);
    }

    public EstatisticaProva calcularEstatisticas(Long provaId) {
        List<Resultado> resultados = resultadoRepository.findByProvaIdOrderByNotaDesc(provaId);

        if (resultados.isEmpty()) {
            return new EstatisticaProva();
        }

        double media = resultados.stream().mapToDouble(Resultado::getNota).average().orElse(0.0);
        double notaMaxima = resultados.stream().mapToDouble(Resultado::getNota).max().orElse(0.0);
        double notaMinima = resultados.stream().mapToDouble(Resultado::getNota).min().orElse(0.0);

        long aprovados = resultados.stream().filter(r -> r.getNota() >= 6.0).count();
        long reprovados = resultados.size() - aprovados;

        EstatisticaProva estatistica = new EstatisticaProva();
        estatistica.setProvaId(provaId);
        estatistica.setTotalAlunos(resultados.size());
        estatistica.setMedia(media);
        estatistica.setNotaMaxima(notaMaxima);
        estatistica.setNotaMinima(notaMinima);
        estatistica.setAprovados((int) aprovados);
        estatistica.setReprovados((int) reprovados);
        estatistica.setResultados(resultados);

        return estatistica;
    }

    public List<Resultado> findResultadosByProva(Long provaId) {
        return resultadoRepository.findByProvaIdOrderByNotaDesc(provaId);
    }

    public List<Resultado> findResultadosByAluno(Long alunoId) {
        Aluno aluno = alunoService.findById(alunoId);
        return aluno != null ? resultadoRepository.findByAluno(aluno) : new ArrayList<>();
    }
}