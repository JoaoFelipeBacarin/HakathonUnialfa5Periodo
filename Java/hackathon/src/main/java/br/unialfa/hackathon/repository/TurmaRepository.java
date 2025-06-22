package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByAtivaTrue();

    List<Turma> findByUsuario(Usuario professor);

    List<Turma> findByUsuarioAndAtivaTrue(Usuario professor);

    @Query("SELECT t FROM Turma t JOIN t.alunos a WHERE a.id = :alunoId AND t.ativa = true")
    List<Turma> findByAlunoId(Long alunoId);

    List<Turma> findByAnoAndAtivaTrue(Integer ano);
}