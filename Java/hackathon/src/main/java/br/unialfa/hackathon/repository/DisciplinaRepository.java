package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Disciplina;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    List<Disciplina> findByProfessor(Usuario professor);
}
