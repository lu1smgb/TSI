package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

public abstract class AgenteOnline extends PlantillaAgente {

    CostTableRTAStar costTable;

    AgenteOnline(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        super(stateObs, elapsedTimer);
        costTable = new CostTableRTAStar(stateObs);

    }

}
