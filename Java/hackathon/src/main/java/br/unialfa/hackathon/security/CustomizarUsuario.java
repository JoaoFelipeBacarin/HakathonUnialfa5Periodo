// src/main/java/br/unialfa/hackathon/security/CustomizarUsuario.java
// src/main/java/br/unialfa/hackathon/security/CustomizarUsuario.java
package br.unialfa.hackathon.security;

import br.unialfa.hackathon.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importar
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomizarUsuario implements UserDetails {

    private final Usuario usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assume que 'role' no objeto Usuario é uma String como "ADMINISTRADOR", "PROFESSOR", "ALUNO"
        // Adiciona o prefixo "ROLE_" exigido pelo Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Usuario getUsuario() {
        return usuario;
    }
}