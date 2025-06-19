package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

}
