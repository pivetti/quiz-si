package br.edu.ifpr.quizsi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AtividadeResultado extends Activity {
    public static final String CHAVE_PONTUACAO = "br.edu.ifpr.quizsi.PONTUACAO";
    public static final String CHAVE_TOTAL_PERGUNTAS = "br.edu.ifpr.quizsi.TOTAL";

    private boolean navegando;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        setContentView(R.layout.tela_resultado);
        MargensDaTela.aplicar(this);
        exibirResultado();
        configurarAcoes();
    }

    private void exibirResultado() {
        int totalPerguntas = Math.max(1, getIntent().getIntExtra(CHAVE_TOTAL_PERGUNTAS, 5));
        int pontuacaoRecebida = getIntent().getIntExtra(CHAVE_PONTUACAO, 0);
        int pontuacao = Math.max(0, Math.min(totalPerguntas, pontuacaoRecebida));
        int percentual = Math.round(pontuacao * 100f / totalPerguntas);

        TextView textoPontuacao = findViewById(R.id.texto_pontuacao);
        TextView textoPercentual = findViewById(R.id.texto_percentual);
        TextView mensagemResultado = findViewById(R.id.mensagem_resultado);

        textoPontuacao.setText(getString(R.string.formato_pontuacao, pontuacao, totalPerguntas));
        textoPercentual.setText(getString(R.string.formato_percentual, percentual));
        mensagemResultado.setText(obterMensagemResultado(percentual));
    }

    private int obterMensagemResultado(int percentual) {
        if (percentual == 100) {
            return R.string.resultado_perfeito;
        }
        if (percentual >= 60) {
            return R.string.resultado_bom;
        }
        return R.string.resultado_praticar;
    }

    private void configurarAcoes() {
        findViewById(R.id.botao_jogar_novamente).setOnClickListener(componente -> jogarNovamente());
        findViewById(R.id.botao_inicio).setOnClickListener(componente -> voltarAoInicio());
    }

    private void jogarNovamente() {
        if (navegando) {
            return;
        }

        navegando = true;
        startActivity(new Intent(this, AtividadeQuiz.class));
        finish();
    }

    private void voltarAoInicio() {
        if (navegando) {
            return;
        }

        navegando = true;
        Intent destino = new Intent(this, AtividadePrincipal.class);
        destino.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(destino);
        finish();
    }
}
