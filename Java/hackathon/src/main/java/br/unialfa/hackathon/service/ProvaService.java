package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Questao;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.ProvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final QuestaoService questaoService;

    public List<Prova> findAll() {
        return provaRepository.findByAtivaTrue();
    }

    public Prova findById(Long id) {
        return provaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Prova save(Prova prova) {
        return provaRepository.save(prova);
    }

    public void deleteById(Long id) {
        Prova prova = findById(id);
        if (prova != null) {
            prova.setAtiva(false);
            save(prova);
        }
    }

    public List<Prova> findByProfessor(Usuario professor) {
        return provaRepository.findByProfessorIdAndAtivaTrue(professor.getId());
    }

    public List<Prova> findByTurmaId(Long turmaId) {
        return provaRepository.findByTurmaIdAndAtivaTrue(turmaId);
    }

    @Transactional
    public Prova criarProvaComQuestoes(Prova prova, List<String> respostasCorretas) {
        Prova provaSalva = save(prova);

        for (int i = 0; i < respostasCorretas.size(); i++) {
            Questao questao = new Questao();
            questao.setNumero(i + 1);
            questao.setRespostaCorreta(br.unialfa.hackathon.model.Alternativa.valueOf(respostasCorretas.get(i)));
            questao.setProva(provaSalva);
            questaoService.save(questao);
        }

        provaSalva.setNumeroQuestoes(respostasCorretas.size());
        return save(provaSalva);
    }

    @Transactional
    public Prova atualizarProvaComQuestoes(Prova prova, List<String> respostasCorretas) {
        // Deletar questões antigas
        List<Questao> questoesAntigas = questaoService.findByProvaId(prova.getId());
        for (Questao questao : questoesAntigas) {
            questaoService.deleteById(questao.getId());
        }

        // Criar novas questões
        for (int i = 0; i < respostasCorretas.size(); i++) {
            Questao questao = new Questao();
            questao.setNumero(i + 1);
            questao.setRespostaCorreta(br.unialfa.hackathon.model.Alternativa.valueOf(respostasCorretas.get(i)));
            questao.setProva(prova);
            questaoService.save(questao);
        }

        prova.setNumeroQuestoes(respostasCorretas.size());
        return save(prova);
    }
}