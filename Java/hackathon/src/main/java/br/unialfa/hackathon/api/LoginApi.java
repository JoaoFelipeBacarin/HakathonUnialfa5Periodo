package br.unialfa.hackathon.api;

import br.unialfa.hackathon.dto.LoginRequest;
import br.unialfa.hackathon.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class LoginApi {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        try {
            return ResponseEntity.ok(authService.autenticar(login));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("mensagem", e.getMessage()));
        }
    }
}

