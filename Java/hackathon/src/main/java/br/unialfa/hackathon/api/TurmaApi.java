package br.unialfa.hackathon.api;

import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.service.TurmaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaApi {

    private final TurmaService turmaService;

    public TurmaApi(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping
    public List<Turma> listarTurmas() {
        return turmaService.listarTodas();
    }
}
