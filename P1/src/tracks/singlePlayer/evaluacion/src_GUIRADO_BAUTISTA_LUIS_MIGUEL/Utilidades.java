package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.ArrayList;
import java.util.HashMap;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

public class Utilidades {

    static final ACTIONS[] ordenAcciones = {
            ACTIONS.ACTION_UP,
            ACTIONS.ACTION_DOWN,
            ACTIONS.ACTION_LEFT,
            ACTIONS.ACTION_RIGHT };
    
    public Utilidades() {}

    public static Vector2d adjustToScaleFactor(Vector2d fescala, Vector2d vector) {

        return new Vector2d(vector.x / fescala.x, vector.y / fescala.y);

    }

    public static Vector2d calculateScaleFactor(StateObservation stateObs) {

        return new Vector2d(
                stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
                stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

    }

    public static Vector2d getAvatarGridPosition(StateObservation stateObs) {

        Vector2d fescala = calculateScaleFactor(stateObs);
        return adjustToScaleFactor(fescala, stateObs.getAvatarPosition());

    }

    public static Vector2d getNearestObjective(StateObservation stateObs) {

        ArrayList<Observation>[] objectives = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        Vector2d fescala = calculateScaleFactor(stateObs);

        Vector2d nearestObjective = objectives[0].get(0).position;
        nearestObjective = adjustToScaleFactor(fescala, nearestObjective);
        nearestObjective.x = Math.floor(nearestObjective.x);
        nearestObjective.y = Math.floor(nearestObjective.y);

        return nearestObjective;

    }

    public static boolean outOfBounds(StateObservation stateObs, Vector2d position) {

        return position.x < 0 || position.x > stateObs.getObservationGrid().length - 1 ||
               position.y < 0 || position.y > stateObs.getObservationGrid()[0].length - 1;

    }

    public static boolean isTransitable(StateObservation stateObs, Vector2d position) {

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
    // Si la accion no se puede realizar porque saldria del mapa, entonces el nodo
    // sera nulo
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

}
