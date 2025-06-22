package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByMatricula(String matricula);

    Optional<Aluno> findByCpf(String cpf);

    List<Aluno> findByAtivoTrue();

    List<Aluno> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT a FROM Aluno a JOIN a.turmas t WHERE t.id = :turmaId AND a.ativo = true")
    List<Aluno> findByTurmaId(Long turmaId);
}