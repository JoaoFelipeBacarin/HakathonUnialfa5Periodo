package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.AlunoTurma;
import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunoTurmaRepository extends JpaRepository<AlunoTurma, Long> {
    List<AlunoTurma> findByTurma(Turma turma);
    List<AlunoTurma> findByAluno(Usuario aluno);
    boolean existsByAlunoAndTurma(Usuario aluno, Turma turma);
}
