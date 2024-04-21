package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.Comparator;
import java.util.HashMap;

// Comparador utilizado por el agente A* para determinar que nodos extraer antes de las colas
public class NodeComparatorAStar implements Comparator<CostTableKey> {

    HashMap<CostTableKey, CostTableValuesAStar> costs;

    NodeComparatorAStar(HashMap<CostTableKey, CostTableValuesAStar> costs) {
        this.costs = costs;
    }

    @Override
    public int compare(CostTableKey n1, CostTableKey n2) {

        CostTableValuesAStar v1 = costs.get(n1);
        CostTableValuesAStar v2 = costs.get(n2);
        double c1 = v1.cost_g + v1.cost_h;
        double c2 = v2.cost_g + v2.cost_h;
        return (int) Math.round(c1 - c2);

    }

}
