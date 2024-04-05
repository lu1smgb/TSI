package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.HashMap;

import core.game.StateObservation;
import tools.Vector2d;

public class CostTable extends HashMap<CostTableKey, CostTableValues> {
    
    public CostTable(StateObservation stateObs) {
        super();

        int width = stateObs.getObservationGrid().length;
        int height = stateObs.getObservationGrid()[0].length;

        for (int i=0; i < height; i++) {

            for (int j=0; j < width; j++) {

                Vector2d pos = new Vector2d(j, i);
                CostTableKey key = new CostTableKey(pos);
                CostTableValues values = new CostTableValues();
                this.put(key, values);

            }

        }

    }

}
