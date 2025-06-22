package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProvaRepository extends JpaRepository<Prova, Long> {

    List<Prova> findByAtivaTrue();

    List<Prova> findByTurma(Turma turma);

    List<Prova> findByProfessor(Usuario professor);

    List<Prova> findByDataAplicacao(LocalDate dataAplicacao);

    @Query("SELECT p FROM Prova p WHERE p.turma.id = :turmaId AND p.ativa = true ORDER BY p.dataAplicacao DESC")
    List<Prova> findByTurmaIdAndAtivaTrue(Long turmaId);

    @Query("SELECT p FROM Prova p WHERE p.professor.id = :professorId AND p.ativa = true ORDER BY p.dataAplicacao DESC")
    List<Prova> findByProfessorIdAndAtivaTrue(Long professorId);
}