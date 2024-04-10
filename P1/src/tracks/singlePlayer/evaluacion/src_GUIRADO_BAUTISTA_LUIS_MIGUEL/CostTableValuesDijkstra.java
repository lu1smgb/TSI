package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import ontology.Types.ACTIONS;

public class CostTableValuesDijkstra {
    
    public double cost;
    public CostTableKey parent;

    public CostTableValuesDijkstra() {
        this.cost = Double.POSITIVE_INFINITY;
        this.parent = null;
    }
    
    public CostTableValuesDijkstra(double cost, CostTableKey parent) {
        this.cost = cost;
        this.parent = parent;
    }

}
