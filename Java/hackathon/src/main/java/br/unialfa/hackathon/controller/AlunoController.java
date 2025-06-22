package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.model.Resultado;
import br.unialfa.hackathon.model.TipoUsuario;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.AlunoService;
import br.unialfa.hackathon.service.CorrecaoService;
import br.unialfa.hackathon.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/aluno")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;
    private final CorrecaoService correcaoService;
    private final UsuarioService usuarioService;

    @GetMapping("/notas")
    public String minhasNotas(@RequestParam(required = false) String matricula,
                              Authentication authentication,
                              Model model) {

        // Se for um usuário ALUNO logado, buscar sua matrícula automaticamente
        if (matricula == null && authentication != null) {
            Usuario usuario = usuarioService.findByUsername(authentication.getName());
            if (usuario != null && usuario.getTipo() == TipoUsuario.ALUNO) {
                // Aqui você precisaria de uma relação entre Usuario e Aluno
                // Por enquanto, vamos pedir a matrícula
                return "aluno/buscar-matricula";
            }
        }

        if (matricula != null && !matricula.isEmpty()) {
            Aluno aluno = alunoService.findByMatricula(matricula);
            if (aluno != null) {
                List<Resultado> resultados = correcaoService.findResultadosByAluno(aluno.getId());
                model.addAttribute("aluno", aluno);
                model.addAttribute("resultados", resultados);

                // Calcular média geral
                double mediaGeral = 0.0;
                if (!resultados.isEmpty()) {
                    mediaGeral = resultados.stream()
                            .mapToDouble(Resultado::getNota)
                            .average()
                            .orElse(0.0);
                }

                model.addAttribute("mediaGeral", mediaGeral);
                model.addAttribute("breadcrumbs", Arrays.asList(
                        criarBreadcrumb("Acadêmico", "#"),
                        criarBreadcrumb("Minhas Notas", "")
                ));

                return "aluno/notas";
            } else {
                model.addAttribute("erro", "Matrícula não encontrada!");
            }
        }

        // Se não encontrou o aluno ou não tem matrícula, pede a matrícula
        return "aluno/buscar-matricula";
    }

    @GetMapping("/historico")
    public String historico(@RequestParam(required = false) String matricula,
                            Authentication authentication,
                            Model model) {

        // Se for um usuário ALUNO logado, buscar sua matrícula automaticamente
        if (matricula == null && authentication != null) {
            Usuario usuario = usuarioService.findByUsername(authentication.getName());
            if (usuario != null && usuario.getTipo() == TipoUsuario.ALUNO) {
                // Por enquanto, vamos pedir a matrícula
                return "aluno/buscar-matricula";
            }
        }

        if (matricula != null && !matricula.isEmpty()) {
            Aluno aluno = alunoService.findByMatricula(matricula);
            if (aluno != null) {
                List<Resultado> resultados = correcaoService.findResultadosByAluno(aluno.getId());

                // Calcular média geral
                double mediaGeral = 0.0;
                if (!resultados.isEmpty()) {
                    mediaGeral = resultados.stream()
                            .mapToDouble(Resultado::getNota)
                            .average()
                            .orElse(0.0);
                }

                // Obter turmas únicas (disciplinas cursadas)
                Set<String> turmasUnicas = resultados.stream()
                        .map(r -> r.getProva().getTurma().getDisciplina().getNome())
                        .collect(Collectors.toSet());

                // Agrupar resultados por período (simplificado - você pode melhorar isso)
                List<Resultado> resultados2024_1 = resultados.stream()
                        .filter(r -> r.getProva().getDataAplicacao().getYear() == 2024)
                        .collect(Collectors.toList());

                model.addAttribute("aluno", aluno);
                model.addAttribute("resultados", resultados);
                model.addAttribute("mediaGeral", mediaGeral);
                model.addAttribute("turmasUnicas", turmasUnicas);
                model.addAttribute("resultados2024_1", resultados2024_1);
                model.addAttribute("breadcrumbs", Arrays.asList(
                        criarBreadcrumb("Acadêmico", "#"),
                        criarBreadcrumb("Histórico", "")
                ));

                return "aluno/historico";
            } else {
                model.addAttribute("erro", "Matrícula não encontrada!");
            }
        }

        return "aluno/buscar-matricula";
    }

    @GetMapping("/resultado/{id}")
    public String detalheResultado(@PathVariable Long id, Model model) {
        // Implementar visualização detalhada se necessário
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Acadêmico", "#"),
                criarBreadcrumb("Resultado", "")
        ));
        return "aluno/resultado-detalhe";
    }

    // Método auxiliar para criar breadcrumb
    private Map<String, String> criarBreadcrumb(String label, String url) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put("label", label);
        breadcrumb.put("url", url);
        return breadcrumb;
    }
}