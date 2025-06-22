package br.unialfa.hackathon.api;

import br.unialfa.hackathon.dto.LoginRequest;
import br.unialfa.hackathon.dto.LoginResponse;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Tentar autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Buscar usuário completo
            Usuario usuario = usuarioService.findByUsername(loginRequest.getUsername());

            if (usuario != null) {
                // Gerar um token simples (substitua por JWT se necessário)
                String token = "simple-token-" + usuario.getId() + "-" + System.currentTimeMillis();

                LoginResponse response = new LoginResponse(
                        token,
                        "Bearer",
                        usuario.getLogin(),
                        3600L
                );

                return ResponseEntity.ok(response);
            }

            throw new BadCredentialsException("Usuário não encontrado");

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciais inválidas");
            error.put("message", "Username ou password incorretos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Validação simples do token (substitua por JWT se necessário)
                if (token.startsWith("simple-token-")) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao validar token");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Extrair ID do usuário do token simples
                if (token.startsWith("simple-token-")) {
                    String[] parts = token.split("-");
                    if (parts.length >= 3) {
                        Long userId = Long.parseLong(parts[2]);
                        Usuario usuario = usuarioService.findById(userId);

                        if (usuario != null) {
                            Map<String, Object> response = new HashMap<>();
                            response.put("id", usuario.getId());
                            response.put("username", usuario.getLogin());
                            response.put("nome", usuario.getNome());
                            response.put("email", usuario.getEmail());
                            response.put("tipo", usuario.getTipo());
                            return ResponseEntity.ok(response);
                        }
                    }
                }
            }

            Map<String, String> error = new HashMap<>();
            error.put("error", "Token inválido ou usuário não encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar usuário");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}