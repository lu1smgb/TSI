package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.ArrayList;
import java.util.HashMap;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

// Funciones y variables de utilidad utilizadas por los agentes
public class Utilidades {

    // Orden de las acciones de los nodos expandidos
    static final ACTIONS[] ordenAcciones = {
            ACTIONS.ACTION_UP,
            ACTIONS.ACTION_DOWN,
            ACTIONS.ACTION_LEFT,
            ACTIONS.ACTION_RIGHT };
    
    public Utilidades() {}

    // Devuelve el vector ajustado al factor de escala proporcionado
    public static Vector2d adjustToScaleFactor(Vector2d fescala, Vector2d vector) {

        return new Vector2d(vector.x / fescala.x, vector.y / fescala.y);

    }

    // Calcula el factor de escala del entorno del juego
    public static Vector2d calculateScaleFactor(StateObservation stateObs) {

        return new Vector2d(
                stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
                stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

    }

    // Obtiene la posicion del avatar en el contexto de casillas del juego
    public static Vector2d getAvatarGridPosition(StateObservation stateObs) {

        Vector2d fescala = calculateScaleFactor(stateObs);
        return adjustToScaleFactor(fescala, stateObs.getAvatarPosition());

    }

    // Obtiene el objetivo mas cercano
    public static Vector2d getNearestObjective(StateObservation stateObs) {

        ArrayList<Observation>[] objectives = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        Vector2d fescala = calculateScaleFactor(stateObs);

        Vector2d nearestObjective = objectives[0].get(0).position;
        nearestObjective = adjustToScaleFactor(fescala, nearestObjective);
        nearestObjective.x = Math.floor(nearestObjective.x);
        nearestObjective.y = Math.floor(nearestObjective.y);

        return nearestObjective;

    }

    // Comprueba que la posicion dada no salga del mapa
    public static boolean outOfBounds(StateObservation stateObs, Vector2d position) {

        return position.x < 0 || position.x > stateObs.getObservationGrid().length - 1 ||
               position.y < 0 || position.y > stateObs.getObservationGrid()[0].length - 1;

    }

    // Comprueba que la posicion dada pueda ser transitada por un agente
    public static boolean isTransitable(StateObservation stateObs, Vector2d position) {

        // Comprobamos que la posicion no este fuera del mapa
        if (outOfBounds(stateObs, position)) {
            return false;
        }

        // Obtenemos las observaciones de dicha posicion
        ArrayList<Observation> observations = stateObs.getObservationGrid()[(int) position.x][(int) position.y];

        // Si ha encontrado algun objeto
        if (!observations.isEmpty()) {
            for (Observation obs : observations) {
                // Y es un muro u obstaculo
                if (obs.category == 4) {
                    // La posicion no es transitable
                    return false;
                }
            }
        }

        // No se ha encontrado ningun obstaculo en esa posicion, true
        return true;

    }

    // Genera las posiciones adyacentes a una posicion origen
    // Para ello se emplea un HashMap que asocia una accion a una posicion
    public static HashMap<ACTIONS, Vector2d> generateAdyacentPositions(StateObservation stateObs, Vector2d origen) {

        HashMap<ACTIONS, Vector2d> adyacentes = new HashMap<ACTIONS, Vector2d>();

        for (ACTIONS accion : ordenAcciones) {

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

    // Distancia Manhattan, heuristica principal del agente
    public static double manhattanDistance(Vector2d n1, Vector2d n2) {

        return Math.abs(n2.x - n1.x) + Math.abs(n2.y - n1.y);

    }

    // Convierte un vector que representa la orientacion dada la funcion `stateObs.getAvatarOrientation()`
    // en un elemento de tipo ACTIONS (por ejemplo si el agente mira arriba, devuelve la accion arriba)
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

    // Comprueba que los dos vectores son iguales
    // (por algun motivo esta comprobacion supuestamente se realiza en la clase Vector2d, pero
    // a la hora de ejecutar esto no ocurre, por lo que se emplea esta funcion)
    public static boolean mismaPosicion(Vector2d v1, Vector2d v2) {
        return v1.x == v2.x && v1.y == v2.y;
    }

    // Funcion de depuracion usada durante la implementacion para
    // comprobar las categorias de las observaciones del mapa, no se
    // utiliza en las implementaciones finales
    public static void __printMap(StateObservation stateObs) {

        int width = stateObs.getObservationGrid().length;
        int height = stateObs.getObservationGrid()[0].length;

        for (int i = 0; i < height; i++) {

            String str = ""; // debug

            for (int j = 0; j < width; j++) {

                ArrayList<Observation> _obs = stateObs.getObservationGrid()[j][i];
                if (!_obs.isEmpty()) {
                    Observation obs = _obs.get(0);
                    int category = obs.category;
                    str += category;
                } else {
                    str += "-";
                }

            }

            // debug
            System.out.println(str);

        }

    }

}
