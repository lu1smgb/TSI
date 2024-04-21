package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import core.game.StateObservation;
import java.util.ArrayDeque;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public abstract class AgenteOffline extends PlantillaAgente {

    ArrayDeque<ACTIONS> ruta;
    
    public AgenteOffline(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        super(stateObs, elapsedTimer);

        ruta = new ArrayDeque<>();

    }

    ArrayDeque<ACTIONS> generateRoute(StateObservation stateObs, Vector2d posicionObjetivo) {
        return new ArrayDeque<>();
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        ACTIONS siguienteAccion = ACTIONS.ACTION_NIL;

        // Si no se ha generado la ruta a seguir
        if (ruta.isEmpty()) {

            // Localizamos el objetivo
            Vector2d posicionObjetivo = Utilidades.getNearestObjective(stateObs);

            // Generamos la ruta y medimos estadisticas
            metricas.iniciarCronometro();
            ruta = generateRoute(stateObs, posicionObjetivo);
            metricas.pararCronometro();
            metricas.setSteps(ruta.size());

            // Mostramos estadisticas
            mostrarMetricas();

        }

        // Sacamos la siguiente accion de la ruta
        siguienteAccion = this.ruta.poll();

        // Realiza dicha accion
        return siguienteAccion;

    }

}
