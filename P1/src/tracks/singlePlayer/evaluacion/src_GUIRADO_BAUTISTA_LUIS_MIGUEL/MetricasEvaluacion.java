package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

// Clase usada para medir las estadisticas requeridas por la memoria de la practica
public class MetricasEvaluacion {

    protected long tinicio;
    protected long tfin;
    
    protected long runtime;
    protected long steps;
    protected long nodes;

    MetricasEvaluacion() {
        tinicio = tfin = runtime = steps = nodes = 0;
    }

    void iniciarCronometro() {
        tinicio = System.nanoTime();
    }

    void pararCronometro() {
        if (tinicio != 0) {
            tfin = System.nanoTime();
            runtime = (tfin - tinicio) / 1000000;
        }
        else {
            System.err.println("No se puede parar el cronometro porque no se ha iniciado");
        }
    }

    void setSteps(long steps) { this.steps = steps; }

    void setNodes(long nodes) { this.nodes = nodes; }

    @Override
    public String toString() {
        return "Tiempo: " + runtime + " ms\n" +
               "Pasos: " + steps + "\n" +
               "Nodos expandidos: " + nodes;
    }

}
