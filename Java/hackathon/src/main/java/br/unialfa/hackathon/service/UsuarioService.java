package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.TipoUsuario;
import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByLogin(username).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByLogin(username);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> findByTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipoAndAtivoTrue(tipo);
    }

    public List<Usuario> findProfessores() {
        return findByTipo(TipoUsuario.PROFESSOR);
    }

    public Usuario criarUsuarioDefault() {
        if (!existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setPassword("admin123");
            admin.setNome("Administrador");
            admin.setTipo(TipoUsuario.ADMINISTRADOR);
            admin.setEmail("admin@unialfa.br");
            return save(admin);
        }
        return findByUsername("admin");
    }
}