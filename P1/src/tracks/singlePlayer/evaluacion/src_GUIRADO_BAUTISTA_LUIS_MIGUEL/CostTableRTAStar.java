package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.HashMap;

import core.game.StateObservation;
import tools.Vector2d;

// Tabla de costes para el agente RTA*
public class CostTableRTAStar extends HashMap<CostTableKey, CostTableValuesAStar> {

    public CostTableRTAStar(StateObservation stateObs) {
        super();
        
        int width = stateObs.getObservationGrid().length;
        int height = stateObs.getObservationGrid()[0].length;

        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {

                Vector2d pos = new Vector2d(j, i);
                CostTableKey key = new CostTableKey(pos);
                // Valores iniciales de cada nodo:
                // Coste G = +infinito (no se utiliza)
                // Coste H = +infinito (desconocido)
                // Padre = null (no se utiliza)
                CostTableValuesAStar values = new CostTableValuesAStar(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, null);
                this.put(key, values);

            }

        }

    }

}
