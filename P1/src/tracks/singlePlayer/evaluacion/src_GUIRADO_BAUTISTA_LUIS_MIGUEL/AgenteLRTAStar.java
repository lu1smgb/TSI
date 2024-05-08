package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

// Reutilizamos el codigo del agente RTA ya que solo tiene una pequenia diferencia
public class AgenteLRTAStar extends AgenteRTAStar {

    public AgenteLRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        super(stateObs, elapsedTimer);
        Utilidades.__printMap(stateObs);
        this.isLRTA = true;
    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return super.act(stateObs, elapsedTimer);
    }

}
