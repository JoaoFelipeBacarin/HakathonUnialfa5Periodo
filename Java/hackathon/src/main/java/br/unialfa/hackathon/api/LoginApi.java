package br.unialfa.hackathon.api;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.security.JwtUtil;
import br.unialfa.hackathon.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginApi {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String senha) {
        return authService.autenticar(email, senha)
                .map(usuario -> {
                    String token = jwtUtil.generateToken(usuario);
                    return ResponseEntity.ok().body(token);
                })
                .orElse(ResponseEntity.status(401).body("Credenciais inválidas"));
    }
}
