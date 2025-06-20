package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByPerfil(Perfil perfil);
}