// src/main/java/br/unialfa/hackathon/model/Usuario.java
package br.unialfa.hackathon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Id;

@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String role; // <<--- ESTE CAMPO DEVE EXISTIR E TER UM GETTER

    // Se estiver usando Lombok @Data, os getters e setters são gerados automaticamente.
    // Caso contrário, você precisaria de:
    // public String getRole() { return this.role; }
    // public void setRole(String role) { this.role = role; }
}