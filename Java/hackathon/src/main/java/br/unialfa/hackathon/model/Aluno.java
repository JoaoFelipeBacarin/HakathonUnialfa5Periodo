// src/main/java/br/unialfa/hackathon/model/Aluno.java
package br.unialfa.hackathon.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;
    private Long ra;
    private Long tipo;
    private Boolean status;


    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @OneToOne // <--- Adiciona esta anotação para o relacionamento com Usuario
    @JoinColumn(name = "usuario_id", unique = true) // <--- Coluna para a chave estrangeira do Usuario
    private Usuario usuario; // <--- NOVO CAMPO: vínculo com a entidade Usuario (para o email)
}