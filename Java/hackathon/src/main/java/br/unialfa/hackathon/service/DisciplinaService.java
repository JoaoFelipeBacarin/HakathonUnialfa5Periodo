package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Disciplina;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository repository;

    public Disciplina salvar(Disciplina disciplina) {
        return repository.save(disciplina);
    }

    public List<Disciplina> listarPorProfessor(Usuario professor) {
        return repository.findByProfessor(professor);
    }

    public List<Disciplina> listarTodas() {
        return repository.findAll();
    }
}
