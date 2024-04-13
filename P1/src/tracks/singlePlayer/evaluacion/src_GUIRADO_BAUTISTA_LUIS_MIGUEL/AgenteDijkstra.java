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

public class AgenteDijkstra extends AgenteOfflineAbstracto {

    public ArrayDeque<ACTIONS> generateRoute(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        
        // PRINICIPIO DEL ALGORITMO

        // Creamos la tabla de costes
        // Para entender mejor el proposito de la tabla de costes, podemos considerar la tabla como la funcion de coste g(n)
        CostTableDijkstra costTable = new CostTableDijkstra(stateObs);
        CostTableKey initialNode = new CostTableKey(Utilidades.getAvatarGridPosition(stateObs));
        CostTableValuesDijkstra initialNodeValues = new CostTableValuesDijkstra(0, null);
        costTable.put(initialNode, initialNodeValues);

        // Creamos el comparador para las colas de nodos
        NodeComparatorDijkstra comparator = new NodeComparatorDijkstra(costTable);

        // Creamos la cola de nodos abiertos
        // Al empezar tendra el nodo inicial
        PriorityQueue<CostTableKey> nodosAbiertos = new PriorityQueue<>(comparator);
        nodosAbiertos.add(initialNode);

        // Creamos la cola de nodos cerrados
        PriorityQueue<CostTableKey> nodosCerrados = new PriorityQueue<>(comparator);

        // Nodo sobre el que generaremos los siguientes nodos abiertos a lo largo de las iteraciones
        CostTableKey nodoActual = null;

        // Localizamos el objetivo
        Vector2d posicionObjetivo = Utilidades.getNearestObjective(stateObs);

        while (true) {

            nodoActual = nodosAbiertos.poll();

            // Si no hay mas nodos sin explorar
            if (nodoActual == null) {
                if (DEBUG) {
                    System.out.println("El algoritmo ha fallado :(");
                }
                System.exit(-1); // Se muere
            }

            // Si se ha alcanzado el objetivo
            if (Utilidades.mismaPosicion(posicionObjetivo, nodoActual.pos)) {
                if (DEBUG) System.out.println("!!! OBJETIVO ALCANZADO !!!");
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

                // Si es transitable esa posicion, seguimos, si no, pasamos a la siguiente posicion
                if (Utilidades.isTransitable(stateObs, sucesorPos)) {

                    // Generamos el nodo
                    CostTableKey nodoSucesor = new CostTableKey(sucesorPos);

                    // Calculamos la distancia entre los dos nodos
                    double distancia = Utilidades.manhattanDistance(nodoActual.pos, nodoSucesor.pos);

                    // Obtenemos los costes actuales y el estimado
                    CostTableValuesDijkstra nodoActualValues = costTable.get(nodoActual);
                    CostTableValuesDijkstra nodoSucesorValues = costTable.get(nodoSucesor);
                    Double costoActual = nodoActualValues.cost;
                    Double costoSucesor = nodoSucesorValues.cost;
                    double nuevoCosto = costoActual + distancia;

                    // Si el nodo no ha sido explorado y el coste estimado es inferior al del sucesor
                    if (!nodosCerrados.contains(nodoSucesor) && costoSucesor > nuevoCosto) {

                        // Actualizamos el costo y asignamos padre al nodo sucesor
                        CostTableValuesDijkstra valoresSucesor = new CostTableValuesDijkstra(nuevoCosto, nodoActual);
                        costTable.put(nodoSucesor, valoresSucesor);

                        // Insertamos el nodo sucesor en abiertos
                        nodosAbiertos.add(nodoSucesor);

                    }

                }

            }

        }

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
        
        return route;

    }

    public AgenteDijkstra(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        super(stateObs, elapsedTimer);

    }
    
}
