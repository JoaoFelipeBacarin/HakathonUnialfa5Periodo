package br.unialfa.hackathon.service;


import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository repository;

    public Turma salvar(Turma turma) {
        return repository.save(turma);
    }

    public List<Turma> listarPorProfessor(Usuario professor) {
        return repository.findByProfessor(professor);
    }

    public List<Turma> listarTodas() {
        return repository.findAll();
    }
}