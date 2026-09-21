package br.edu.ifpr.quizsi.modelo;

public final class Pergunta {
    private final int enunciado;
    private final int[] alternativas;
    private final int indiceCorreto;
    private final int explicacao;

    public Pergunta(int enunciado, int[] alternativas, int indiceCorreto, int explicacao) {
        if (alternativas == null || alternativas.length != 4 || indiceCorreto < 0 || indiceCorreto >= 4) {
            throw new IllegalArgumentException("A pergunta precisa de quatro alternativas e uma resposta válida.");
        }

        this.enunciado = enunciado;
        this.alternativas = alternativas.clone();
        this.indiceCorreto = indiceCorreto;
        this.explicacao = explicacao;
    }

    public int obterEnunciado() {
        return enunciado;
    }

    public int obterAlternativa(int indice) {
        return alternativas[indice];
    }

    public int obterIndiceCorreto() {
        return indiceCorreto;
    }

    public int obterExplicacao() {
        return explicacao;
    }

    public boolean estaCorreta(int resposta) {
        return resposta == indiceCorreto;
    }
}
