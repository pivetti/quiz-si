package br.edu.ifpr.quizsi;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/** Mantém o conteúdo fora das barras do sistema e dos recortes da tela. */
final class MargensDaTela {
    private MargensDaTela() {
    }

    static void aplicar(Activity atividade) {
        WindowCompat.setDecorFitsSystemWindows(atividade.getWindow(), false);
        View conteudoTela = atividade.findViewById(R.id.raiz_tela);

        ViewCompat.setOnApplyWindowInsetsListener(conteudoTela, (componente, margensJanela) -> {
            Insets margens = margensJanela.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            componente.setPadding(margens.left, margens.top, margens.right, margens.bottom);
            return margensJanela;
        });

        WindowInsetsControllerCompat controleBarras =
                WindowCompat.getInsetsController(atividade.getWindow(), conteudoTela);
        controleBarras.setAppearanceLightStatusBars(true);
        controleBarras.setAppearanceLightNavigationBars(true);
        ViewCompat.requestApplyInsets(conteudoTela);
    }
}
