package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

public enum BoulderDashMaze {
    
    Simple(122), Extended(123);

    private int id;

    private BoulderDashMaze(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
