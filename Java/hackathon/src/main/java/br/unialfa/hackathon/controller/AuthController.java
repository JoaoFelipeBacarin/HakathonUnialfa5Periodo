// src/main/java/br/unialfa/hackathon/controller/AuthController.java
package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; // Usar UserDetails
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // Injetar AuthService

    // Formulário de login personalizado
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("erro", "Credenciais inválidas.");
        }
        return "login"; // Retorna o nome do template Thymeleaf: login.html
    }

    // Página inicial pós-login
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // userDetails contém as informações do usuário logado fornecidas pelo Spring Security
        // Você pode buscar o usuário completo se precisar de mais detalhes além do email/username
        // Ou, se o seu CustomUserDetail tiver um método para retornar o usuário original:
        // if (userDetails instanceof CustomUsuario customUsuario) {
        //     model.addAttribute("usuarioLogado", customUsuario.getUsuario().getNome());
        // } else {
        //     model.addAttribute("usuarioLogado", userDetails.getUsername());
        // }
        model.addAttribute("usuarioLogado", userDetails.getUsername()); // Exemplo: exibe o email
        return "home"; // Retorna o nome do template Thymeleaf: home.html
    }

    // (Opcional) Formulário de cadastro de novo usuário (útil para testes rápidos)
    // Se você tiver uma API REST para cadastro, essa parte pode não ser necessária,
    // mas é comum ter um formulário para o usuário final.
    @GetMapping("/cadastro")
    public String formCadastro(Model model) {
        model.addAttribute("usuario", new Usuario()); // Adiciona um objeto Usuario vazio para o formulário
        return "cadastro"; // cadastro.html
    }

    @PostMapping("/cadastro")
    public String salvarCadastro(@ModelAttribute Usuario usuario, Model model) {
        // O ideal seria usar um DTO de entrada para cadastro também, para evitar
        // expor a entidade Usuario diretamente no formulário.
        try {
            authService.cadastrar(usuario); // Certifique-se que AuthService.cadastrar existe e encoda a senha
            return "redirect:/login?cadastroSucesso"; // Redireciona para login com mensagem de sucesso
        } catch (Exception e) {
            model.addAttribute("erroCadastro", "Erro ao cadastrar usuário: " + e.getMessage());
            model.addAttribute("usuario", usuario); // Retorna os dados para preencher o formulário novamente
            return "cadastro"; // Volta para a tela de cadastro
        }
    }

    // (Opcional) Página de acesso negado
    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado"; // acesso-negado.html
    }
}