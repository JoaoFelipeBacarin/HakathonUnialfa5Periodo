// src/main/java/br/unialfa/hackathon/service/AlunoService.java
package br.unialfa.hackathon.service;

import br.unialfa.hackathon.dto.AlunoResponse;
import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.repository.AlunoRepository; // Certifique-se de ter um AlunoRepository
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository; // Renomeado de 'repository' para 'alunoRepository' para clareza
    // Removido AlunoTurmaRepository se Aluno agora tem Turma diretamente.
    // Se AlunoTurma for para múltiplos alunos em múltiplas turmas, o modelo precisa ser revisto.

    // Método para listar alunos com detalhes para o DTO
    public List<AlunoResponse> listarAlunosComDetalhes() {
        return alunoRepository.findAll().stream()
                .map(aluno -> AlunoResponse.builder()
                        .id(aluno.getId())
                        .nome(aluno.getNome())
                        .turma(aluno.getTurma() != null ? aluno.getTurma().getNome() : null) // Pega o nome da turma
                        .email(aluno.getUsuario() != null ? aluno.getUsuario().getEmail() : null) // Pega o email do usuario associado
                        .build())
                .collect(Collectors.toList());
    }

    // Se você removeu o AlunoTurmaRepository, você pode precisar de um AlunoRepository padrão:
    // Exemplo de AlunoRepository (se você ainda não tem um):
    // @Repository
    // public interface AlunoRepository extends JpaRepository<Aluno, Long> {}

    // Manter métodos existentes, se houver, ou adicionar novos conforme necessário
    // Por exemplo, para salvar Alunos:
    public Aluno salvar(Aluno aluno) {
        return alunoRepository.save(aluno);
    }
}