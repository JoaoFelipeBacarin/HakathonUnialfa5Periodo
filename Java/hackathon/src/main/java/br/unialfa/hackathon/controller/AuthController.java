package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Formulário de login personalizado (Spring Security irá redirecionar aqui)
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("erro", "Credenciais inválidas.");
        }
        return "login"; // login.html
    }

    // Página inicial pós-login
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                       Model model) {
        model.addAttribute("usuarioLogado", user.getUsername()); // e-mail
        return "home"; // home.html
    }

    // (Opcional) Formulário de cadastro de novo usuário (útil para testes rápidos)
    @GetMapping("/cadastro")
    public String formCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro"; // cadastro.html
    }

    @PostMapping("/cadastro")
    public String salvarCadastro(@ModelAttribute Usuario usuario) {
        authService.cadastrar(usuario);
        return "redirect:/login";
    }

    // (Opcional) Página de acesso negado
    @GetMapping("/403")
    public String acessoNegado() {
        return "403"; // 403.html
    }
}
