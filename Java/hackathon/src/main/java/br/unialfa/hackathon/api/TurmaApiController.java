package br.unialfa.hackathon.api;

import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.service.TurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TurmaApiController {

    private final TurmaService turmaService;

    @GetMapping
    public ResponseEntity<?> listarTurmas() {
        try {
            List<Turma> turmas = turmaService.findAll();

            List<Map<String, Object>> turmasDTO = turmas.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(turmasDTO);
        } catch (Exception e) {
            return createErrorResponse("TURMAS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTurma(@PathVariable Long id) {
        try {
            Turma turma = turmaService.findById(id);
            if (turma != null) {
                return ResponseEntity.ok(convertToDTO(turma));
            }
            return createErrorResponse("NOT_FOUND", "Turma não encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("TURMA_ERROR", e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> criarTurma(@RequestBody Map<String, Object> request) {
        try {
            // TODO: Implementar criação de turma
            return createErrorResponse("NOT_IMPLEMENTED", "Endpoint ainda não implementado", HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return createErrorResponse("CREATE_ERROR", e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTurma(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            // TODO: Implementar atualização de turma
            return createErrorResponse("NOT_IMPLEMENTED", "Endpoint ainda não implementado", HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return createErrorResponse("UPDATE_ERROR", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarTurma(@PathVariable Long id) {
        try {
            Turma turma = turmaService.findById(id);
            if (turma == null) {
                return createErrorResponse("NOT_FOUND", "Turma não encontrada", HttpStatus.NOT_FOUND);
            }

            turmaService.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Turma desativada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("DELETE_ERROR", e.getMessage());
        }
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<?> listarTurmasPorProfessor(@PathVariable Long professorId) {
        try {
            // TODO: Implementar busca por professor
            return createErrorResponse("NOT_IMPLEMENTED", "Endpoint ainda não implementado", HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return createErrorResponse("PROFESSOR_ERROR", e.getMessage());
        }
    }

    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<?> buscarAlunosPorTurma(@PathVariable Long turmaId) {
        try {
            Turma turma = turmaService.findById(turmaId);
            if (turma != null) {
                // Converta a lista de Aluno para uma lista de DTOs de aluno
                List<Map<String, Object>> alunosDTO = turma.getAlunos().stream()
                        .map(aluno -> {
                            Map<String, Object> dto = new HashMap<>();
                            dto.put("id", aluno.getId());
                            dto.put("nome", aluno.getNome());
                            dto.put("matricula", aluno.getMatricula());
                            dto.put("email", aluno.getEmail());
                            // Adicione outros campos que você precisa no Flutter
                            return dto;
                        })
                        .collect(Collectors.toList());
                return ResponseEntity.ok(alunosDTO);
            } else {
                return createErrorResponse("TURMA_NOT_FOUND", "Turma não encontrada com ID: " + turmaId, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("ALUNOS_BY_TURMA_ERROR", e.getMessage());
        }
    }

    private Map<String, Object> convertToDTO(Turma turma) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", turma.getId());
        dto.put("nome", turma.getNome());
        dto.put("descricao", turma.getDescricao());
        dto.put("ano", turma.getAno());
        dto.put("periodo", turma.getPeriodo());
        dto.put("ativa", turma.getAtiva());

        if (turma.getDisciplina() != null) {
            dto.put("disciplinaId", turma.getDisciplina().getId());
            dto.put("disciplinaNome", turma.getDisciplina().getNome());
        }

        if (turma.getUsuario() != null) {
            dto.put("professorId", turma.getUsuario().getId());
            dto.put("professorNome", turma.getUsuario().getNome());
        }

        if (turma.getAlunos() != null) {
            dto.put("totalAlunos", turma.getAlunos().size());
        }

        return dto;
    }

    private ResponseEntity<?> createErrorResponse(String errorCode, String message) {
        return createErrorResponse(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> createErrorResponse(String errorCode, String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", errorCode);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(error);
    }
}