package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.Comparator;
import java.util.HashMap;

public class NodeComparatorAStar implements Comparator<CostTableKey> {

    HashMap<CostTableKey, CostTableValuesAStar>costs;
    boolean compareH;
    boolean compareG;

    NodeComparatorAStar(HashMap<CostTableKey, CostTableValuesAStar> costs, boolean compareG) {
        this.costs = costs;
        this.compareG = compareG;
        this.compareH = !compareG;
    }

    @Override
    public int compare(CostTableKey n1, CostTableKey n2) {

        Double c1 = compareG ? costs.get(n1).cost_g : costs.get(n1).cost_h;
        Double c2 = compareG ? costs.get(n2).cost_g : costs.get(n2).cost_h;
        return (int) Math.round(c1 - c2);

    }

}
