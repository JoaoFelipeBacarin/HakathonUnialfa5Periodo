package br.unialfa.hackathon.security;



import br.unialfa.hackathon.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtil {
    private final String jwtSecret = "F5T6aK9dL2pR8xV1QzBnM3sH7jDcW4eZ";
    private final long expiration = 86400000; // 1 dia

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("role", usuario.getRole())
                .claim("nome", usuario.getNome())
                .claim("id", usuario.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}

