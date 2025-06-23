package br.unialfa.hackathon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import br.unialfa.hackathon.service.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/turma")


public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping("/turmas")
    public String listarTurmas(Model model) {
        model.addAttribute("turmas", turmaService.findAll());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Turmas", "")
        ));
        return "professor/turmas";
    }

    //Método auxiliar para criar breadcrumb
    private Map<String, String> criarBreadcrumb(String label, String url) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put("label", label);
        breadcrumb.put("url", url);
        return breadcrumb;
    }
}