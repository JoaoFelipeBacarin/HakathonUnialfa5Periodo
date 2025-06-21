// src/main/java/br/unialfa/hackathon/dto/UsuarioResponse.java
package br.unialfa.hackathon.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String role; // Representa o papel do usuário (ex: professor, admin, aluno)
}