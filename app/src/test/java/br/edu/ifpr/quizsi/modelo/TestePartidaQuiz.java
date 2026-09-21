package br.edu.ifpr.quizsi.modelo;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestePartidaQuiz {
    private List<Pergunta> perguntas;
    private PartidaQuiz partida;

    @Before
    public void prepararPartida() {
        perguntas = Arrays.asList(
                criarPergunta(1), criarPergunta(2), criarPergunta(0),
                criarPergunta(3), criarPergunta(1));
        partida = new PartidaQuiz(perguntas);
    }

    private Pergunta criarPergunta(int indiceCorreto) {
        return new Pergunta(10, new int[]{11, 12, 13, 14}, indiceCorreto, 15);
    }

    private void responderPergunta(int alternativa) {
        assertTrue(partida.selecionarAlternativa(alternativa));
        assertTrue(partida.confirmarResposta());
    }

    private PartidaQuiz restaurarPartidaAtual() {
        return PartidaQuiz.restaurarPartida(
                perguntas, partida.obterIndicePergunta(),
                partida.obterAlternativaSelecionada(), partida.copiarRespostasConfirmadas());
    }

    private void acertarTodasAsPerguntas() {
        for (Pergunta pergunta : perguntas) {
            responderPergunta(pergunta.obterIndiceCorreto());
            assertTrue(partida.avancarPergunta());
        }
    }

    @Test
    public void iniciaSemRespostas() {
        assertEquals(0, partida.obterPontuacao());
        assertEquals(0, partida.obterIndicePergunta());
        assertEquals(-1, partida.obterAlternativaSelecionada());
        assertFalse(partida.estaFinalizada());
    }

    @Test
    public void exigeSelecao() {
        assertFalse(partida.confirmarResposta());
        assertFalse(partida.avancarPergunta());
        assertEquals(0, partida.obterPontuacao());
    }

    @Test
    public void selecaoSozinhaNaoPontua() {
        assertTrue(partida.selecionarAlternativa(1));
        assertEquals(0, partida.obterPontuacao());
        assertFalse(partida.avancarPergunta());
    }

    @Test
    public void respostaCorretaPontuaUmaVez() {
        responderPergunta(1);
        assertEquals(1, partida.obterPontuacao());
        assertTrue(partida.respostaAtualEstaCorreta());

        for (int tentativa = 0; tentativa < 20; tentativa++) {
            assertFalse(partida.confirmarResposta());
        }
        assertEquals(1, partida.obterPontuacao());
    }

    @Test
    public void respostaIncorretaNaoPontua() {
        responderPergunta(0);
        assertEquals(0, partida.obterPontuacao());
        assertFalse(partida.respostaAtualEstaCorreta());
        assertTrue(partida.respostaEstaConfirmada());
    }

    @Test
    public void impedeAlteracaoAposConfirmar() {
        responderPergunta(0);
        assertFalse(partida.selecionarAlternativa(1));
        assertEquals(0, partida.obterAlternativaSelecionada());
        assertEquals(0, partida.obterPontuacao());
    }

    @Test
    public void permiteAlteracaoAntesDeConfirmar() {
        partida.selecionarAlternativa(0);
        partida.selecionarAlternativa(1);
        partida.confirmarResposta();
        assertEquals(1, partida.obterPontuacao());
    }

    @Test
    public void avancosRepetidosNaoPulamPergunta() {
        responderPergunta(1);
        assertTrue(partida.avancarPergunta());

        for (int tentativa = 0; tentativa < 20; tentativa++) {
            assertFalse(partida.avancarPergunta());
        }
        assertEquals(1, partida.obterIndicePergunta());
        assertEquals(-1, partida.obterAlternativaSelecionada());
    }

    @Test
    public void cincoRespostasFinalizamComPontuacaoEPercentualEsperados() {
        for (int alternativa : new int[]{1, 0, 0, 0, 1}) {
            responderPergunta(alternativa);
            assertTrue(partida.avancarPergunta());
        }

        assertTrue(partida.estaFinalizada());
        assertEquals(3, partida.obterPontuacao());
        assertEquals(60, partida.obterPercentual());
        assertFalse(partida.confirmarResposta());
        assertFalse(partida.avancarPergunta());
        assertFalse(partida.selecionarAlternativa(1));
    }

    @Test
    public void reiniciarLimpaPartidaFinalizada() {
        acertarTodasAsPerguntas();
        assertEquals(100, partida.obterPercentual());

        partida.reiniciarPartida();

        assertEquals(0, partida.obterIndicePergunta());
        assertEquals(0, partida.obterPontuacao());
        assertArrayEquals(new int[]{-1, -1, -1, -1, -1}, partida.copiarRespostasConfirmadas());
        assertEquals(-1, partida.obterAlternativaSelecionada());
        assertFalse(partida.respostaEstaConfirmada());
        assertFalse(partida.estaFinalizada());
    }

    @Test
    public void restauraSelecaoAntesDeConfirmar() {
        partida.selecionarAlternativa(2);
        PartidaQuiz partidaRestaurada = restaurarPartidaAtual();

        assertEquals(2, partidaRestaurada.obterAlternativaSelecionada());
        assertFalse(partidaRestaurada.respostaEstaConfirmada());
        assertTrue(partidaRestaurada.confirmarResposta());
        assertEquals(0, partidaRestaurada.obterPontuacao());
    }

    @Test
    public void restauraRespostaConfirmadaSemDuplicarPontos() {
        responderPergunta(1);
        partida.avancarPergunta();
        responderPergunta(0);
        PartidaQuiz partidaRestaurada = restaurarPartidaAtual();

        assertEquals(1, partidaRestaurada.obterIndicePergunta());
        assertEquals(0, partidaRestaurada.obterAlternativaSelecionada());
        assertTrue(partidaRestaurada.respostaEstaConfirmada());
        assertFalse(partidaRestaurada.respostaAtualEstaCorreta());
        assertEquals(1, partidaRestaurada.obterPontuacao());
        assertFalse(partidaRestaurada.confirmarResposta());
        assertFalse(partidaRestaurada.selecionarAlternativa(2));
    }

    @Test
    public void respostasSalvasSaoCopiasIndependentes() {
        responderPergunta(1);
        int[] respostasSalvas = partida.copiarRespostasConfirmadas();
        PartidaQuiz partidaRestaurada = PartidaQuiz.restaurarPartida(perguntas, 0, 1, respostasSalvas);

        respostasSalvas[0] = 0;

        assertEquals(1, partida.obterPontuacao());
        assertEquals(1, partidaRestaurada.obterPontuacao());
    }

    @Test
    public void rejeitaAlternativaInvalida() {
        assertFalse(partida.selecionarAlternativa(-1));
        assertFalse(partida.selecionarAlternativa(4));
        assertFalse(partida.confirmarResposta());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejeitaProgressoSalvoInvalido() {
        PartidaQuiz.restaurarPartida(perguntas, 2, -1, new int[]{1, -1, -1, -1, -1});
    }

    @Test
    public void restauraPartidaFinalizada() {
        acertarTodasAsPerguntas();
        PartidaQuiz partidaRestaurada = restaurarPartidaAtual();

        assertTrue(partidaRestaurada.estaFinalizada());
        assertEquals(5, partidaRestaurada.obterPontuacao());
        assertFalse(partidaRestaurada.avancarPergunta());
    }
}
