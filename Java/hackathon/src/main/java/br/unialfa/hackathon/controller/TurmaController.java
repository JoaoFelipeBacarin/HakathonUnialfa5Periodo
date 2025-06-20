package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Turma;
import br.unialfa.hackathon.service.TurmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping("/nova")
    public String formNovaTurma(Model model) {
        model.addAttribute("turma", new Turma());
        return "turma/form";
    }

    @PostMapping("/salvar")
    public String salvarTurma(@ModelAttribute Turma turma) {
        turmaService.salvar(turma);
        return "redirect:/turmas/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("turmas", turmaService.listarTodas());
        return "turma/lista";
    }
}
