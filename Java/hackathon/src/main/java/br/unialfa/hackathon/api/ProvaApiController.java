package br.unialfa.hackathon.api;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Questao;
import br.unialfa.hackathon.service.ProvaService;
import br.unialfa.hackathon.service.QuestaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/provas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProvaApiController {

    private final ProvaService provaService;
    private final QuestaoService questaoService;

    @GetMapping
    public ResponseEntity<?> listarProvas(@RequestParam(required = false) Long turmaId,
                                          @RequestParam(required = false) Boolean ativas) {
        try {
            List<Prova> provas;

            if (turmaId != null) {
                provas = provaService.findByTurmaId(turmaId);
            } else {
                provas = provaService.findAll();
            }

            if (ativas != null && ativas) {
                provas = provas.stream()
                        .filter(Prova::getAtiva)
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> provasDTO = provas.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(provasDTO);
        } catch (Exception e) {
            return createErrorResponse("PROVAS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarProva(@PathVariable Long id) {
        try {
            Prova prova = provaService.findById(id);
            if (prova != null) {
                return ResponseEntity.ok(convertToDetailedDTO(prova));
            }
            return createErrorResponse("NOT_FOUND", "Prova não encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("PROVA_ERROR", e.getMessage());
        }
    }

    @GetMapping("/turmas/{turmaId}/provas")
    public ResponseEntity<?> listarProvasPorTurma(@PathVariable Long turmaId,
                                                  @RequestParam(required = false) Boolean ativas) {
        try {
            List<Prova> provas = provaService.findByTurmaId(turmaId);

            if (ativas != null && ativas) {
                provas = provas.stream()
                        .filter(Prova::getAtiva)
                        .collect(Collectors.toList());
            }

            List<Map<String, Object>> provasDTO = provas.stream()
                    .map(this::convertToDTO) // Reutiliza seu método existente de conversão
                    .collect(Collectors.toList());

            return ResponseEntity.ok(provasDTO);
        } catch (Exception e) {
            // Em caso de erro, você pode querer um tratamento de erro mais específico
            // Por exemplo, um 404 se a turma não existir ou 500 para outros erros.
            return createErrorResponse("internal_error", "Erro interno do servidor ao buscar provas por turma: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{provaId}/questoes")
    public ResponseEntity<?> buscarQuestoesPorProva(@PathVariable Long provaId) {
        try {
            List<Questao> questoes = questaoService.findByProvaId(provaId);

            List<Map<String, Object>> questoesDTO = questoes.stream()
                    .map(this::convertQuestaoToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(questoesDTO);
        } catch (Exception e) {
            return createErrorResponse("QUESTOES_ERROR", e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> criarProva(@RequestBody Map<String, Object> request) {
        try {
            // TODO: Implementar criação de prova
            return createErrorResponse("NOT_IMPLEMENTED", "Endpoint ainda não implementado", HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return createErrorResponse("CREATE_ERROR", e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProva(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            // TODO: Implementar atualização de prova
            return createErrorResponse("NOT_IMPLEMENTED", "Endpoint ainda não implementado", HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return createErrorResponse("UPDATE_ERROR", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProva(@PathVariable Long id) {
        try {
            Prova prova = provaService.findById(id);
            if (prova == null) {
                return createErrorResponse("NOT_FOUND", "Prova não encontrada", HttpStatus.NOT_FOUND);
            }

            provaService.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Prova desativada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("DELETE_ERROR", e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatusProva(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Prova prova = provaService.findById(id);
            if (prova == null) {
                return createErrorResponse("NOT_FOUND", "Prova não encontrada", HttpStatus.NOT_FOUND);
            }

            Boolean ativa = Boolean.valueOf(request.get("ativa"));
            prova.setAtiva(ativa);
            provaService.save(prova);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", ativa ? "Prova ativada com sucesso" : "Prova desativada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("STATUS_ERROR", e.getMessage());
        }
    }

    private Map<String, Object> convertToDTO(Prova prova) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", prova.getId());
        dto.put("titulo", prova.getTitulo());
        dto.put("descricao", prova.getDescricao());
        dto.put("dataAplicacao", prova.getDataAplicacao());
        dto.put("numeroQuestoes", prova.getNumeroQuestoes());
        dto.put("valorTotal", prova.getValorTotal());
        dto.put("ativa", prova.getAtiva());

        if (prova.getTurma() != null) {
            dto.put("turmaId", prova.getTurma().getId());
            dto.put("turmaNome", prova.getTurma().getNome());

            if (prova.getTurma().getDisciplina() != null) {
                dto.put("disciplinaNome", prova.getTurma().getDisciplina().getNome());
            }
        }

        if (prova.getProfessor() != null) {
            dto.put("professorId", prova.getProfessor().getId());
            dto.put("professorNome", prova.getProfessor().getNome());
        }

        return dto;
    }

    private Map<String, Object> convertToDetailedDTO(Prova prova) {
        Map<String, Object> dto = convertToDTO(prova);
        dto.put("dataCriacao", prova.getDataCriacao());

        // Adicionar estatísticas se houver resultados
        if (prova.getResultados() != null && !prova.getResultados().isEmpty()) {
            dto.put("totalCorrecoes", prova.getResultados().size());
        }

        return dto;
    }

    private Map<String, Object> convertQuestaoToDTO(Questao questao) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", questao.getId());
        dto.put("numero", questao.getNumero());
        dto.put("peso", questao.getPeso());
        dto.put("enunciado", questao.getEnunciado());

        // Criar mapa de alternativas genérico
        Map<String, String> alternativas = new HashMap<>();
        alternativas.put("A", "Alternativa A");
        alternativas.put("B", "Alternativa B");
        alternativas.put("C", "Alternativa C");
        alternativas.put("D", "Alternativa D");
        alternativas.put("E", "Alternativa E");
        dto.put("alternativas", alternativas);

        // Para visualização do gabarito (apenas para professores/admin)
        dto.put("respostaCorreta", questao.getRespostaCorreta().name());

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