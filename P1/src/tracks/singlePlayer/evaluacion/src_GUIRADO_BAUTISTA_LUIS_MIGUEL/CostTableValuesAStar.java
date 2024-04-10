package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

public class CostTableValuesAStar {

    public double cost_h;
    public double cost_g;
    public CostTableKey parent;

    public CostTableValuesAStar() {
        this.cost_h = Double.POSITIVE_INFINITY;
        this.cost_g = Double.POSITIVE_INFINITY;
        this.parent = null;
    }

    public CostTableValuesAStar(Double cost_h, Double cost_g, CostTableKey parent) {
        this.cost_h = cost_h;
        this.cost_g = cost_g;
        this.parent = parent;
    }

}
