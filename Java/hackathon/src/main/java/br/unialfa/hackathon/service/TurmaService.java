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

    private final TurmaRepository turmaRepository;

    public List<Turma> findAll() {
        return turmaRepository.findByAtivaTrue();
    }

    public Turma findById(Long id) {
        return turmaRepository.findById(id).orElse(null);
    }

    public Turma save(Turma turma) {
        return turmaRepository.save(turma);
    }

    public void deleteById(Long id) {
        Turma turma = findById(id);
        if (turma != null) {
            turma.setAtiva(false);
            save(turma);
        }
    }

    public List<Turma> findByProfessor(Usuario professor) {
        return turmaRepository.findByUsuarioAndAtivaTrue(professor);
    }

    public List<Turma> findByAlunoId(Long alunoId) {
        return turmaRepository.findByAlunoId(alunoId);
    }
}
