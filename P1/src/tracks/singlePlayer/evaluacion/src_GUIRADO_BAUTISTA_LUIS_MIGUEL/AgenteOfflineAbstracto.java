package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.ArrayDeque;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public abstract class AgenteOfflineAbstracto extends AbstractPlayer {

    final boolean DEBUG = false;

    Vector2d fescala;

    ArrayDeque<ACTIONS> ruta;
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
    
    public AgenteOfflineAbstracto(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        fescala = null;
        ruta = new ArrayDeque<>();
        objetivoAlcanzado = false;
        metricas = new MetricasEvaluacion();

        getScaleFactor(stateObs);
        metricas.iniciarCronometro();
        ruta = generateRoute(stateObs, elapsedTimer);
        metricas.pararCronometro();
        metricas.setSteps(ruta.size());
        mostrarMetricas();

    }

    ArrayDeque<ACTIONS> generateRoute(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return new ArrayDeque<>();
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        ACTIONS siguienteAccion = ACTIONS.ACTION_NIL;

        siguienteAccion = this.ruta.poll();

        return siguienteAccion;

    }

}
