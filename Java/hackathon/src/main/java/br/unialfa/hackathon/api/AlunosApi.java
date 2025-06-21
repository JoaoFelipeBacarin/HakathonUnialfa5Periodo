package br.unialfa.hackathon.api;

import br.unialfa.hackathon.dto.AlunoResponse;
import br.unialfa.hackathon.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunosApi {

    private final AlunoService alunoService;

    @GetMapping
    public ResponseEntity<List<AlunoResponse>> listarAlunos() {
        List<AlunoResponse> alunos = alunoService.listarAlunosComDetalhes();
        return ResponseEntity.ok(alunos);
    }
}