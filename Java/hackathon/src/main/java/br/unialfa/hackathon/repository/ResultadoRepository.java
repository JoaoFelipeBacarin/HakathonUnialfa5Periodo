package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    List<Resultado> findByAluno(Aluno aluno);

    List<Resultado> findByProva(Prova prova);

    Optional<Resultado> findByAlunoAndProva(Aluno aluno, Prova prova);

    @Query("SELECT r FROM Resultado r WHERE r.prova.id = :provaId ORDER BY r.nota DESC")
    List<Resultado> findByProvaIdOrderByNotaDesc(Long provaId);

    @Query("SELECT AVG(r.nota) FROM Resultado r WHERE r.prova.id = :provaId")
    Double findMediaNotaByProvaId(Long provaId);

    @Query("SELECT COUNT(r) FROM Resultado r WHERE r.prova.id = :provaId AND r.nota >= :notaMinima")
    Long countByProvaIdAndNotaGreaterThanEqual(Long provaId, Double notaMinima);
}
