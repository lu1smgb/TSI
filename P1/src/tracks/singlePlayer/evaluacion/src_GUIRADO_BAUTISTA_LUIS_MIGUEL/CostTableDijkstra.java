package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.HashMap;

import core.game.StateObservation;
import tools.Vector2d;

// Tabla de costes para el agente Dijkstra
public class CostTableDijkstra extends HashMap<CostTableKey, CostTableValuesDijkstra> {
    
    public CostTableDijkstra(StateObservation stateObs) {
        super();

        int width = stateObs.getObservationGrid().length;
        int height = stateObs.getObservationGrid()[0].length;

        for (int i=0; i < height; i++) {

            for (int j=0; j < width; j++) {

                Vector2d pos = new Vector2d(j, i);
                CostTableKey key = new CostTableKey(pos);
                // Valores iniciales de cada nodo:
                // Coste G = +infinito (desconocido)
                // Padre = ninguno
                CostTableValuesDijkstra values = new CostTableValuesDijkstra();
                this.put(key, values);

            }

        }

    }

}
