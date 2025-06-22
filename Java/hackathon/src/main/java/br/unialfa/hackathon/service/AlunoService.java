package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public List<Aluno> findAll() {
        return alunoRepository.findByAtivoTrue();
    }

    public Aluno findById(Long id) {
        return alunoRepository.findById(id).orElse(null);
    }

    public Aluno findByMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula).orElse(null);
    }

    public Aluno save(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public void deleteById(Long id) {
        Aluno aluno = findById(id);
        if (aluno != null) {
            aluno.setAtivo(false);
            save(aluno);
        }
    }

    public List<Aluno> findByTurmaId(Long turmaId) {
        return alunoRepository.findByTurmaId(turmaId);
    }

    public List<Aluno> findByNome(String nome) {
        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public boolean existsByMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula).isPresent();
    }
}
