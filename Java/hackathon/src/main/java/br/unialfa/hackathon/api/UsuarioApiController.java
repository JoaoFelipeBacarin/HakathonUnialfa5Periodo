package br.unialfa.hackathon.api;

import br.unialfa.hackathon.model.Usuario;
import br.unialfa.hackathon.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();

        return usuarios.stream()
                .map(usuario -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", usuario.getId());
                    dto.put("login", usuario.getLogin());
                    dto.put("nome", usuario.getNome());
                    dto.put("email", usuario.getEmail());
                    dto.put("tipo", usuario.getTipo().name());
                    dto.put("ativo", usuario.getAtivo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("endpoint", "/api/usuarios/test");
        return response;
    }
}