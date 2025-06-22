package br.unialfa.hackathon.model;

public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    PROFESSOR("Professor"),
    ALUNO("Aluno");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}