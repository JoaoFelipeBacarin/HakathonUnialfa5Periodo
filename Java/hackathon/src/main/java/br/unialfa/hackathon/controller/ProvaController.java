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
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter; // Importar para formatação

@Controller
@RequestMapping("/prova")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;
    private final TurmaService turmaService;
    private final UsuarioService usuarioService;
    private final CorrecaoService correcaoService;

    // Formatter para exibir datas no input type="date"
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping()
    public String iniciar(Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        Prova novaProva = new Prova();
        novaProva.setNumeroQuestoes(0); // Inicializa para nova prova
        model.addAttribute("prova", novaProva);
        model.addAttribute("turmas", turmaService.findByProfessor(usuario));
        model.addAttribute("gabaritoAtual", ""); // Sempre inicializar gabaritoAtual para nova prova
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

            List<String> respostasCorretas = Arrays.stream(gabarito.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .filter(s -> !s.isEmpty())
                    .toList();

            prova.setNumeroQuestoes(respostasCorretas.size());

            if (prova.getId() == null) {
                provaService.criarProvaComQuestoes(prova, respostasCorretas);
                redirectAttributes.addFlashAttribute("sucesso", "Prova criada com sucesso!");
            } else {
                provaService.atualizarProvaComQuestoes(prova, respostasCorretas);
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

        if (prova == null) {
            return "redirect:/prova/listar";
        }

        Usuario usuario = usuarioService.findByUsername(authentication.getName());

        String gabaritoStr = prova.getQuestoes().stream()
                .map(q -> q.getRespostaCorreta().name())
                .collect(Collectors.joining(","));

        model.addAttribute("prova", prova);
        model.addAttribute("turmas", turmaService.findByProfessor(usuario));
        model.addAttribute("gabaritoAtual", gabaritoStr);

        // Garante que a data esteja no formato correto para input type="date"
        if (prova.getDataAplicacao() != null) {
            model.addAttribute("dataAplicacaoFormatada", prova.getDataAplicacao().format(DATE_FORMATTER));
        }

        prova.setNumeroQuestoes(prova.getQuestoes().size());

        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Provas", "/prova/listar"),
                criarBreadcrumb("Editar", "")
        ));

        return "prova/formulario";
    }

    @PostMapping("/remover/{id}")
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

    @GetMapping("/visualizar/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        Prova prova = provaService.findById(id);

        if (prova == null) {
            return "redirect:/prova/listar";
        }

        String gabaritoStr = prova.getQuestoes().stream()
                .map(q -> q.getRespostaCorreta().name())
                .collect(Collectors.joining(","));

        model.addAttribute("prova", prova);
        model.addAttribute("turmas", turmaService.findAll());
        model.addAttribute("gabaritoAtual", gabaritoStr);

        // Garante que a data esteja no formato correto para input type="date"
        if (prova.getDataAplicacao() != null) {
            model.addAttribute("dataAplicacaoFormatada", prova.getDataAplicacao().format(DATE_FORMATTER));
        }

        model.addAttribute("modoVisualizacao", true);

        prova.setNumeroQuestoes(prova.getQuestoes().size());

        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Provas", "/prova/listar"),
                criarBreadcrumb("Visualizar", "")
        ));

        return "prova/formulario";
    }

    private Map<String, String> criarBreadcrumb(String label, String url) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put("label", label);
        breadcrumb.put("url", url);
        return breadcrumb;
    }
}