package br.unialfa.hackathon.api;

import br.unialfa.hackathon.dto.CorrecaoRequest;
import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Resultado;
import br.unialfa.hackathon.service.AlunoService;
import br.unialfa.hackathon.service.CorrecaoService;
import br.unialfa.hackathon.service.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gabarito")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GabaritoApiController {

    private final AlunoService alunoService;
    private final ProvaService provaService;
    private final CorrecaoService correcaoService;

    @GetMapping("/alunos")
    public ResponseEntity<List<Aluno>> listarAlunos() {
        return ResponseEntity.ok(alunoService.findAll());
    }

    @GetMapping("/alunos/{id}")
    public ResponseEntity<Aluno> buscarAluno(@PathVariable Long id) {
        Aluno aluno = alunoService.findById(id);
        if (aluno != null) {
            return ResponseEntity.ok(aluno);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/alunos/matricula/{matricula}")
    public ResponseEntity<Aluno> buscarAlunoPorMatricula(@PathVariable String matricula) {
        Aluno aluno = alunoService.findByMatricula(matricula);
        if (aluno != null) {
            return ResponseEntity.ok(aluno);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/provas")
    public ResponseEntity<List<Prova>> listarProvas() {
        return ResponseEntity.ok(provaService.findAll());
    }

    @GetMapping("/provas/{id}")
    public ResponseEntity<Prova> buscarProva(@PathVariable Long id) {
        Prova prova = provaService.findById(id);
        if (prova != null) {
            return ResponseEntity.ok(prova);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/corrigir")
    public ResponseEntity<?> corrigirProva(@RequestBody CorrecaoRequest request) {
        try {
            Resultado resultado = correcaoService.corrigirProva(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Prova corrigida com sucesso!");
            response.put("resultado", resultado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/resultados/aluno/{alunoId}")
    public ResponseEntity<List<Resultado>> buscarResultadosAluno(@PathVariable Long alunoId) {
        List<Resultado> resultados = correcaoService.findResultadosByAluno(alunoId);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/resultados/prova/{provaId}")
    public ResponseEntity<List<Resultado>> buscarResultadosProva(@PathVariable Long provaId) {
        List<Resultado> resultados = correcaoService.findResultadosByProva(provaId);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/estatisticas/{provaId}")
    public ResponseEntity<?> buscarEstatisticas(@PathVariable Long provaId) {
        try {
            return ResponseEntity.ok(correcaoService.calcularEstatisticas(provaId));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}