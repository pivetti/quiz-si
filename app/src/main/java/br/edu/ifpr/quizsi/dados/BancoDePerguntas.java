package br.edu.ifpr.quizsi.dados;

import java.util.Arrays;
import java.util.List;

import br.edu.ifpr.quizsi.R;
import br.edu.ifpr.quizsi.modelo.Pergunta;

public final class BancoDePerguntas {
    private BancoDePerguntas() {
    }

    public static List<Pergunta> criarPerguntas() {
        return Arrays.asList(
                new Pergunta(
                        R.string.pergunta_1,
                        new int[]{R.string.pergunta_1_a, R.string.pergunta_1_b,
                                R.string.pergunta_1_c, R.string.pergunta_1_d},
                        1,
                        R.string.pergunta_1_explicacao),
                new Pergunta(
                        R.string.pergunta_2,
                        new int[]{R.string.pergunta_2_a, R.string.pergunta_2_b,
                                R.string.pergunta_2_c, R.string.pergunta_2_d},
                        2,
                        R.string.pergunta_2_explicacao),
                new Pergunta(
                        R.string.pergunta_3,
                        new int[]{R.string.pergunta_3_a, R.string.pergunta_3_b,
                                R.string.pergunta_3_c, R.string.pergunta_3_d},
                        0,
                        R.string.pergunta_3_explicacao),
                new Pergunta(
                        R.string.pergunta_4,
                        new int[]{R.string.pergunta_4_a, R.string.pergunta_4_b,
                                R.string.pergunta_4_c, R.string.pergunta_4_d},
                        3,
                        R.string.pergunta_4_explicacao),
                new Pergunta(
                        R.string.pergunta_5,
                        new int[]{R.string.pergunta_5_a, R.string.pergunta_5_b,
                                R.string.pergunta_5_c, R.string.pergunta_5_d},
                        1,
                        R.string.pergunta_5_explicacao)
        );
    }
}
