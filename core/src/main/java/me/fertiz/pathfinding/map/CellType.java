package me.fertiz.pathfinding.map;

import com.badlogic.gdx.graphics.Color;

public enum CellType {
    WALL(Color.WHITE),
    TEST(Color.RED),
    PATH(Color.BLACK);

    private final Color color;

    CellType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
