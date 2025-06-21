// src/main/java/br/unialfa/hackathon/dto/AuthResponse.java
package br.unialfa.hackathon.dto;

public record AuthResponse(String token, UsuarioResponse usuario) {
    // Não precisamos de construtores explícitos ou getters/setters com records
    // Eles são gerados automaticamente.
}