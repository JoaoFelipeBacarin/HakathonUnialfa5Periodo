package br.unialfa.hackathon.repository;

import br.unialfa.hackathon.model.TipoUsuario;
import br.unialfa.hackathon.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    List<Usuario> findByTipo(TipoUsuario tipo);

    List<Usuario> findByAtivoTrue();

    @Query("SELECT u FROM Usuario u WHERE u.tipo = :tipo AND u.ativo = true")
    List<Usuario> findByTipoAndAtivoTrue(TipoUsuario tipo);
}
