package br.unialfa.hackathon.service;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Usuario> listarPorPerfil(String perfil) {
        return repository.findAll(); // Ou filtrar por perfil, se desejar
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }
}
