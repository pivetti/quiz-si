package br.edu.ifpr.quizsi.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PartidaQuiz {
    private final List<Pergunta> perguntas;
    private final int[] respostasConfirmadas;
    private int indicePergunta;
    private int alternativaSelecionada = -1;

    public PartidaQuiz(List<Pergunta> perguntas) {
        if (perguntas == null || perguntas.isEmpty() || perguntas.contains(null)) {
            throw new IllegalArgumentException("A lista de perguntas não pode ser vazia nem conter valores nulos.");
        }

        this.perguntas = Collections.unmodifiableList(new ArrayList<>(perguntas));
        respostasConfirmadas = new int[perguntas.size()];
        reiniciarPartida();
    }

    public boolean selecionarAlternativa(int alternativa) {
        if (estaFinalizada() || respostaEstaConfirmada() || alternativa < 0 || alternativa > 3) {
            return false;
        }

        alternativaSelecionada = alternativa;
        return true;
    }

    public boolean confirmarResposta() {
        if (estaFinalizada() || respostaEstaConfirmada() || alternativaSelecionada == -1) {
            return false;
        }

        respostasConfirmadas[indicePergunta] = alternativaSelecionada;
        return true;
    }

    public boolean avancarPergunta() {
        if (estaFinalizada() || !respostaEstaConfirmada()) {
            return false;
        }

        indicePergunta++;
        alternativaSelecionada = -1;
        return true;
    }

    public void reiniciarPartida() {
        Arrays.fill(respostasConfirmadas, -1);
        indicePergunta = 0;
        alternativaSelecionada = -1;
    }

    public int obterPontuacao() {
        int pontuacao = 0;
        for (int indice = 0; indice < respostasConfirmadas.length; indice++) {
            if (perguntas.get(indice).estaCorreta(respostasConfirmadas[indice])) {
                pontuacao++;
            }
        }
        return pontuacao;
    }

    public int obterPercentual() {
        return Math.round(100f * obterPontuacao() / obterTotalPerguntas());
    }

    public boolean estaFinalizada() {
        return indicePergunta == perguntas.size();
    }

    public boolean respostaEstaConfirmada() {
        return !estaFinalizada() && respostasConfirmadas[indicePergunta] != -1;
    }

    public boolean respostaAtualEstaCorreta() {
        return respostaEstaConfirmada() && obterPerguntaAtual().estaCorreta(alternativaSelecionada);
    }

    public Pergunta obterPerguntaAtual() {
        if (estaFinalizada()) {
            throw new IllegalStateException("A partida já foi finalizada.");
        }
        return perguntas.get(indicePergunta);
    }

    public int obterIndicePergunta() {
        return indicePergunta;
    }

    public int obterTotalPerguntas() {
        return perguntas.size();
    }

    public int obterAlternativaSelecionada() {
        return alternativaSelecionada;
    }

    public int[] copiarRespostasConfirmadas() {
        return respostasConfirmadas.clone();
    }

    public static PartidaQuiz restaurarPartida(
            List<Pergunta> perguntas, int indicePergunta, int alternativaSelecionada, int[] respostasSalvas) {
        PartidaQuiz partida = new PartidaQuiz(perguntas);
        validarEstadoSalvo(partida.obterTotalPerguntas(), indicePergunta, alternativaSelecionada, respostasSalvas);

        System.arraycopy(respostasSalvas, 0, partida.respostasConfirmadas, 0, respostasSalvas.length);
        partida.indicePergunta = indicePergunta;
        partida.alternativaSelecionada = alternativaSelecionada;
        return partida;
    }

    private static void validarEstadoSalvo(
            int totalPerguntas, int indicePergunta, int alternativaSelecionada, int[] respostasSalvas) {
        if (respostasSalvas == null || respostasSalvas.length != totalPerguntas
                || indicePergunta < 0 || indicePergunta > totalPerguntas
                || alternativaSelecionada < -1 || alternativaSelecionada > 3) {
            throw new IllegalArgumentException("O estado salvo da partida é inválido.");
        }

        // Perguntas anteriores devem estar respondidas; as seguintes ainda não.
        for (int indice = 0; indice < respostasSalvas.length; indice++) {
            int resposta = respostasSalvas[indice];
            boolean respostaInvalida = resposta < -1 || resposta > 3;
            boolean perguntaAnteriorSemResposta = indice < indicePergunta && resposta == -1;
            boolean perguntaFuturaRespondida = indice > indicePergunta && resposta != -1;

            if (respostaInvalida || perguntaAnteriorSemResposta || perguntaFuturaRespondida) {
                throw new IllegalArgumentException("A sequência de respostas é inválida.");
            }
        }

        boolean partidaFinalizada = indicePergunta == totalPerguntas;
        if (partidaFinalizada) {
            if (alternativaSelecionada != -1) {
                throw new IllegalArgumentException("A alternativa selecionada é inválida.");
            }
        } else if (respostasSalvas[indicePergunta] != -1
                && respostasSalvas[indicePergunta] != alternativaSelecionada) {
            throw new IllegalArgumentException("A alternativa selecionada é inválida.");
        }
    }
}
