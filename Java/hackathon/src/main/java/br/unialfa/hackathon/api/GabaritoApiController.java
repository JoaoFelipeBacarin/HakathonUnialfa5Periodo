//package br.unialfa.hackathon.api;
//
//import br.unialfa.hackathon.dto.*;
//import br.unialfa.hackathon.model.*;
//import br.unialfa.hackathon.service.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/gabarito")
//@CrossOrigin(origins = "*")
//@RequiredArgsConstructor
//public class GabaritoApiController {
//
//    private final AlunoService alunoService;
//    private final ProvaService provaService;
//    private final CorrecaoService correcaoService;
//    private final QuestaoService questaoService;
//    private final TurmaService turmaService;
//
//    // ========== TURMAS ==========
//
//    @GetMapping("/turmas")
//    public ResponseEntity<?> listarTurmas() {
//        try {
//            List<Turma> turmas = turmaService.findAll();
//
//            List<Map<String, Object>> turmasDTO = turmas.stream()
//                    .map(turma -> {
//                        Map<String, Object> dto = new HashMap<>();
//                        dto.put("id", turma.getId());
//                        dto.put("nome", turma.getNome());
//                        dto.put("ano", turma.getAno());
//                        dto.put("periodo", turma.getPeriodo());
//                        if (turma.getDisciplina() != null) {
//                            dto.put("disciplinaNome", turma.getDisciplina().getNome());
//                        }
//                        if (turma.getUsuario() != null) {
//                            dto.put("professorNome", turma.getUsuario().getNome());
//                        }
//                        return dto;
//                    })
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(turmasDTO);
//        } catch (Exception e) {
//            return createErrorResponse("TURMAS_ERROR", e.getMessage());
//        }
//    }
//
//    // ========== ALUNOS ==========
//
//    @GetMapping("/alunos")
//    public ResponseEntity<?> listarAlunos(@RequestParam(required = false) String search) {
//        try {
//            List<Aluno> alunos;
//
//            if (search != null && !search.isEmpty()) {
//                alunos = alunoService.findByNome(search);
//            } else {
//                alunos = alunoService.findAll();
//            }
//
//            List<Map<String, Object>> alunosDTO = alunos.stream()
//                    .map(aluno -> {
//                        Map<String, Object> dto = new HashMap<>();
//                        dto.put("id", aluno.getId());
//                        dto.put("nome", aluno.getNome());
//                        dto.put("email", aluno.getEmail());
//                        dto.put("matricula", aluno.getMatricula());
//
//                        // Adicionar turma como string e turmaId
//                        if (aluno.getTurmas() != null && !aluno.getTurmas().isEmpty()) {
//                            Turma primeiraTurma = aluno.getTurmas().get(0);
//                            dto.put("turma", primeiraTurma.getNome());
//                            dto.put("turmaId", primeiraTurma.getId());
//                        } else {
//                            dto.put("turma", "Sem turma");
//                            dto.put("turmaId", 0);
//                        }
//
//                        return dto;
//                    })
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(alunosDTO);
//        } catch (Exception e) {
//            return createErrorResponse("ALUNOS_ERROR", e.getMessage());
//        }
//    }
//
//    @GetMapping("/alunos/{id}")
//    public ResponseEntity<?> buscarAluno(@PathVariable Long id) {
//        try {
//            Aluno aluno = alunoService.findById(id);
//            if (aluno != null) {
//                Map<String, Object> dto = new HashMap<>();
//                dto.put("id", aluno.getId());
//                dto.put("nome", aluno.getNome());
//                dto.put("email", aluno.getEmail());
//                dto.put("matricula", aluno.getMatricula());
//
//                if (aluno.getTurmas() != null && !aluno.getTurmas().isEmpty()) {
//                    Turma primeiraTurma = aluno.getTurmas().get(0);
//                    dto.put("turma", primeiraTurma.getNome());
//                    dto.put("turmaId", primeiraTurma.getId());
//                } else {
//                    dto.put("turma", "Sem turma");
//                    dto.put("turmaId", 0);
//                }
//
//                return ResponseEntity.ok(dto);
//            }
//            return createErrorResponse("NOT_FOUND", "Aluno não encontrado", HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return createErrorResponse("ALUNO_ERROR", e.getMessage());
//        }
//    }
//
//    @GetMapping("/alunos/matricula/{matricula}")
//    public ResponseEntity<?> buscarAlunoPorMatricula(@PathVariable String matricula) {
//        try {
//            Aluno aluno = alunoService.findByMatricula(matricula);
//            if (aluno != null) {
//                Map<String, Object> dto = new HashMap<>();
//                dto.put("id", aluno.getId());
//                dto.put("nome", aluno.getNome());
//                dto.put("email", aluno.getEmail());
//                dto.put("matricula", aluno.getMatricula());
//
//                if (aluno.getTurmas() != null && !aluno.getTurmas().isEmpty()) {
//                    Turma primeiraTurma = aluno.getTurmas().get(0);
//                    dto.put("turma", primeiraTurma.getNome());
//                    dto.put("turmaId", primeiraTurma.getId());
//                } else {
//                    dto.put("turma", "Sem turma");
//                    dto.put("turmaId", 0);
//                }
//
//                return ResponseEntity.ok(dto);
//            }
//            return createErrorResponse("NOT_FOUND", "Aluno não encontrado com esta matrícula", HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return createErrorResponse("MATRICULA_ERROR", e.getMessage());
//        }
//    }
//
//    // ========== PROVAS ==========
//
//    @GetMapping("/provas")
//    public ResponseEntity<?> listarProvas(@RequestParam(required = false) Long turmaId,
//                                          @RequestParam(required = false) Boolean ativas) {
//        try {
//            List<Prova> provas;
//
//            if (turmaId != null) {
//                provas = provaService.findByTurmaId(turmaId);
//            } else {
//                provas = provaService.findAll();
//            }
//
//            if (ativas != null && ativas) {
//                provas = provas.stream()
//                        .filter(Prova::getAtiva)
//                        .collect(Collectors.toList());
//            }
//
//            List<Map<String, Object>> provasDTO = provas.stream()
//                    .map(prova -> {
//                        Map<String, Object> dto = new HashMap<>();
//                        dto.put("id", prova.getId());
//                        dto.put("nome", prova.getTitulo());
//                        dto.put("disciplina", prova.getTurma() != null && prova.getTurma().getDisciplina() != null
//                                ? prova.getTurma().getDisciplina().getNome()
//                                : "");
//                        dto.put("turmaId", prova.getTurma() != null ? prova.getTurma().getId() : 0);
//                        dto.put("dataAplicacao", prova.getDataAplicacao());
//                        dto.put("numeroQuestoes", prova.getNumeroQuestoes());
//                        return dto;
//                    })
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(provasDTO);
//        } catch (Exception e) {
//            return createErrorResponse("PROVAS_ERROR", e.getMessage());
//        }
//    }
//
//    @GetMapping("/provas/turma/{turmaId}")
//    public ResponseEntity<?> listarProvasPorTurma(@PathVariable Long turmaId) {
//        try {
//            List<Prova> provas = provaService.findByTurmaId(turmaId);
//
//            List<Map<String, Object>> provasDTO = provas.stream()
//                    .map(prova -> {
//                        Map<String, Object> dto = new HashMap<>();
//                        dto.put("id", prova.getId());
//                        dto.put("nome", prova.getTitulo());
//                        dto.put("disciplina", prova.getTurma() != null && prova.getTurma().getDisciplina() != null
//                                ? prova.getTurma().getDisciplina().getNome()
//                                : "");
//                        dto.put("turmaId", turmaId);
//                        dto.put("dataAplicacao", prova.getDataAplicacao());
//                        dto.put("numeroQuestoes", prova.getNumeroQuestoes());
//                        return dto;
//                    })
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(provasDTO);
//        } catch (Exception e) {
//            return createErrorResponse("PROVAS_TURMA_ERROR", e.getMessage());
//        }
//    }
//
//    @GetMapping("/provas/{id}")
//    public ResponseEntity<?> buscarProva(@PathVariable Long id) {
//        try {
//            Prova prova = provaService.findById(id);
//            if (prova != null) {
//                Map<String, Object> dto = new HashMap<>();
//                dto.put("id", prova.getId());
//                dto.put("nome", prova.getTitulo());
//                dto.put("disciplina", prova.getTurma() != null && prova.getTurma().getDisciplina() != null
//                        ? prova.getTurma().getDisciplina().getNome()
//                        : "");
//                dto.put("turmaId", prova.getTurma() != null ? prova.getTurma().getId() : 0);
//                dto.put("dataAplicacao", prova.getDataAplicacao());
//                dto.put("numeroQuestoes", prova.getNumeroQuestoes());
//                dto.put("valorTotal", prova.getValorTotal());
//
//                return ResponseEntity.ok(dto);
//            }
//            return createErrorResponse("NOT_FOUND", "Prova não encontrada", HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            return createErrorResponse("PROVA_ERROR", e.getMessage());
//        }
//    }
//
//    // ========== QUESTÕES ==========
//
//    @GetMapping("/questoes/prova/{provaId}")
//    public ResponseEntity<?> buscarQuestoesPorProva(@PathVariable Long provaId) {
//        try {
//            List<Questao> questoes = questaoService.findByProvaId(provaId);
//
//            List<Map<String, Object>> questoesDTO = questoes.stream()
//                    .map(questao -> {
//                        Map<String, Object> dto = new HashMap<>();
//                        dto.put("numero", questao.getNumero());
//
//                        // Criar mapa de alternativas
//                        Map<String, String> alternativas = new HashMap<>();
//                        alternativas.put("A", "Alternativa A");
//                        alternativas.put("B", "Alternativa B");
//                        alternativas.put("C", "Alternativa C");
//                        alternativas.put("D", "Alternativa D");
//                        alternativas.put("E", "Alternativa E");
//                        dto.put("alternativas", alternativas);
//
//                        // Para visualização do gabarito
//                        dto.put("respostaSelecionada", questao.getRespostaCorreta().name());
//
//                        return dto;
//                    })
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(questoesDTO);
//        } catch (Exception e) {
//            return createErrorResponse("QUESTOES_ERROR", e.getMessage());
//        }
//    }
//
//    // ========== CORREÇÃO ==========
//
//    @PostMapping("/corrigir")
//    public ResponseEntity<?> corrigirProva(@RequestBody Map<String, Object> request) {
//        try {
//            // Extrair dados do request
//            Long provaId = Long.valueOf(request.get("provaId").toString());
//            Long alunoId = Long.valueOf(request.get("alunoId").toString());
//            List<Map<String, Object>> respostasMap = (List<Map<String, Object>>) request.get("respostas");
//
//            // Converter respostas do formato Flutter para o formato esperado pela API
//            List<String> respostas = new ArrayList<>();
//
//            // Ordenar as respostas pelo número da questão
//            respostasMap.sort((a, b) -> {
//                int numA = Integer.parseInt(a.get("questaoNumero").toString());
//                int numB = Integer.parseInt(b.get("questaoNumero").toString());
//                return Integer.compare(numA, numB);
//            });
//
//            // Extrair apenas as alternativas selecionadas
//            for (Map<String, Object> resp : respostasMap) {
//                respostas.add(resp.get("alternativaSelecionada").toString());
//            }
//
//            // Criar CorrecaoRequest
//            CorrecaoRequest correcaoRequest = new CorrecaoRequest();
//            correcaoRequest.setProvaId(provaId);
//            correcaoRequest.setAlunoId(alunoId);
//            correcaoRequest.setRespostas(respostas);
//
//            // Processar correção
//            Resultado resultado = correcaoService.corrigirProva(correcaoRequest);
//
//            // Criar response
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Prova corrigida com sucesso!");
//            response.put("resultadoId", resultado.getId());
//            response.put("alunoId", resultado.getAluno().getId());
//            response.put("alunoNome", resultado.getAluno().getNome());
//            response.put("provaId", resultado.getProva().getId());
//            response.put("provaTitulo", resultado.getProva().getTitulo());
//            response.put("nota", resultado.getNota());
//            response.put("acertos", resultado.getAcertos());
//            response.put("erros", resultado.getErros());
//            response.put("totalQuestoes", resultado.getProva().getNumeroQuestoes());
//            response.put("aprovado", resultado.getNota() >= 6.0);
//            response.put("dataCorrecao", resultado.getDataCorrecao());
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            if (e.getMessage().contains("já foi corrigida")) {
//                return createErrorResponse("DUPLICATE_ERROR", e.getMessage(), HttpStatus.CONFLICT);
//            }
//            return createErrorResponse("CORRECAO_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return createErrorResponse("INTERNAL_ERROR", "Erro ao corrigir prova: " + e.getMessage());
//        }
//    }
//
//    // ========== RESULTADOS ==========
//
//    @GetMapping("/resultados/aluno/{alunoId}")
//    public ResponseEntity<?> buscarResultadosAluno(@PathVariable Long alunoId) {
//        try {
//            List<Resultado> resultados = correcaoService.findResultadosByAluno(alunoId);
//
//            List<Map<String, Object>> resultadosDTO = resultados.stream()
//                    .map(this::convertToResultadoDTO)
//                    .collect(Collectors.toList());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("count", resultadosDTO.size());
//            response.put("data", resultadosDTO);
//
//            if (!resultados.isEmpty()) {
//                double mediaGeral = resultados.stream()
//                        .mapToDouble(Resultado::getNota)
//                        .average()
//                        .orElse(0.0);
//                response.put("mediaGeral", mediaGeral);
//            }
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return createErrorResponse("RESULTADOS_ERROR", e.getMessage());
//        }
//    }
//
//    @GetMapping("/resultados/prova/{provaId}")
//    public ResponseEntity<?> buscarResultadosProva(@PathVariable Long provaId) {
//        try {
//            List<Resultado> resultados = correcaoService.findResultadosByProva(provaId);
//
//            List<Map<String, Object>> resultadosDTO = resultados.stream()
//                    .map(this::convertToResultadoDTO)
//                    .collect(Collectors.toList());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("count", resultadosDTO.size());
//            response.put("data", resultadosDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return createErrorResponse("RESULTADOS_ERROR", e.getMessage());
//        }
//    }
//
//    @GetMapping("/estatisticas/{provaId}")
//    public ResponseEntity<?> buscarEstatisticas(@PathVariable Long provaId) {
//        try {
//            var estatisticas = correcaoService.calcularEstatisticas(provaId);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("data", estatisticas);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return createErrorResponse("ESTATISTICAS_ERROR", e.getMessage());
//        }
//    }
//
//    // ========== MÉTODOS AUXILIARES ==========
//
//    private Map<String, Object> convertToResultadoDTO(Resultado resultado) {
//        Map<String, Object> dto = new HashMap<>();
//        dto.put("id", resultado.getId());
//        dto.put("nota", resultado.getNota());
//        dto.put("acertos", resultado.getAcertos());
//        dto.put("erros", resultado.getErros());
//        dto.put("dataCorrecao", resultado.getDataCorrecao());
//        dto.put("aprovado", resultado.getNota() >= 6.0);
//
//        if (resultado.getAluno() != null) {
//            dto.put("alunoNome", resultado.getAluno().getNome());
//            dto.put("alunoMatricula", resultado.getAluno().getMatricula());
//        }
//
//        if (resultado.getProva() != null) {
//            dto.put("provaTitulo", resultado.getProva().getTitulo());
//            dto.put("provaData", resultado.getProva().getDataAplicacao());
//        }
//
//        return dto;
//    }
//
//    private ResponseEntity<?> createErrorResponse(String errorCode, String message) {
//        return createErrorResponse(errorCode, message, HttpStatus.BAD_REQUEST);
//    }
//
//    private ResponseEntity<?> createErrorResponse(String errorCode, String message, HttpStatus status) {
//        Map<String, Object> error = new HashMap<>();
//        error.put("success", false);
//        error.put("error", errorCode);
//        error.put("message", message);
//        error.put("timestamp", System.currentTimeMillis());
//        return ResponseEntity.status(status).body(error);
//    }
//}