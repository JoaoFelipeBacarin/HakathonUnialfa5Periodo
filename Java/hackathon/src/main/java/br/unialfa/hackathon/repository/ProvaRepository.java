package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Disciplina;
import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
    List<Prova> findByTurma(Turma turma);
    List<Prova> findByDisciplina(Disciplina disciplina);
    List<Prova> findByDataAplicacao(LocalDate data);
}
