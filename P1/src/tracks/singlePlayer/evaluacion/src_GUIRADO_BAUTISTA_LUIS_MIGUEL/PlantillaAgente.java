package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

// Clase raiz de la que heredan todos nuestros agentes
public class PlantillaAgente extends AbstractPlayer {

    final boolean DEBUG = false;

    Vector2d fescala;

    boolean objetivoAlcanzado;

    MetricasEvaluacion metricas;

    public Vector2d getScaleFactor(StateObservation stateObs) {

        if (this.fescala == null) {
            this.fescala = Utilidades.calculateScaleFactor(stateObs);
        }
        return this.fescala;

    }

    void mostrarMetricas() {
        System.out.println(metricas);
    }

    public PlantillaAgente(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        fescala = null;
        objetivoAlcanzado = false;
        metricas = new MetricasEvaluacion();

        getScaleFactor(stateObs);

    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        return ACTIONS.ACTION_NIL;

    }

}
