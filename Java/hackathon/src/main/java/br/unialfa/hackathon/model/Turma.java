package br.unialfa.hackathon.model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String ano;

    public Turma() {}

    public Turma(String nome, String ano) {
        this.nome = nome;
        this.ano = ano;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getAno() {
        return ano;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}

