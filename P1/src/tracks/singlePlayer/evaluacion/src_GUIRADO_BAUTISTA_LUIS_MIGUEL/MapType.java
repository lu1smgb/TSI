package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

// Enumeracion para representar los 3 mapas por defecto de cada juego
public enum MapType {
    
    Small(0), Medium(1), Large(2);

    private int id;

    private MapType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
