package br.unialfa.hackathon.api;

import br.unialfa.hackathon.dto.CorrecaoRequest;
import br.unialfa.hackathon.model.Resultado;
import br.unialfa.hackathon.service.CorrecaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/correcao")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CorrecaoApiController {

    private final CorrecaoService correcaoService;

    @PostMapping("/corrigir")
    public ResponseEntity<?> corrigirProva(@RequestBody Map<String, Object> request) {
        try {
            // Extrair dados do request
            Long provaId = Long.valueOf(request.get("provaId").toString());
            Long alunoId = Long.valueOf(request.get("alunoId").toString());
            List<Map<String, Object>> respostasMap = (List<Map<String, Object>>) request.get("respostas");

            // Converter respostas do formato Flutter para o formato esperado
            List<String> respostas = new ArrayList<>();

            // Ordenar as respostas pelo número da questão
            respostasMap.sort((a, b) -> {
                int numA = Integer.parseInt(a.get("questaoNumero").toString());
                int numB = Integer.parseInt(b.get("questaoNumero").toString());
                return Integer.compare(numA, numB);
            });

            // Extrair apenas as alternativas selecionadas
            for (Map<String, Object> resp : respostasMap) {
                respostas.add(resp.get("alternativaSelecionada").toString());
            }

            // Criar CorrecaoRequest
            CorrecaoRequest correcaoRequest = new CorrecaoRequest();
            correcaoRequest.setProvaId(provaId);
            correcaoRequest.setAlunoId(alunoId);
            correcaoRequest.setRespostas(respostas);

            // Processar correção
            Resultado resultado = correcaoService.corrigirProva(correcaoRequest);

            // Criar response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Prova corrigida com sucesso!");
            response.put("resultadoId", resultado.getId());
            response.put("alunoId", resultado.getAluno().getId());
            response.put("alunoNome", resultado.getAluno().getNome());
            response.put("provaId", resultado.getProva().getId());
            response.put("provaTitulo", resultado.getProva().getTitulo());
            response.put("nota", resultado.getNota());
            response.put("acertos", resultado.getAcertos());
            response.put("erros", resultado.getErros());
            response.put("totalQuestoes", resultado.getProva().getNumeroQuestoes());
            response.put("aprovado", resultado.getNota() >= 6.0);
            response.put("dataCorrecao", resultado.getDataCorrecao());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("já foi corrigida")) {
                return createErrorResponse("DUPLICATE_ERROR", e.getMessage(), HttpStatus.CONFLICT);
            }
            return createErrorResponse("CORRECAO_ERROR", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("INTERNAL_ERROR", "Erro ao corrigir prova: " + e.getMessage());
        }
    }

    @GetMapping("/resultados/aluno/{alunoId}")
    public ResponseEntity<?> buscarResultadosAluno(@PathVariable Long alunoId) {
        try {
            List<Resultado> resultados = correcaoService.findResultadosByAluno(alunoId);

            List<Map<String, Object>> resultadosDTO = resultados.stream()
                    .map(this::convertToResultadoDTO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", resultadosDTO.size());
            response.put("data", resultadosDTO);

            if (!resultados.isEmpty()) {
                double mediaGeral = resultados.stream()
                        .mapToDouble(Resultado::getNota)
                        .average()
                        .orElse(0.0);
                response.put("mediaGeral", mediaGeral);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("RESULTADOS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/resultados/prova/{provaId}")
    public ResponseEntity<?> buscarResultadosProva(@PathVariable Long provaId) {
        try {
            List<Resultado> resultados = correcaoService.findResultadosByProva(provaId);

            List<Map<String, Object>> resultadosDTO = resultados.stream()
                    .map(this::convertToResultadoDTO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", resultadosDTO.size());
            response.put("data", resultadosDTO);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("RESULTADOS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/estatisticas/{provaId}")
    public ResponseEntity<?> buscarEstatisticas(@PathVariable Long provaId) {
        try {
            var estatisticas = correcaoService.calcularEstatisticas(provaId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", estatisticas);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("ESTATISTICAS_ERROR", e.getMessage());
        }
    }

    @GetMapping("/resultado/{resultadoId}")
    public ResponseEntity<?> buscarResultadoDetalhado(@PathVariable Long resultadoId) {
        try {
            // TODO: Implementar busca detalhada do resultado com respostas
            return createErrorResponse("NOT_IMPLEMENTED", "Endpoint ainda não implementado", HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            return createErrorResponse("RESULTADO_ERROR", e.getMessage());
        }
    }

    private Map<String, Object> convertToResultadoDTO(Resultado resultado) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", resultado.getId());
        dto.put("nota", resultado.getNota());
        dto.put("acertos", resultado.getAcertos());
        dto.put("erros", resultado.getErros());
        dto.put("dataCorrecao", resultado.getDataCorrecao());
        dto.put("aprovado", resultado.getNota() >= 6.0);
        dto.put("observacoes", resultado.getObservacoes());

        if (resultado.getAluno() != null) {
            dto.put("alunoId", resultado.getAluno().getId());
            dto.put("alunoNome", resultado.getAluno().getNome());
            dto.put("alunoMatricula", resultado.getAluno().getMatricula());
        }

        if (resultado.getProva() != null) {
            dto.put("provaId", resultado.getProva().getId());
            dto.put("provaTitulo", resultado.getProva().getTitulo());
            dto.put("provaData", resultado.getProva().getDataAplicacao());
            dto.put("totalQuestoes", resultado.getProva().getNumeroQuestoes());
        }

        // Calcular percentual de acertos
        if (resultado.getProva() != null && resultado.getProva().getNumeroQuestoes() > 0) {
            double percentualAcertos = (resultado.getAcertos() * 100.0) / resultado.getProva().getNumeroQuestoes();
            dto.put("percentualAcertos", percentualAcertos);
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