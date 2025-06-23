package br.unialfa.hackathon.api;

import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AlunoApiController {

    private final AlunoService alunoService;

    @GetMapping
    public ResponseEntity<?> listarAlunos(@RequestParam(required = false) String search) {
        try {
            List<Aluno> alunos;

            if (search != null && !search.isEmpty()) {
                alunos = alunoService.findByNome(search);
            } else {
                alunos = alunoService.findAll();
            }

            List<Map<String, Object>> alunosDTO = alunos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(alunosDTO);
        } catch (Exception e) {
            return createErrorResponse("ALUNOS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAluno(@PathVariable Long id) {
        try {
            Aluno aluno = alunoService.findById(id);
            if (aluno != null) {
                return ResponseEntity.ok(convertToDTO(aluno));
            }
            return createErrorResponse("NOT_FOUND", "Aluno não encontrado", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("ALUNO_ERROR", e.getMessage());
        }
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<?> buscarAlunoPorMatricula(@PathVariable String matricula) {
        try {
            Aluno aluno = alunoService.findByMatricula(matricula);
            if (aluno != null) {
                return ResponseEntity.ok(convertToDTO(aluno));
            }
            return createErrorResponse("NOT_FOUND", "Aluno não encontrado com esta matrícula", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("MATRICULA_ERROR", e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> criarAluno(@RequestBody Map<String, Object> request) {
        try {
            String matricula = (String) request.get("matricula");
            String nome = (String) request.get("nome");
            String email = (String) request.get("email");
            String telefone = (String) request.get("telefone");
            String cpf = (String) request.get("cpf");

            // Validações
            if (matricula == null || matricula.trim().isEmpty()) {
                return createErrorResponse("VALIDATION_ERROR", "Matrícula é obrigatória", HttpStatus.BAD_REQUEST);
            }

            if (nome == null || nome.trim().isEmpty()) {
                return createErrorResponse("VALIDATION_ERROR", "Nome é obrigatório", HttpStatus.BAD_REQUEST);
            }

            if (alunoService.existsByMatricula(matricula)) {
                return createErrorResponse("DUPLICATE_ERROR", "Matrícula já existe", HttpStatus.CONFLICT);
            }

            // Criar aluno
            Aluno aluno = new Aluno();
            aluno.setMatricula(matricula);
            aluno.setNome(nome);
            aluno.setEmail(email);
            aluno.setTelefone(telefone);
            aluno.setCpf(cpf);
            aluno.setAtivo(true);

            Aluno alunoSalvo = alunoService.save(aluno);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluno criado com sucesso");
            response.put("data", convertToDTO(alunoSalvo));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse("CREATE_ERROR", e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAluno(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Aluno aluno = alunoService.findById(id);
            if (aluno == null) {
                return createErrorResponse("NOT_FOUND", "Aluno não encontrado", HttpStatus.NOT_FOUND);
            }

            // Atualizar dados
            String nome = (String) request.get("nome");
            String email = (String) request.get("email");
            String telefone = (String) request.get("telefone");
            String cpf = (String) request.get("cpf");
            Boolean ativo = (Boolean) request.get("ativo");

            if (nome != null) aluno.setNome(nome);
            if (email != null) aluno.setEmail(email);
            if (telefone != null) aluno.setTelefone(telefone);
            if (cpf != null) aluno.setCpf(cpf);
            if (ativo != null) aluno.setAtivo(ativo);

            Aluno alunoAtualizado = alunoService.save(aluno);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluno atualizado com sucesso");
            response.put("data", convertToDTO(alunoAtualizado));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("UPDATE_ERROR", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarAluno(@PathVariable Long id) {
        try {
            Aluno aluno = alunoService.findById(id);
            if (aluno == null) {
                return createErrorResponse("NOT_FOUND", "Aluno não encontrado", HttpStatus.NOT_FOUND);
            }

            alunoService.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Aluno desativado com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("DELETE_ERROR", e.getMessage());
        }
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<?> listarAlunosPorTurma(@PathVariable Long turmaId) {
        try {
            List<Aluno> alunos = alunoService.findByTurmaId(turmaId);

            List<Map<String, Object>> alunosDTO = alunos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(alunosDTO);
        } catch (Exception e) {
            return createErrorResponse("TURMA_ERROR", e.getMessage());
        }
    }

    private Map<String, Object> convertToDTO(Aluno aluno) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", aluno.getId());
        dto.put("matricula", aluno.getMatricula());
        dto.put("nome", aluno.getNome());
        dto.put("email", aluno.getEmail());
        dto.put("telefone", aluno.getTelefone());
        dto.put("cpf", aluno.getCpf());
        dto.put("ativo", aluno.getAtivo());

        // Adicionar informações da turma
        if (aluno.getTurmas() != null && !aluno.getTurmas().isEmpty()) {
            Turma primeiraTurma = aluno.getTurmas().get(0);
            dto.put("turma", primeiraTurma.getNome());
            dto.put("turmaId", primeiraTurma.getId());

            // Lista de todas as turmas
            List<Map<String, Object>> turmas = aluno.getTurmas().stream()
                    .map(turma -> {
                        Map<String, Object> turmaInfo = new HashMap<>();
                        turmaInfo.put("id", turma.getId());
                        turmaInfo.put("nome", turma.getNome());
                        return turmaInfo;
                    })
                    .collect(Collectors.toList());
            dto.put("turmas", turmas);
        } else {
            dto.put("turma", "Sem turma");
            dto.put("turmaId", 0);
            dto.put("turmas", List.of());
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