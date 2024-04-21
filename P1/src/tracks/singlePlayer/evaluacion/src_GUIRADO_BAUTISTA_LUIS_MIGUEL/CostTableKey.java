package tracks.singlePlayer.evaluacion.src_GUIRADO_BAUTISTA_LUIS_MIGUEL;

import java.util.Objects;

import tools.Vector2d;

// Clase "clave de tabla" usada para obtener y actualizar los datos de las tablas de costes
public class CostTableKey {
    
    public Vector2d pos;

    public CostTableKey(double x, double y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    public CostTableKey(Vector2d v) {
        this.pos = v;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CostTableKey that = (CostTableKey) obj;
        return this.pos.x == that.pos.x && this.pos.y == that.pos.y;

    }

    @Override
    public int hashCode() {
        return Objects.hash(pos.x,pos.y);
    }

}
