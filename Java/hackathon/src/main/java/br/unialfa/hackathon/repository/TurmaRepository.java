package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    List<Turma> findByProfessor(Usuario professor);
}
