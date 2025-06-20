package br.unialfa.hackathon.dto;

import br.unialfa.hackathon.model.Usuario;

public record AuthResponse(String token, Usuario usuario) {

}
