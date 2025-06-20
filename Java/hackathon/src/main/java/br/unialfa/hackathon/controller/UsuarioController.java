package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/novo")
    public String formNovoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/form"; // Thymeleaf: usuario/form.html
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.salvar(usuario);
        return "redirect:/usuarios/listar";
    }

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarPorPerfil("ALUNO")); // Exemplo
        return "usuario/lista"; // Thymeleaf: usuario/lista.html
    }
}
