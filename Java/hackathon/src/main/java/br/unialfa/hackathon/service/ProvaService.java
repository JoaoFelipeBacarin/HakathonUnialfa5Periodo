package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Disciplina;
import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.repository.ProvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository repository;

    public Prova salvar(Prova prova) {
        return repository.save(prova);
    }

    public List<Prova> listarPorTurma(Turma turma) {
        return repository.findByTurma(turma);
    }

    public List<Prova> listarPorDisciplina(Disciplina disciplina) {
        return repository.findByDisciplina(disciplina);
    }

    public List<Prova> listarPorData(LocalDate data) {
        return repository.findByDataAplicacao(data);
    }

    public List<Prova> listarTodas() {
        return repository.findAll();
    }
}
