// src/main/java/br/unialfa/hackathon/security/JwtAuthFilter.java
package br.unialfa.hackathon.security;

import br.unialfa.hackathon.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService; // Use o CustomUserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // remove "Bearer "

            try {
                // Valida o token e extrai o email
                String email = jwtUtil.extractUsername(token); // Altere JwtUtil para ter extractUsername
                // Se o token for válido e o usuário ainda não estiver autenticado no contexto de segurança
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // Revalida o token com base nos UserDetails carregados
                    if (jwtUtil.validateToken(token, userDetails)) { // Altere JwtUtil para ter validateToken
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Log the exception for debugging
                // System.err.println("Erro ao processar token JWT: " + e.getMessage());
                // Permite que o ControllerAdvice ou o próprio Spring Security lide com erros de autenticação
                // Não é bom retornar um erro aqui diretamente, deixe o fluxo continuar
            }
        }

        filterChain.doFilter(request, response);
    }
}