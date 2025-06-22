package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {

    List<Questao> findByProvaIdOrderByNumero(Long provaId);

    Long countByProvaId(Long provaId);
}