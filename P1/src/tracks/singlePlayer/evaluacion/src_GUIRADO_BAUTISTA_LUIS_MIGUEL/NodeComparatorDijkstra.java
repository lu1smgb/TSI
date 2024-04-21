package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.Comparator;

// Comparador utilizado por el agente Dijkstra para determinar que nodos extraer antes de las colas
public class NodeComparatorDijkstra implements Comparator<CostTableKey> {

    CostTableDijkstra costs;

    NodeComparatorDijkstra(CostTableDijkstra costs) {
        this.costs = costs;
    }

    @Override
    public int compare(CostTableKey n1, CostTableKey n2) {

        Double c1 = costs.get(n1).cost;
        Double c2 = costs.get(n2).cost;
        return (int) Math.round(c1 - c2);

    }

}
