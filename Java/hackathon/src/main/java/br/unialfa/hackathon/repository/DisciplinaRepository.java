package br.unialfa.hackathon.repository;


import br.unialfa.hackathon.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Disciplina> findByCodigo(String codigo);

    List<Disciplina> findByAtivaTrue();

    List<Disciplina> findByNomeContainingIgnoreCase(String nome);
}