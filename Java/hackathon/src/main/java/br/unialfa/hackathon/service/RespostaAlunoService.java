package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.RespostaAluno;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.RespostaAlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RespostaAlunoService {

    private final RespostaAlunoRepository repository;

    public RespostaAluno corrigirERegistrar(Prova prova, Usuario aluno, String respostas) {
        String gabarito = prova.getGabarito();
        int acertos = 0;

        for (int i = 0; i < Math.min(gabarito.length(), respostas.length()); i++) {
            if (gabarito.charAt(i) == respostas.charAt(i)) {
                acertos++;
            }
        }

        double nota = (double) acertos / gabarito.length() * 10;

        RespostaAluno resposta = RespostaAluno.builder()
                .prova(prova)
                .aluno(aluno)
                .respostas(respostas)
                .acertos(acertos)
                .nota(nota)
                .build();

        return repository.save(resposta);
    }

    public Optional<RespostaAluno> buscarPorProvaEAluno(Prova prova, Usuario aluno) {
        return repository.findByProvaAndAluno(prova, aluno);
    }

    public List<RespostaAluno> listarPorProva(Prova prova) {
        return repository.findByProva(prova);
    }
}