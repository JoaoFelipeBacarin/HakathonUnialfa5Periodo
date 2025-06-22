package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.*;
import br.unialfa.hackathon.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final DisciplinaService disciplinaService;
    private final TurmaService turmaService;
    private final AlunoService alunoService;

    // ========== USUÁRIOS ==========
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Usuários", "")
        ));
        return "admin/usuarios/lista";
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tipos", TipoUsuario.values());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Usuários", "/admin/usuarios"),
                criarBreadcrumb("Novo", "")
        ));
        return "admin/usuarios/formulario";
    }

    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("tipos", TipoUsuario.values());
            model.addAttribute("breadcrumbs", Arrays.asList(
                    criarBreadcrumb("Administração", "#"),
                    criarBreadcrumb("Usuários", "/admin/usuarios"),
                    criarBreadcrumb("Editar", "")
            ));
            return "admin/usuarios/formulario";
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/remover/{id}")
    public String removerUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // ========== DISCIPLINAS ==========
    @GetMapping("/disciplinas")
    public String listarDisciplinas(Model model) {
        model.addAttribute("disciplinas", disciplinaService.findAll());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Disciplinas", "")
        ));
        return "admin/disciplinas/lista";
    }

    @GetMapping("/disciplinas/nova")
    public String novaDisciplina(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Disciplinas", "/admin/disciplinas"),
                criarBreadcrumb("Nova", "")
        ));
        return "admin/disciplinas/formulario";
    }

    @PostMapping("/disciplinas/salvar")
    public String salvarDisciplina(@ModelAttribute Disciplina disciplina, RedirectAttributes redirectAttributes) {
        try {
            disciplinaService.save(disciplina);
            redirectAttributes.addFlashAttribute("sucesso", "Disciplina salva com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar disciplina: " + e.getMessage());
        }
        return "redirect:/admin/disciplinas";
    }

    @GetMapping("/disciplinas/editar/{id}")
    public String editarDisciplina(@PathVariable Long id, Model model) {
        Disciplina disciplina = disciplinaService.findById(id);
        if (disciplina != null) {
            model.addAttribute("disciplina", disciplina);
            model.addAttribute("breadcrumbs", Arrays.asList(
                    criarBreadcrumb("Administração", "#"),
                    criarBreadcrumb("Disciplinas", "/admin/disciplinas"),
                    criarBreadcrumb("Editar", "")
            ));
            return "admin/disciplinas/formulario";
        }
        return "redirect:/admin/disciplinas";
    }

    @GetMapping("/disciplinas/remover/{id}")
    public String removerDisciplina(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            disciplinaService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Disciplina removida com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover disciplina: " + e.getMessage());
        }
        return "redirect:/admin/disciplinas";
    }

    // ========== TURMAS ==========
    @GetMapping("/turmas")
    public String listarTurmas(Model model) {
        model.addAttribute("turmas", turmaService.findAll());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Turmas", "")
        ));
        return "admin/turmas/lista";
    }

    @GetMapping("/turmas/nova")
    public String novaTurma(Model model) {
        model.addAttribute("turma", new Turma());
        model.addAttribute("professores", usuarioService.findProfessores());
        model.addAttribute("disciplinas", disciplinaService.findAll());
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Turmas", "/admin/turmas"),
                criarBreadcrumb("Nova", "")
        ));
        return "admin/turmas/formulario";
    }

    @PostMapping("/turmas/salvar")
    public String salvarTurma(@ModelAttribute Turma turma,
                              @RequestParam(required = false) List<Long> alunosIds,
                              RedirectAttributes redirectAttributes) {
        try {
            if (alunosIds != null) {
                turma.setAlunos(alunosIds.stream()
                        .map(alunoService::findById)
                        .filter(aluno -> aluno != null)
                        .toList());
            }
            turmaService.save(turma);
            redirectAttributes.addFlashAttribute("sucesso", "Turma salva com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar turma: " + e.getMessage());
        }
        return "redirect:/admin/turmas";
    }

    @GetMapping("/turmas/editar/{id}")
    public String editarTurma(@PathVariable Long id, Model model) {
        Turma turma = turmaService.findById(id);
        if (turma != null) {
            model.addAttribute("turma", turma);
            model.addAttribute("professores", usuarioService.findProfessores());
            model.addAttribute("disciplinas", disciplinaService.findAll());
            model.addAttribute("alunos", alunoService.findAll());
            model.addAttribute("breadcrumbs", Arrays.asList(
                    criarBreadcrumb("Administração", "#"),
                    criarBreadcrumb("Turmas", "/admin/turmas"),
                    criarBreadcrumb("Editar", "")
            ));
            return "admin/turmas/formulario";
        }
        return "redirect:/admin/turmas";
    }

    @GetMapping("/turmas/remover/{id}")
    public String removerTurma(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            turmaService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Turma removida com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover turma: " + e.getMessage());
        }
        return "redirect:/admin/turmas";
    }

    // ========== ALUNOS ==========
    @GetMapping("/alunos")
    public String listarAlunos(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Alunos", "")
        ));
        return "admin/alunos/lista";
    }

    @GetMapping("/alunos/novo")
    public String novoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("breadcrumbs", Arrays.asList(
                criarBreadcrumb("Administração", "#"),
                criarBreadcrumb("Alunos", "/admin/alunos"),
                criarBreadcrumb("Novo", "")
        ));
        return "admin/alunos/formulario";
    }

    @PostMapping("/alunos/salvar")
    public String salvarAluno(@ModelAttribute Aluno aluno, RedirectAttributes redirectAttributes) {
        try {
            alunoService.save(aluno);
            redirectAttributes.addFlashAttribute("sucesso", "Aluno salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar aluno: " + e.getMessage());
        }
        return "redirect:/admin/alunos";
    }

    @GetMapping("/alunos/editar/{id}")
    public String editarAluno(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id);
        if (aluno != null) {
            model.addAttribute("aluno", aluno);
            model.addAttribute("breadcrumbs", Arrays.asList(
                    criarBreadcrumb("Administração", "#"),
                    criarBreadcrumb("Alunos", "/admin/alunos"),
                    criarBreadcrumb("Editar", "")
            ));
            return "admin/alunos/formulario";
        }
        return "redirect:/admin/alunos";
    }

    @GetMapping("/alunos/remover/{id}")
    public String removerAluno(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Aluno removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover aluno: " + e.getMessage());
        }
        return "redirect:/admin/alunos";
    }

    // Método auxiliar para criar breadcrumb
    private Map<String, String> criarBreadcrumb(String label, String url) {
        Map<String, String> breadcrumb = new HashMap<>();
        breadcrumb.put("label", label);
        breadcrumb.put("url", url);
        return breadcrumb;
    }
}