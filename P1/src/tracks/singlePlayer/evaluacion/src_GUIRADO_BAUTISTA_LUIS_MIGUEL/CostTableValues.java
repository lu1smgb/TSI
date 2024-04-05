package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import ontology.Types.ACTIONS;

public class CostTableValues {
    
    public double cost;
    public CostTableKey parent;
    public ACTIONS action;

    public CostTableValues() {
        this.cost = Double.POSITIVE_INFINITY;
        this.parent = null;
    }
    
    public CostTableValues(double cost, CostTableKey parent) {
        this.cost = cost;
        this.parent = parent;
    }

}
