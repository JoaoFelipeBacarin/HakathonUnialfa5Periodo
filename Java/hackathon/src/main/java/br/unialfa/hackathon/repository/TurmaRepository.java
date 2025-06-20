package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
}

