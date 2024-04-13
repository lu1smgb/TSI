package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import core.game.StateObservation;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteAStar extends AgenteOfflineAbstracto {

    public ArrayDeque<ACTIONS> generateRoute(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        // PRINICIPIO DEL ALGORITMO

        // Localizamos el objetivo
        Vector2d posicionObjetivo = Utilidades.getNearestObjective(stateObs);

        // Creamos la tabla de costes
        // Para entender mejor el proposito de la tabla de costes, podemos considerar la
        // tabla como la funcion de coste g(n)
        HashMap<CostTableKey, CostTableValuesAStar> costTable = new HashMap<>();
        int width = stateObs.getObservationGrid().length;
        int height = stateObs.getObservationGrid()[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Vector2d pos = new Vector2d(j, i);
                CostTableKey key = new CostTableKey(pos);
                double distanciaInicial = Utilidades.manhattanDistance(pos, posicionObjetivo);
                CostTableValuesAStar values = new CostTableValuesAStar(distanciaInicial, Double.POSITIVE_INFINITY, null);
                costTable.put(key, values);
            }
        }

        CostTableKey initialNode = new CostTableKey(Utilidades.getAvatarGridPosition(stateObs));
        CostTableValuesAStar initialNodeValues = costTable.get(initialNode);
        initialNodeValues.cost_g = 0;
        costTable.put(initialNode, initialNodeValues);

        // Creamos el comparador para las colas de nodos
        NodeComparatorAStar comparator = new NodeComparatorAStar(costTable, false);

        // Creamos la cola de nodos abiertos
        // Al empezar tendra el nodo inicial
        PriorityQueue<CostTableKey> nodosAbiertos = new PriorityQueue<>(comparator);
        nodosAbiertos.add(initialNode);

        // Creamos la cola de nodos cerrados
        PriorityQueue<CostTableKey> nodosCerrados = new PriorityQueue<>(comparator);

        // Nodo sobre el que generaremos los siguientes nodos abiertos a lo largo de las
        // iteraciones
        CostTableKey nodoActual = null;

        while (true) {

            // Sacamos el nodo con menor coste H de abiertos
            nodoActual = nodosAbiertos.poll();

            // Si no hay mas nodos que explorar y no se ha encontrado un camino al objetivo
            if (nodoActual == null) {
                if (DEBUG) {
                    System.out.println("El algoritmo ha fallado :(");
                }
                System.exit(-1); // Se muere
            }

            // Si se ha alcanzado el objetivo
            if (Utilidades.mismaPosicion(posicionObjetivo, nodoActual.pos)) {
                if (DEBUG)
                    System.out.println("!!! OBJETIVO ALCANZADO !!!");
                this.objetivoAlcanzado = true;
                break; // El algoritmo acaba con exito
            }

            // Ponemos el nodo actual en cerrados
            nodosCerrados.add(nodoActual);

            // Generamos las posiciones adyacentes al nodo actual
            HashMap<Types.ACTIONS, Vector2d> adyacentes = Utilidades.generateAdyacentPositions(stateObs, nodoActual.pos);
            metricas.nodes++;

            // Para cada nodo adyacente, para cada sucesor
            for (Map.Entry<Types.ACTIONS, Vector2d> sucesor : adyacentes.entrySet()) {

                // Obtenemos la posicion
                Vector2d sucesorPos = sucesor.getValue();

                // Si es transitable esa posicion, seguimos, si no, pasamos a la siguiente
                // posicion
                if (Utilidades.isTransitable(stateObs, sucesorPos)) {

                    // Generamos el nodo
                    CostTableKey nodoSucesor = new CostTableKey(sucesorPos);

                    CostTableValuesAStar nodoActualValues = costTable.get(nodoActual);
                    CostTableValuesAStar nodoSucesorValues = costTable.get(nodoSucesor);

                    Double actualCost_g = nodoActualValues.cost_g;
                    Double sucesorCost_g = nodoSucesorValues.cost_g;
                    Double estimadoCost_g = actualCost_g + 1;

                    if (nodosCerrados.contains(nodoSucesor) && estimadoCost_g < sucesorCost_g) {

                        nodosCerrados.remove(nodoSucesor);

                        // Actualizamos el coste G
                        nodoSucesorValues.cost_g = estimadoCost_g;
                        nodoSucesorValues.parent = nodoActual;
                        costTable.put(nodoSucesor, nodoSucesorValues);

                        nodosAbiertos.add(nodoSucesor);
                    }
                    else if (!nodosCerrados.contains(nodoSucesor) && !nodosAbiertos.contains(nodoSucesor)) {

                        nodoSucesorValues.parent = nodoActual;
                        costTable.put(nodoSucesor, nodoSucesorValues);
                        nodosAbiertos.add(nodoSucesor);

                    }
                    else if (nodosAbiertos.contains(nodoSucesor) && estimadoCost_g < sucesorCost_g) { 

                        // Actualizamos el coste G
                        nodoSucesorValues.cost_g = estimadoCost_g;
                        nodoSucesorValues.parent = nodoActual;
                        costTable.put(nodoSucesor, nodoSucesorValues);

                    }

                }

            }

        }

        // ***** CONSTRUCCION DE LA RUTA *****

        // Una vez alcanzado el nodo objetivo, generamos la ruta
        ArrayDeque<ACTIONS> route = new ArrayDeque<ACTIONS>();

        // Primero generamos la ruta sin tener en cuenta la orientacion
        // Desde el final hasta el inicio
        CostTableKey nodoPadre = costTable.get(nodoActual).parent;
        while (nodoPadre != null) {

            // Estimamos la accion a realizar a partir de las posiciones del nodo
            // actual y su padre
            double diferencia_x = nodoActual.pos.x - nodoPadre.pos.x;
            double diferencia_y = nodoActual.pos.y - nodoPadre.pos.y;
            Vector2d diferencia = new Vector2d(diferencia_x, diferencia_y);
            ACTIONS accion = Utilidades.orientationToAction(diferencia);

            route.addFirst(accion);
            nodoActual = nodoPadre;
            nodoPadre = costTable.get(nodoActual).parent;

        }

        // Despues la modificamos teniendo en cuenta la orientacion
        // Desde el principio hasta el final
        ArrayDeque<ACTIONS> new_route = new ArrayDeque<ACTIONS>();
        ACTIONS orientacion = Utilidades.orientationToAction(stateObs.getAvatarOrientation());
        nodoActual = initialNode;
        while (true) {

            ACTIONS accion = route.poll();

            if (accion == null) {
                break;
            }

            if (orientacion != accion) {
                new_route.add(accion);
                orientacion = accion;
            }

            new_route.add(accion);

        }
        route = new_route;

        // FIN DEL ALGORITMO

        return route;

    }

    public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        super(stateObs, elapsedTimer);

    }

}
