package br.edu.ifpr.quizsi;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class TesteFluxoQuiz {
    private void tocar(int identificador) {
        onView(withId(identificador)).perform(scrollTo(), click());
    }

    private void tocarDuasVezes(int identificador) {
        onView(withId(identificador)).perform(scrollTo(), doubleClick());
    }

    private void responderPergunta(int identificadorAlternativa) {
        tocar(identificadorAlternativa);
        tocar(R.id.botao_confirmar);
        tocar(R.id.botao_proxima);
    }

    private void verificarTexto(int identificador, String textoEsperado) {
        onView(withId(identificador)).check(matches(withText(textoEsperado)));
    }

    private void verificarRetorno(String inicioEsperado) {
        onView(withId(R.id.texto_retorno)).check(matches(withText(startsWith(inicioEsperado))));
    }

    private void verificarComponenteVisivel(int identificador) {
        onView(withId(identificador)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }

    private void verificarSelecao(int identificador, boolean selecionada) {
        onView(withId(identificador)).check(matches(selecionada ? isChecked() : not(isChecked())));
    }

    private void verificarHabilitacao(int identificador, boolean habilitado) {
        onView(withId(identificador)).check(matches(habilitado ? isEnabled() : not(isEnabled())));
    }

    @Test
    public void concluiPartidaReiniciaEVoltaAoInicio() {
        try (ActivityScenario<AtividadePrincipal> cenario = ActivityScenario.launch(AtividadePrincipal.class)) {
            tocar(R.id.botao_iniciar);
            tocar(R.id.botao_confirmar);
            verificarComponenteVisivel(R.id.erro_selecao);

            tocar(R.id.alternativa_b);
            tocarDuasVezes(R.id.botao_confirmar);
            verificarRetorno("Você acertou!");
            verificarHabilitacao(R.id.alternativa_a, false);

            tocarDuasVezes(R.id.botao_proxima);
            verificarTexto(R.id.texto_progresso, "Pergunta 2 de 5");
            verificarSelecao(R.id.alternativa_a, false);

            responderPergunta(R.id.alternativa_a); // Erro.
            responderPergunta(R.id.alternativa_a); // Acerto.
            responderPergunta(R.id.alternativa_a); // Erro.
            responderPergunta(R.id.alternativa_b); // Acerto: total de 3 em 5.
            verificarTexto(R.id.texto_pontuacao, "3 de 5");
            verificarTexto(R.id.texto_percentual, "60% de aproveitamento");

            tocar(R.id.botao_jogar_novamente);
            verificarTexto(R.id.texto_progresso, "Pergunta 1 de 5");
            verificarSelecao(R.id.alternativa_b, false);
            pressBack();
            verificarComponenteVisivel(R.id.botao_iniciar);

            tocar(R.id.botao_iniciar);
            int[] alternativasIncorretas = {
                    R.id.alternativa_a, R.id.alternativa_a, R.id.alternativa_b,
                    R.id.alternativa_a, R.id.alternativa_a
            };
            for (int alternativa : alternativasIncorretas) {
                responderPergunta(alternativa);
            }

            verificarTexto(R.id.texto_pontuacao, "0 de 5");
            pressBack(); // A partida finalizada já saiu da pilha.
            verificarComponenteVisivel(R.id.botao_iniciar);
        }
    }

    @Test
    public void recriacaoPreservaSelecaoERespostaConfirmada() {
        try (ActivityScenario<AtividadeQuiz> cenario = ActivityScenario.launch(AtividadeQuiz.class)) {
            tocar(R.id.alternativa_b);
            cenario.recreate();
            verificarSelecao(R.id.alternativa_b, true);
            verificarHabilitacao(R.id.botao_confirmar, true);

            tocar(R.id.botao_confirmar);
            cenario.recreate();
            verificarSelecao(R.id.alternativa_b, true);
            verificarHabilitacao(R.id.alternativa_b, false);
            verificarRetorno("Você acertou!");

            tocar(R.id.botao_proxima);
            tocar(R.id.alternativa_a);
            tocar(R.id.botao_confirmar);
            cenario.recreate();
            verificarTexto(R.id.texto_progresso, "Pergunta 2 de 5");
            verificarRetorno("Ainda não foi desta vez.");

            tocar(R.id.botao_proxima);
            responderPergunta(R.id.alternativa_a);
            responderPergunta(R.id.alternativa_d);
            responderPergunta(R.id.alternativa_b);
            verificarTexto(R.id.texto_pontuacao, "4 de 5");

            tocar(R.id.botao_inicio);
            verificarComponenteVisivel(R.id.botao_iniciar);
        }
    }
}
