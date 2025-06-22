package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.TipoUsuario;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        model.addAttribute("usuario", usuario);

        // Redireciona para o dashboard apropriado baseado no tipo de usuário
        if (usuario.getTipo() == TipoUsuario.ADMINISTRADOR) {
            return "admin/dashboard";
        } else if (usuario.getTipo() == TipoUsuario.PROFESSOR) {
            return "professor/dashboard";
        } else if (usuario.getTipo() == TipoUsuario.ALUNO) {
            return "aluno/dashboard";
        }

        return "redirect:/login";
    }
}