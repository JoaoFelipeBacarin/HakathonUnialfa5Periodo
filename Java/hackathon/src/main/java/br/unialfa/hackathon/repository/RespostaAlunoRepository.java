package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.RespostaAluno;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespostaAlunoRepository extends JpaRepository<RespostaAluno, Long> {
    List<RespostaAluno> findByProva(Prova prova);
    Optional<RespostaAluno> findByProvaAndAluno(Prova prova, Usuario aluno);
}
