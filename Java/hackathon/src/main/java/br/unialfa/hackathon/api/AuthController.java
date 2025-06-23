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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;

    // Simples armazenamento de tokens em memória (em produção use JWT ou Redis)
    private static final Map<String, Long> activeTokens = new ConcurrentHashMap<>();

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
                // Gerar token único
                String token = "TOKEN-" + UUID.randomUUID().toString();

                // Armazenar token
                activeTokens.put(token, usuario.getId());

                // Criar response completo
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("type", "Bearer");
                response.put("username", usuario.getLogin());
                response.put("nome", usuario.getNome());
                response.put("email", usuario.getEmail());
                response.put("tipo", usuario.getTipo().name());
                response.put("userId", usuario.getId());
                response.put("expiresIn", 3600L);

                return ResponseEntity.ok(response);
            }

            throw new BadCredentialsException("Usuário não encontrado");

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INVALID_CREDENTIALS");
            error.put("message", "Usuário ou senha incorretos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Verificar se token existe
                if (activeTokens.containsKey(token)) {
                    Long userId = activeTokens.get(token);
                    Usuario usuario = usuarioService.findById(userId);

                    if (usuario != null) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("valid", true);
                        response.put("userId", userId);
                        response.put("username", usuario.getLogin());
                        response.put("tipo", usuario.getTipo().name());
                        return ResponseEntity.ok(response);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token inválido ou expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "VALIDATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                activeTokens.remove(token);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout realizado com sucesso");
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Mesmo com erro, retorna sucesso no logout
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout realizado");
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (activeTokens.containsKey(token)) {
                    Long userId = activeTokens.get(token);
                    Usuario usuario = usuarioService.findById(userId);

                    if (usuario != null) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("id", usuario.getId());
                        response.put("username", usuario.getLogin());
                        response.put("nome", usuario.getNome());
                        response.put("email", usuario.getEmail());
                        response.put("tipo", usuario.getTipo().name());
                        response.put("ativo", usuario.getAtivo());
                        return ResponseEntity.ok(response);
                    }
                }
            }

            Map<String, String> error = new HashMap<>();
            error.put("error", "UNAUTHORIZED");
            error.put("message", "Token inválido ou usuário não encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "USER_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}