package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Prova;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/prova")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;
    private final TurmaService turmaService;
    private final UsuarioService usuarioService;
    private final CorrecaoService correcaoService;

    @GetMapping()
    public String iniciar(Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        model.addAttribute("prova", new Prova());
        model.addAttribute("turmas", turmaService.findByProfessor(usuario));
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Provas", "/prova/listar"),
                criarBreadcrumb("Nova Prova", "")
        ));
        return "prova/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Prova prova,
                         @RequestParam String gabarito,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario professor = usuarioService.findByUsername(authentication.getName());
            prova.setProfessor(professor);

            // Parse do gabarito (ex: "A,B,C,D,A")
            List<String> respostasCorretas = Arrays.stream(gabarito.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase) // <-- aqui converte para letras maiúsculas
                    .filter(s -> !s.isEmpty())
                    .toList();

            if (prova.getId() == null) {
                // Nova prova
                provaService.criarProvaComQuestoes(prova, respostasCorretas);
                redirectAttributes.addFlashAttribute("sucesso", "Prova criada com sucesso!");
            } else {
                // Edição de prova existente
                Prova provaExistente = provaService.findById(prova.getId());
                if (provaExistente == null) {
                    throw new RuntimeException("Prova não encontrada para edição.");
                }
                // Atualiza apenas os campos permitidos para edição
                provaExistente.setTitulo(prova.getTitulo());
                provaExistente.setDescricao(prova.getDescricao());
                provaExistente.setDataAplicacao(prova.getDataAplicacao());
                provaExistente.setTurma(prova.getTurma());
                provaExistente.setValorTotal(prova.getValorTotal());

                provaService.atualizarProvaComQuestoes(provaExistente, respostasCorretas);
                redirectAttributes.addFlashAttribute("sucesso", "Prova atualizada com sucesso!");
            }
            return "redirect:/prova/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar prova: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/prova";
        }
    }

    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        model.addAttribute("provas", provaService.findByProfessor(usuario));
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Provas", "#"),
                criarBreadcrumb("Minhas Provas", "")
        ));
        return "prova/lista";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication) {
        Prova prova = provaService.findById(id);
        if (prova != null) {
            Usuario usuario = usuarioService.findByUsername(authentication.getName());

            // Construir string do gabarito a partir das questões
            StringBuilder gabaritoStr = new StringBuilder();
            if (prova.getQuestoes() != null && !prova.getQuestoes().isEmpty()) {
                for (int i = 0; i < prova.getQuestoes().size(); i++) {
                    if (i > 0) gabaritoStr.append(",");
                    gabaritoStr.append(prova.getQuestoes().get(i).getRespostaCorreta().name());
                }
            }

            model.addAttribute("prova", prova);
            model.addAttribute("turmas", turmaService.findByProfessor(usuario));
            model.addAttribute("gabaritoAtual", gabaritoStr.toString());
            model.addAttribute("breadcrumbs", Arrays.asList(
                    criarBreadcrumb("Provas", "/prova/listar"),
                    criarBreadcrumb("Editar", "")
            ));
            return "prova/formulario";
        }
        return "redirect:/prova/listar";
    }

    @GetMapping("/remover/{id}")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            provaService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Prova removida com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover prova: " + e.getMessage());
        }
        return "redirect:/prova/listar";
    }

    @GetMapping("/resultados/{id}")
    public String resultados(@PathVariable Long id, Model model) {
        Prova prova = provaService.findById(id);
        if (prova != null) {
            model.addAttribute("prova", prova);
            model.addAttribute("estatisticas", correcaoService.calcularEstatisticas(id));
            model.addAttribute("breadcrumbs", Arrays.asList(
                    criarBreadcrumb("Provas", "/prova/listar"),
                    criarBreadcrumb("Resultados", "")
            ));
            return "prova/resultados";
        }
        return "redirect:/prova/listar";
    }

    // Método auxiliar para criar breadcrumb
    private Map<String, String> criarBreadcrumb(String label, String url) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put("label", label);
        breadcrumb.put("url", url);
        return breadcrumb;
    }
}