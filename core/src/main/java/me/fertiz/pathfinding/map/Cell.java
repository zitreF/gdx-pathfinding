package me.fertiz.pathfinding.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Cell {

    private final int x;
    private final int y;
    private final Vector2 position;
    private CellType type;

    public Cell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.position = new Vector2(x, y);
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public Color getColor() {
        return type.getColor();
    }
}
