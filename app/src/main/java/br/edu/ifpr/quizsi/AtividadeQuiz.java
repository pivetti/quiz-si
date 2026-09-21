package br.edu.ifpr.quizsi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import br.edu.ifpr.quizsi.dados.BancoDePerguntas;
import br.edu.ifpr.quizsi.modelo.PartidaQuiz;
import br.edu.ifpr.quizsi.modelo.Pergunta;

public class AtividadeQuiz extends Activity {
    private static final String ESTADO_INDICE = "indice_pergunta";
    private static final String ESTADO_ALTERNATIVA = "alternativa_selecionada";
    private static final String ESTADO_RESPOSTAS = "respostas_confirmadas";
    private static final String ESTADO_ERRO = "selecao_obrigatoria";

    private final RadioButton[] botoesAlternativas = new RadioButton[4];
    private PartidaQuiz partida;
    private RadioGroup grupoAlternativas;
    private TextView textoProgresso;
    private TextView textoPergunta;
    private TextView textoRetorno;
    private TextView erroSelecao;
    private Button botaoConfirmar;
    private Button botaoProxima;
    private ProgressBar barraProgresso;
    private ScrollView rolagem;
    private boolean atualizandoTela;
    private boolean saindo;
    private boolean selecaoObrigatoria;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        prepararTela();
        carregarPartida(estadoSalvo);
        configurarAcoes();

        if (partida.estaFinalizada()) {
            abrirResultado();
        } else {
            atualizarTela();
        }
    }

    private void prepararTela() {
        setContentView(R.layout.tela_quiz);
        MargensDaTela.aplicar(this);

        rolagem = findViewById(R.id.raiz_tela);
        textoProgresso = findViewById(R.id.texto_progresso);
        barraProgresso = findViewById(R.id.progresso_quiz);
        textoPergunta = findViewById(R.id.texto_pergunta);
        grupoAlternativas = findViewById(R.id.grupo_alternativas);
        botoesAlternativas[0] = findViewById(R.id.alternativa_a);
        botoesAlternativas[1] = findViewById(R.id.alternativa_b);
        botoesAlternativas[2] = findViewById(R.id.alternativa_c);
        botoesAlternativas[3] = findViewById(R.id.alternativa_d);
        botaoConfirmar = findViewById(R.id.botao_confirmar);
        botaoProxima = findViewById(R.id.botao_proxima);
        textoRetorno = findViewById(R.id.texto_retorno);
        erroSelecao = findViewById(R.id.erro_selecao);
    }

    private void carregarPartida(Bundle estadoSalvo) {
        if (estadoSalvo == null) {
            partida = new PartidaQuiz(BancoDePerguntas.criarPerguntas());
            return;
        }

        partida = PartidaQuiz.restaurarPartida(
                BancoDePerguntas.criarPerguntas(),
                estadoSalvo.getInt(ESTADO_INDICE),
                estadoSalvo.getInt(ESTADO_ALTERNATIVA, -1),
                estadoSalvo.getIntArray(ESTADO_RESPOSTAS));
        selecaoObrigatoria = estadoSalvo.getBoolean(ESTADO_ERRO);
    }

    private void configurarAcoes() {
        grupoAlternativas.setOnCheckedChangeListener(
                (grupo, identificadorSelecionado) -> selecionarAlternativa(identificadorSelecionado));
        botaoConfirmar.setOnClickListener(componente -> confirmarResposta());
        botaoProxima.setOnClickListener(componente -> avancarPergunta());
    }

    private void selecionarAlternativa(int identificadorSelecionado) {
        if (atualizandoTela) {
            return;
        }

        for (int indice = 0; indice < botoesAlternativas.length; indice++) {
            if (botoesAlternativas[indice].getId() == identificadorSelecionado
                    && partida.selecionarAlternativa(indice)) {
                selecaoObrigatoria = false;
                erroSelecao.setVisibility(View.GONE);
                return;
            }
        }
    }

    private void confirmarResposta() {
        if (partida.respostaEstaConfirmada() || saindo) {
            return;
        }

        if (!partida.confirmarResposta()) {
            selecaoObrigatoria = true;
            erroSelecao.setVisibility(View.VISIBLE);
            return;
        }

        atualizarTela();
        textoRetorno.post(() -> rolagem.smoothScrollTo(0, textoRetorno.getTop()));
    }

    private void avancarPergunta() {
        if (saindo || !partida.avancarPergunta()) {
            return;
        }

        if (partida.estaFinalizada()) {
            abrirResultado();
        } else {
            atualizarTela();
            rolagem.post(() -> rolagem.scrollTo(0, 0));
        }
    }

    private void atualizarTela() {
        Pergunta pergunta = partida.obterPerguntaAtual();
        boolean respostaConfirmada = partida.respostaEstaConfirmada();

        atualizarProgresso(respostaConfirmada);
        textoPergunta.setText(pergunta.obterEnunciado());
        atualizarAlternativas(pergunta, respostaConfirmada);
        atualizarBotoes(respostaConfirmada);
        atualizarRetorno(pergunta, respostaConfirmada);
        erroSelecao.setVisibility(selecaoObrigatoria ? View.VISIBLE : View.GONE);
    }

    private void atualizarProgresso(boolean respostaConfirmada) {
        int indicePergunta = partida.obterIndicePergunta();
        int totalPerguntas = partida.obterTotalPerguntas();

        textoProgresso.setText(getString(R.string.progresso_pergunta, indicePergunta + 1, totalPerguntas));
        barraProgresso.setMax(totalPerguntas);
        barraProgresso.setProgress(indicePergunta + (respostaConfirmada ? 1 : 0));
    }

    private void atualizarAlternativas(Pergunta pergunta, boolean respostaConfirmada) {
        // Restaurar a seleção na tela não deve contar como uma nova escolha.
        atualizandoTela = true;
        grupoAlternativas.clearCheck();

        for (int indice = 0; indice < botoesAlternativas.length; indice++) {
            botoesAlternativas[indice].setText(pergunta.obterAlternativa(indice));
            botoesAlternativas[indice].setEnabled(!respostaConfirmada);
        }

        int alternativaSelecionada = partida.obterAlternativaSelecionada();
        if (alternativaSelecionada >= 0) {
            grupoAlternativas.check(botoesAlternativas[alternativaSelecionada].getId());
        }
        atualizandoTela = false;
    }

    private void atualizarBotoes(boolean respostaConfirmada) {
        boolean ultimaPergunta = partida.obterIndicePergunta() == partida.obterTotalPerguntas() - 1;

        botaoConfirmar.setEnabled(!respostaConfirmada);
        botaoConfirmar.setText(respostaConfirmada
                ? R.string.resposta_confirmada : R.string.confirmar_resposta);
        botaoProxima.setVisibility(respostaConfirmada ? View.VISIBLE : View.GONE);
        botaoProxima.setText(ultimaPergunta ? R.string.ver_resultado : R.string.proxima_pergunta);
    }

    private void atualizarRetorno(Pergunta pergunta, boolean respostaConfirmada) {
        textoRetorno.setVisibility(respostaConfirmada ? View.VISIBLE : View.GONE);
        if (!respostaConfirmada) {
            return;
        }

        boolean respostaCorreta = partida.respostaAtualEstaCorreta();
        int titulo = respostaCorreta ? R.string.retorno_acerto : R.string.retorno_erro;
        String alternativaCorreta = getString(pergunta.obterAlternativa(pergunta.obterIndiceCorreto()));
        String explicacao = getString(pergunta.obterExplicacao());

        textoRetorno.setText(getString(R.string.formato_retorno, getString(titulo), alternativaCorreta, explicacao));
        textoRetorno.setBackgroundResource(respostaCorreta ? R.drawable.fundo_acerto : R.drawable.fundo_erro);
        textoRetorno.setTextColor(getColor(respostaCorreta ? R.color.verde_escuro : R.color.erro));
    }

    // O Android chama este método antes de recriar a tela, por exemplo ao girar o celular.
    @Override
    protected void onSaveInstanceState(Bundle estado) {
        salvarPartida(estado);
        super.onSaveInstanceState(estado);
    }

    private void salvarPartida(Bundle estado) {
        estado.putInt(ESTADO_INDICE, partida.obterIndicePergunta());
        estado.putInt(ESTADO_ALTERNATIVA, partida.obterAlternativaSelecionada());
        estado.putIntArray(ESTADO_RESPOSTAS, partida.copiarRespostasConfirmadas());
        estado.putBoolean(ESTADO_ERRO, selecaoObrigatoria);
    }

    private void abrirResultado() {
        if (saindo) {
            return;
        }

        saindo = true;
        Intent destino = new Intent(this, AtividadeResultado.class);
        destino.putExtra(AtividadeResultado.CHAVE_PONTUACAO, partida.obterPontuacao());
        destino.putExtra(AtividadeResultado.CHAVE_TOTAL_PERGUNTAS, partida.obterTotalPerguntas());
        startActivity(destino);
        finish(); // Impede voltar para uma partida já finalizada.
    }
}
