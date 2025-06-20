package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.service.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/provas")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;

    @GetMapping("/nova")
    public String formNovaProva(Model model) {
        model.addAttribute("prova", new Prova());
        return "prova/form";
    }

    @PostMapping("/salvar")
    public String salvarProva(@ModelAttribute Prova prova) {
        provaService.salvar(prova);
        return "redirect:/provas/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("provas", provaService.listarTodas());
        return "prova/lista";
    }
}
