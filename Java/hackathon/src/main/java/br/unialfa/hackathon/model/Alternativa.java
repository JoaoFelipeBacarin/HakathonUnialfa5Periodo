package br.unialfa.hackathon.model;

public enum Alternativa {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E");

    private final String letra;

    Alternativa(String letra) {
        this.letra = letra;
    }

    public String getLetra() {
        return letra;
    }
}