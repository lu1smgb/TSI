package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

public class CostTableValuesAStar {

    public double cost_g;
    public double cost_h;
    public CostTableKey parent;

    public CostTableValuesAStar() {
        this.cost_g = Double.POSITIVE_INFINITY;
        this.cost_h = Double.POSITIVE_INFINITY;
        this.parent = null;
    }

    public CostTableValuesAStar(Double cost_g, Double cost_h, CostTableKey parent) {
        this.cost_g = cost_g;
        this.cost_h = cost_h;
        this.parent = parent;
    }

}
