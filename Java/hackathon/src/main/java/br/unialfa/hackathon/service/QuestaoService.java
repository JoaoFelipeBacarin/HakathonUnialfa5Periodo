package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Questao;
import br.unialfa.hackathon.repository.QuestaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepository;

    public List<Questao> findAll() {
        return questaoRepository.findAll();
    }

    public Questao findById(Long id) {
        return questaoRepository.findById(id).orElse(null);
    }

    public Questao save(Questao questao) {
        return questaoRepository.save(questao);
    }

    public void deleteById(Long id) {
        questaoRepository.deleteById(id);
    }

    public List<Questao> findByProvaId(Long provaId) {
        return questaoRepository.findByProvaIdOrderByNumero(provaId);
    }
}