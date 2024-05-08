package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.HashMap;
import java.util.Map;

import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteRTAStar extends AgenteOnline {

    boolean isLRTA;
    boolean firstActCall;

    public AgenteRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        super(stateObs, elapsedTimer);
        isLRTA = false;
        firstActCall = true;

    }

    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        // Si es la primera llamada, empezamos a medir el tiempo
        if (firstActCall) {
            metricas.iniciarCronometro();
            firstActCall = false;
        }

        ACTIONS siguienteAccion = ACTIONS.ACTION_NIL;

        // Obtenemos la posicion y la orientacion actuales del avatar
        Vector2d posicionActual = Utilidades.getAvatarGridPosition(stateObs);
        CostTableKey nodoActual = new CostTableKey(posicionActual);
        CostTableValuesAStar nodoActualValues = costTable.get(nodoActual);
        ACTIONS orientacion = Utilidades.orientationToAction(stateObs.getAvatarOrientation());

        // Posicion del objetivo, utilizada unicamente para comprobaciones y actualizaciones de h(x)
        Vector2d posicionObjetivo = Utilidades.getNearestObjective(stateObs);

        // Si el nodo no tiene un coste h definido, lo inicializamos con la heuristica
        if (nodoActualValues.cost_h == Double.POSITIVE_INFINITY) {
            double distancia = Utilidades.manhattanDistance(posicionActual, posicionObjetivo);
            nodoActualValues.cost_h = distancia;
            costTable.put(nodoActual, nodoActualValues);
        }

        // Espacio de busqueda
        HashMap<ACTIONS, Vector2d> adyacentes = Utilidades.generateAdyacentPositions(stateObs, posicionActual);

        // Mejor coste (primer minimo) y segundo minimo (para la regla de aprendizaje en RTA)
        double bestCost = Double.POSITIVE_INFINITY;
        double secondBestCost = Double.POSITIVE_INFINITY;

        // Iteramos sobre los nodos del espacio actual de busqueda y escogemos
        // la accion relacionada con el mejor vecino
        for (Map.Entry<ACTIONS, Vector2d> sucesor: adyacentes.entrySet()) {

            // Informacion del nodo sucesor
            Vector2d posicionSucesor = sucesor.getValue();
            CostTableKey nodoSucesor = new CostTableKey(posicionSucesor);
            CostTableValuesAStar valoresSucesor = costTable.get(nodoSucesor);
            ACTIONS accionSucesor = sucesor.getKey();

            // Si no es muro o obstaculo
            if (Utilidades.isTransitable(stateObs, posicionSucesor)) {

                metricas.nodes++;

                // Si el nodo sucesor no tiene valor heuristico, le asignamos un valor
                if (valoresSucesor.cost_h == Double.POSITIVE_INFINITY) {
                    double distancia = Utilidades.manhattanDistance(posicionSucesor, posicionObjetivo);
                    valoresSucesor.cost_h = distancia;
                    costTable.put(nodoSucesor, valoresSucesor);
                }

                // Si se ha visto la posicion objetivo, le indicamos que ha
                // "alcanzado el objetivo" al final de la iteracion para
                // que termine de medir el tiempo y muestre los resultados
                if (Utilidades.mismaPosicion(posicionObjetivo, posicionSucesor)) {
                    objetivoAlcanzado = true;
                }

                double heuristicCost = valoresSucesor.cost_h; // h(y)
                //double movementCost = orientacion == accionSucesor ? 1d : 2d; // Coste(x,y)
                double movementCost = 1d;
                double calculatedCost = movementCost + heuristicCost; // Coste(x,y) + h(y)

                // Si el coste calculado es menor que el primer minimo
                if (calculatedCost < bestCost) {

                    // Asignamos el primer minimo al segundo minimo 
                    // si este es menor que el segundo minimo
                    if (bestCost < secondBestCost && !isLRTA) {
                        secondBestCost = bestCost;
                    }

                    // Actualizamos el primer minimo
                    bestCost = calculatedCost;
                    siguienteAccion = accionSucesor;

                }
                // Si el coste calculado es mayor que el primero pero menor que el segundo,
                // actualizamos el segundo
                else if (calculatedCost < secondBestCost && !isLRTA) {
                    secondBestCost = calculatedCost;
                }

            }

        }

        // Si hay primer minimo pero no hay segundo minimo, le asignamos el valor del primero
        if (bestCost != Double.POSITIVE_INFINITY && secondBestCost == Double.POSITIVE_INFINITY && !isLRTA) {
            secondBestCost = bestCost;
        }

        // Aplicamos la regla de aprendizaje
        nodoActualValues.cost_h = Math.max(nodoActualValues.cost_h, (isLRTA ? bestCost : secondBestCost));
        costTable.put(nodoActual, nodoActualValues);

        // Si se ha alcanzado el objetivo, paramos de medir el tiempo y mostramos los resultados
        if (objetivoAlcanzado) {
            metricas.pararCronometro();
            mostrarMetricas();
        }

        metricas.steps++;
        return siguienteAccion;

    }

}
