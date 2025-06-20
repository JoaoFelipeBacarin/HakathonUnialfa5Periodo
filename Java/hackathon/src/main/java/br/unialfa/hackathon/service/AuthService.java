package br.unialfa.hackathon.service;

import br.unialfa.hackathon.dto.AuthResponse;
import br.unialfa.hackathon.dto.LoginRequest;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.UsuarioRepository;
import br.unialfa.hackathon.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    public AuthResponse autenticar(LoginRequest login) {
        Usuario usuario = usuarioRepo.findByEmail(login.email())
                .orElseThrow(() -> new RuntimeException("Credenciais inválidas."));

        if (!passwordEncoder.matches(login.senha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas.");
        }

        String token = jwtUtil.gerarToken(usuario);
        return new AuthResponse(token, usuario);
    }
}



