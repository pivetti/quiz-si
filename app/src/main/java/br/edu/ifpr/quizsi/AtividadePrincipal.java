package br.edu.ifpr.quizsi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class AtividadePrincipal extends Activity {
    private boolean abrindoQuiz;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        prepararTela();
    }

    private void prepararTela() {
        setContentView(R.layout.tela_inicial);
        MargensDaTela.aplicar(this);

        Button botaoIniciar = findViewById(R.id.botao_iniciar);
        botaoIniciar.setOnClickListener(componente -> iniciarQuiz());
    }

    private void iniciarQuiz() {
        if (abrindoQuiz) {
            return;
        }

        abrindoQuiz = true;
        startActivity(new Intent(this, AtividadeQuiz.class));
    }

    // Permite começar outra partida ao retornar à tela inicial.
    @Override
    protected void onResume() {
        super.onResume();
        abrindoQuiz = false;
    }
}
