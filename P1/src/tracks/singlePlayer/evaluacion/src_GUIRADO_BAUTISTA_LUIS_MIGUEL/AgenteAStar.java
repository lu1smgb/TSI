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

public class AgenteAStar extends AgenteOffline {

    @Override
    public ArrayDeque<ACTIONS> generateRoute(StateObservation stateObs, Vector2d posicionObjetivo) {

        // PRINICIPIO DEL ALGORITMO
        if (DEBUG) System.out.println("### AGENTE A* ###");

        // Creamos la tabla de costes
        // Para entender mejor el proposito de la tabla de costes, podemos considerar la
        // tabla como la funcion de coste g(n)
        CostTableAStar costTable = new CostTableAStar(stateObs, posicionObjetivo);

        // Establecemos los valores de la tabla de costes del nodo inicial
        CostTableKey initialNode = new CostTableKey(Utilidades.getAvatarGridPosition(stateObs));
        CostTableValuesAStar initialNodeValues = costTable.get(initialNode);
        initialNodeValues.cost_g = 0;
        costTable.put(initialNode, initialNodeValues);

        // Creamos el comparador para las colas de nodos
        NodeComparatorAStar comparator = new NodeComparatorAStar(costTable);

        // Creamos la cola de nodos abiertos
        // Al empezar tendra el nodo inicial
        PriorityQueue<CostTableKey> nodosAbiertos = new PriorityQueue<>(comparator);
        nodosAbiertos.add(initialNode);

        // Creamos la cola de nodos cerrados
        PriorityQueue<CostTableKey> nodosCerrados = new PriorityQueue<>(comparator);

        // Nodo sobre el que generaremos los siguientes nodos abiertos a lo largo de las
        // iteraciones
        CostTableKey nodoActual = null;

        // Mientras haya nodos en la cola de abiertos
        while (!nodosAbiertos.isEmpty()) {

            // Sacamos el nodo con menor coste H de abiertos
            nodoActual = nodosAbiertos.poll();

            // Si no hay mas nodos que explorar y no se ha encontrado un camino al objetivo
            if (nodoActual == null) {
                if (DEBUG) System.out.println("El algoritmo ha fallado :(");
                System.exit(-1); // Se muere
            }

            // Si se ha alcanzado el objetivo
            if (Utilidades.mismaPosicion(posicionObjetivo, nodoActual.pos)) {
                if (DEBUG) System.out.println("!!! OBJETIVO ALCANZADO !!!");
                this.objetivoAlcanzado = true;
                break; // El algoritmo acaba con exito
            }
            metricas.nodes++;

            // Ponemos el nodo actual en cerrados
            nodosCerrados.add(nodoActual);

            // Generamos las posiciones adyacentes al nodo actual
            HashMap<Types.ACTIONS, Vector2d> adyacentes = Utilidades.generateAdyacentPositions(stateObs, nodoActual.pos);

            // Para cada nodo adyacente, para cada sucesor
            for (Map.Entry<Types.ACTIONS, Vector2d> sucesor : adyacentes.entrySet()) {

                // Obtenemos la posicion
                ACTIONS accionSucesor = sucesor.getKey();
                Vector2d sucesorPos = sucesor.getValue();

                // Si es transitable esa posicion, seguimos, si no, pasamos a la siguiente
                // posicion
                if (Utilidades.isTransitable(stateObs, sucesorPos)) {

                    // Generamos el nodo
                    CostTableKey nodoSucesor = new CostTableKey(sucesorPos);

                    // Obtenemos los costes G
                    ACTIONS orientation = Utilidades.orientationToAction(stateObs.getAvatarOrientation());
                    CostTableValuesAStar nodoActualValues = costTable.get(nodoActual);
                    CostTableValuesAStar nodoSucesorValues = costTable.get(nodoSucesor);
                    Double actualCost_g = nodoActualValues.cost_g;
                    Double sucesorCost_g = nodoSucesorValues.cost_g;
                    Double actionCost = orientation == accionSucesor ? 1d : 2d;
                    Double estimadoCost_g = actualCost_g + actionCost;

                    // Si el nodo es explorado pero el coste estimado G es mejor
                    if (nodosCerrados.contains(nodoSucesor) && estimadoCost_g < sucesorCost_g) {

                        // Eliminamos el nodo de cerrados
                        nodosCerrados.remove(nodoSucesor);

                        // Actualizamos el coste G
                        nodoSucesorValues.cost_g = estimadoCost_g;
                        nodoSucesorValues.parent = nodoActual;
                        costTable.put(nodoSucesor, nodoSucesorValues);

                        // Lo pasamos a abiertos
                        nodosAbiertos.add(nodoSucesor);
                    }
                    // Si el nodo no ha sido visitado y no ha sido vecino de ningun otro nodo
                    else if (!nodosCerrados.contains(nodoSucesor) && !nodosAbiertos.contains(nodoSucesor)) {

                        // Lo aniadimos a abiertos
                        nodoSucesorValues.parent = nodoActual;
                        costTable.put(nodoSucesor, nodoSucesorValues);
                        nodosAbiertos.add(nodoSucesor);

                    }
                    // Si el nodo no ha sido visitado y el coste G es mejor
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

            // Agregamos la accion a la ruta, el actual pasa a ser el padre y
            // obtenemos su respectivo padre
            route.addFirst(accion);
            nodoActual = nodoPadre;
            nodoPadre = costTable.get(nodoActual).parent;

        }

        // Modificamos la ruta teniendo en cuenta la orientacion
        // Desde el principio hasta el final
        ArrayDeque<ACTIONS> new_route = new ArrayDeque<ACTIONS>();
        ACTIONS orientacion = Utilidades.orientationToAction(stateObs.getAvatarOrientation());
        nodoActual = initialNode;
        while (true) {

            ACTIONS accion = route.poll();

            // Si ya no hay mas acciones en la ruta anterior
            if (accion == null) {
                break;
            }

            // Si el agente no esta mirando a donde tiene que moverse
            if (orientacion != accion) {
                // Le indicamos primero que gire en esa direccion
                new_route.add(accion);
                orientacion = accion;
            }

            // Agregamos la accion a la ruta nueva
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
