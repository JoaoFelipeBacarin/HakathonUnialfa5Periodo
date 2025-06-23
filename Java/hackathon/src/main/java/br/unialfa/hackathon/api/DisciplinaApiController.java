package br.unialfa.hackathon.api;

import br.unialfa.hackathon.dto.DisciplinaDTO;
import br.unialfa.hackathon.model.Disciplina;
import br.unialfa.hackathon.service.DisciplinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disciplinas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DisciplinaApiController {

    private final DisciplinaService disciplinaService;

    @GetMapping
    public ResponseEntity<?> listarDisciplinas(@RequestParam(required = false) String search) {
        try {
            List<Disciplina> disciplinas;

            if (search != null && !search.isEmpty()) {
                disciplinas = disciplinaService.findByNome(search);
            } else {
                disciplinas = disciplinaService.findAll();
            }

            List<Map<String, Object>> disciplinasDTO = disciplinas.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(disciplinasDTO);
        } catch (Exception e) {
            return createErrorResponse("DISCIPLINAS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarDisciplina(@PathVariable Long id) {
        try {
            Disciplina disciplina = disciplinaService.findById(id);
            if (disciplina != null) {
                return ResponseEntity.ok(convertToDTO(disciplina));
            }
            return createErrorResponse("NOT_FOUND", "Disciplina não encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("DISCIPLINA_ERROR", e.getMessage());
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        try {
            Disciplina disciplina = disciplinaService.findByCodigo(codigo);
            if (disciplina != null) {
                return ResponseEntity.ok(convertToDTO(disciplina));
            }
            return createErrorResponse("NOT_FOUND", "Disciplina não encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("CODIGO_ERROR", e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> criarDisciplina(@RequestBody DisciplinaDTO disciplinaDTO) {
        try {
            // Validações
            if (disciplinaDTO.getCodigo() == null || disciplinaDTO.getCodigo().trim().isEmpty()) {
                return createErrorResponse("VALIDATION_ERROR", "Código é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (disciplinaDTO.getNome() == null || disciplinaDTO.getNome().trim().isEmpty()) {
                return createErrorResponse("VALIDATION_ERROR", "Nome é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (disciplinaService.existsByCodigo(disciplinaDTO.getCodigo())) {
                return createErrorResponse("DUPLICATE_ERROR", "Código já existe", HttpStatus.CONFLICT);
            }

            // Criar disciplina
            Disciplina disciplina = new Disciplina();
            disciplina.setCodigo(disciplinaDTO.getCodigo());
            disciplina.setNome(disciplinaDTO.getNome());
            disciplina.setDescricao(disciplinaDTO.getDescricao());
            disciplina.setCargaHoraria(disciplinaDTO.getCargaHoraria());
            disciplina.setAtiva(true);

            Disciplina disciplinaSalva = disciplinaService.save(disciplina);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Disciplina criada com sucesso");
            response.put("data", convertToDTO(disciplinaSalva));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse("CREATE_ERROR", e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDisciplina(@PathVariable Long id, @RequestBody DisciplinaDTO disciplinaDTO) {
        try {
            Disciplina disciplina = disciplinaService.findById(id);
            if (disciplina == null) {
                return createErrorResponse("NOT_FOUND", "Disciplina não encontrada", HttpStatus.NOT_FOUND);
            }

            // Verificar se está tentando mudar o código para um que já existe
            if (disciplinaDTO.getCodigo() != null &&
                    !disciplinaDTO.getCodigo().equals(disciplina.getCodigo()) &&
                    disciplinaService.existsByCodigo(disciplinaDTO.getCodigo())) {
                return createErrorResponse("DUPLICATE_ERROR", "Código já existe", HttpStatus.CONFLICT);
            }

            // Atualizar dados
            if (disciplinaDTO.getCodigo() != null) {
                disciplina.setCodigo(disciplinaDTO.getCodigo());
            }
            if (disciplinaDTO.getNome() != null) {
                disciplina.setNome(disciplinaDTO.getNome());
            }
            if (disciplinaDTO.getDescricao() != null) {
                disciplina.setDescricao(disciplinaDTO.getDescricao());
            }
            if (disciplinaDTO.getCargaHoraria() != null) {
                disciplina.setCargaHoraria(disciplinaDTO.getCargaHoraria());
            }
            if (disciplinaDTO.getAtiva() != null) {
                disciplina.setAtiva(disciplinaDTO.getAtiva());
            }

            Disciplina disciplinaAtualizada = disciplinaService.save(disciplina);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Disciplina atualizada com sucesso");
            response.put("data", convertToDTO(disciplinaAtualizada));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("UPDATE_ERROR", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDisciplina(@PathVariable Long id) {
        try {
            Disciplina disciplina = disciplinaService.findById(id);
            if (disciplina == null) {
                return createErrorResponse("NOT_FOUND", "Disciplina não encontrada", HttpStatus.NOT_FOUND);
            }

            disciplinaService.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Disciplina desativada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("DELETE_ERROR", e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Disciplina disciplina = disciplinaService.findById(id);
            if (disciplina == null) {
                return createErrorResponse("NOT_FOUND", "Disciplina não encontrada", HttpStatus.NOT_FOUND);
            }

            Boolean ativa = Boolean.valueOf(request.get("ativa"));
            disciplina.setAtiva(ativa);
            disciplinaService.save(disciplina);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", ativa ? "Disciplina ativada com sucesso" : "Disciplina desativada com sucesso");
            response.put("data", convertToDTO(disciplina));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("STATUS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/ativas")
    public ResponseEntity<?> listarDisciplinasAtivas() {
        try {
            List<Disciplina> disciplinas = disciplinaService.findAll();

            List<Map<String, Object>> disciplinasDTO = disciplinas.stream()
                    .filter(Disciplina::getAtiva)
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(disciplinasDTO);
        } catch (Exception e) {
            return createErrorResponse("DISCIPLINAS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/{id}/turmas")
    public ResponseEntity<?> listarTurmasDaDisciplina(@PathVariable Long id) {
        try {
            Disciplina disciplina = disciplinaService.findById(id);
            if (disciplina == null) {
                return createErrorResponse("NOT_FOUND", "Disciplina não encontrada", HttpStatus.NOT_FOUND);
            }

            List<Map<String, Object>> turmas = disciplina.getTurmas().stream()
                    .map(turma -> {
                        Map<String, Object> turmaDTO = new HashMap<>();
                        turmaDTO.put("id", turma.getId());
                        turmaDTO.put("nome", turma.getNome());
                        turmaDTO.put("periodo", turma.getPeriodo());
                        turmaDTO.put("ano", turma.getAno());
                        turmaDTO.put("ativa", turma.getAtiva());
                        if (turma.getUsuario() != null) {
                            turmaDTO.put("professorNome", turma.getUsuario().getNome());
                        }
                        return turmaDTO;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("disciplina", convertToDTO(disciplina));
            response.put("turmas", turmas);
            response.put("totalTurmas", turmas.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("TURMAS_ERROR", e.getMessage());
        }
    }

    private Map<String, Object> convertToDTO(Disciplina disciplina) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", disciplina.getId());
        dto.put("codigo", disciplina.getCodigo());
        dto.put("nome", disciplina.getNome());
        dto.put("descricao", disciplina.getDescricao());
        dto.put("cargaHoraria", disciplina.getCargaHoraria());
        dto.put("ativa", disciplina.getAtiva());

        if (disciplina.getTurmas() != null) {
            dto.put("totalTurmas", disciplina.getTurmas().size());
            dto.put("possuiTurmas", !disciplina.getTurmas().isEmpty());
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