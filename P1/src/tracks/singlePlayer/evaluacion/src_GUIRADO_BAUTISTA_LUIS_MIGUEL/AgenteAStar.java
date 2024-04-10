package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteAStar extends AbstractPlayer {

    final boolean DEBUG = true;
    final ACTIONS[] ordenAcciones = {
            ACTIONS.ACTION_UP,
            ACTIONS.ACTION_DOWN,
            ACTIONS.ACTION_LEFT,
            ACTIONS.ACTION_RIGHT };

    Vector2d fescala = null;
    ArrayDeque<ACTIONS> ruta;
    boolean objetivoAlcanzado;

    public Vector2d adjustToScaleFactor(Vector2d vector) {

        return new Vector2d(vector.x / this.fescala.x, vector.y / this.fescala.y);

    }

    public Vector2d getScaleFactor(StateObservation stateObs) {

        if (this.fescala == null) {
            this.fescala = new Vector2d(
                    stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
                    stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
        }
        return this.fescala;

    }

    public Vector2d getAvatarGridPosition(StateObservation stateObs) {

        return adjustToScaleFactor(stateObs.getAvatarPosition());

    }

    public Vector2d getNearestObjective(StateObservation stateObs) {

        ArrayList<Observation>[] objectives = stateObs.getPortalsPositions(stateObs.getAvatarPosition());

        Vector2d nearestObjective = objectives[0].get(0).position;
        nearestObjective = adjustToScaleFactor(nearestObjective);
        nearestObjective.x = Math.floor(nearestObjective.x);
        nearestObjective.y = Math.floor(nearestObjective.y);

        return nearestObjective;

    }

    public static boolean outOfBounds(StateObservation stateObs, Vector2d position) {

        return position.x < 0 || position.x > stateObs.getObservationGrid().length - 1 ||
               position.y < 0 || position.y > stateObs.getObservationGrid()[0].length - 1;

    }

    boolean isTransitable(StateObservation stateObs, Vector2d position) {

        // Comprobamos que la posicion no este fuera del mapa
        if (outOfBounds(stateObs, position)) {
            return false;
        }

        // Comprobamos que el avatar pueda moverse a esa posicion
        ArrayList<Observation> observations = stateObs.getObservationGrid()[(int) position.x][(int) position.y];
        // Si no existe ningun objeto
        if (!observations.isEmpty()) {
            for (Observation obs : observations) {
                if (obs.category == 4) {
                    return false;
                }
            }
        }

        return true;

    }

    // Genera los nodos adyacentes a un nodo origen
    // Para ello se emplea un HashMap que asocia una accion a un nodo
    public HashMap<ACTIONS, Vector2d> generateAdyacentPositions(StateObservation stateObs, Vector2d origen) {

        HashMap<ACTIONS, Vector2d> adyacentes = new HashMap<ACTIONS, Vector2d>();

        for (ACTIONS accion : this.ordenAcciones) {

            Vector2d pos = null;
            switch (accion) {
                case ACTION_UP:
                    pos = new Vector2d(origen.x, origen.y - 1);
                    break;
                case ACTION_DOWN:
                    pos = new Vector2d(origen.x, origen.y + 1);
                    break;
                case ACTION_LEFT:
                    pos = new Vector2d(origen.x - 1, origen.y);
                    break;
                case ACTION_RIGHT:
                    pos = new Vector2d(origen.x + 1, origen.y);
                    break;
                default:
                    break;
            }
            adyacentes.put(accion, pos);

        }

        return adyacentes;

    }

    // Distancia Manhattan
    public static double manhattanDistance(Vector2d n1, Vector2d n2) {

        return Math.abs(n2.x - n1.x) + Math.abs(n2.y - n1.y);

    }

    public static ACTIONS orientationToAction(Vector2d orientation) {
        if (orientation.x == 1)
            return ACTIONS.ACTION_RIGHT;
        if (orientation.x == -1)
            return ACTIONS.ACTION_LEFT;
        if (orientation.y == 1)
            return ACTIONS.ACTION_DOWN;
        if (orientation.y == -1)
            return ACTIONS.ACTION_UP;
        return ACTIONS.ACTION_NIL;
    }

    public static boolean mismaPosicion(Vector2d v1, Vector2d v2) {
        return v1.x == v2.x && v1.y == v2.y;
    }

    public ArrayDeque<ACTIONS> generateRoute(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        // PRINICIPIO DEL ALGORITMO
        // Empezamos a cronometrar el runtime
        long tini = System.nanoTime();

        // Localizamos el objetivo
        Vector2d posicionObjetivo = getNearestObjective(stateObs);

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
                CostTableValuesAStar values = new CostTableValuesAStar(manhattanDistance(pos, posicionObjetivo), Double.POSITIVE_INFINITY, null);
                costTable.put(key, values);
            }
        }

        CostTableKey initialNode = new CostTableKey(getAvatarGridPosition(stateObs));
        CostTableValuesAStar initialNodeValues = costTable.get(initialNode);
        initialNodeValues.cost_g = 0;
        costTable.put(initialNode, initialNodeValues);

        // Creamos el comparador para las colas de nodos
        NodeComparatorAStar comparatorH = new NodeComparatorAStar(costTable, false);
        NodeComparatorAStar comparatorG = new NodeComparatorAStar(costTable, true);

        // Creamos la cola de nodos abiertos
        // Al empezar tendra el nodo inicial
        PriorityQueue<CostTableKey> nodosAbiertos = new PriorityQueue<>(comparatorH);
        nodosAbiertos.add(initialNode);

        // Creamos la cola de nodos cerrados
        PriorityQueue<CostTableKey> nodosCerrados = new PriorityQueue<>(comparatorH);

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
            if (mismaPosicion(posicionObjetivo, nodoActual.pos)) {
                if (DEBUG)
                    System.out.println("!!! OBJETIVO ALCANZADO !!!");
                this.objetivoAlcanzado = true;
                break; // El algoritmo acaba con exito
            }

            // Ponemos el nodo actual en cerrados
            nodosCerrados.add(nodoActual);

            // Generamos las posiciones adyacentes al nodo actual
            HashMap<Types.ACTIONS, Vector2d> adyacentes = generateAdyacentPositions(stateObs, nodoActual.pos);

            // Para cada nodo adyacente, para cada sucesor
            for (Map.Entry<Types.ACTIONS, Vector2d> sucesor : adyacentes.entrySet()) {

                // Obtenemos la posicion
                Vector2d sucesorPos = sucesor.getValue();

                // Si es transitable esa posicion, seguimos, si no, pasamos a la siguiente
                // posicion
                if (isTransitable(stateObs, sucesorPos)) {

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
            ACTIONS accion = orientationToAction(diferencia);

            route.addFirst(accion);
            nodoActual = nodoPadre;
            nodoPadre = costTable.get(nodoActual).parent;

        }

        // Despues la modificamos teniendo en cuenta la orientacion
        // Desde el principio hasta el final
        ArrayDeque<ACTIONS> new_route = new ArrayDeque<ACTIONS>();
        ACTIONS orientacion = orientationToAction(stateObs.getAvatarOrientation());
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
        long tfin = System.nanoTime();
        long tiempoTotal = (tfin - tini) / 1000000;
        System.out.println("Ruta generada en: " + tiempoTotal + " ms");
        System.out.println("Tamanio de la ruta: " + route.size());
        System.out.println("Nodos expandidos: " + nodosCerrados.size());

        return route;

    }

    public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        getScaleFactor(stateObs);

        this.objetivoAlcanzado = false;

        this.ruta = generateRoute(stateObs, elapsedTimer);

    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        ACTIONS siguienteAccion = ACTIONS.ACTION_NIL;

        siguienteAccion = this.ruta.poll();

        return siguienteAccion;

    }
}
