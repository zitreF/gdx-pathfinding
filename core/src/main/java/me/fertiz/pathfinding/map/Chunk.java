package me.fertiz.pathfinding.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Chunk {

    public static final int CHUNK_SIZE = 16;

    private static final Color OUTLINE_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);

    private final int x;
    private final int y;
    private final Cell[][] cells;

    public Chunk(int x, int y) {
        this.x = x;
        this.y = y;
        this.cells = new Cell[CHUNK_SIZE][CHUNK_SIZE];

        // fill cells with default values
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                cells[i][j] = new Cell(x * CHUNK_SIZE + i, y * CHUNK_SIZE + j, CellType.PATH);
            }
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                shapeRenderer.setColor(cell.getColor());
                // Already set to ShapeType.Filled in GridMap#render(ShapeRenderer, OrthographicCamera)
                shapeRenderer.rect(cell.getX(), cell.getY(), GridMap.CELL_SIZE, GridMap.CELL_SIZE);
            }
        }
    }

    public void debug(ShapeRenderer shapeRenderer) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                shapeRenderer.setColor(OUTLINE_COLOR);
                shapeRenderer.rect(cell.getX(), cell.getY(), GridMap.CELL_SIZE, GridMap.CELL_SIZE);
            }
        }
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x * CHUNK_SIZE, y * CHUNK_SIZE, CHUNK_SIZE * GridMap.CELL_SIZE, CHUNK_SIZE * GridMap.CELL_SIZE);
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= CHUNK_SIZE || y < 0 || y >= CHUNK_SIZE) return null;
        return cells[x][y];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Cell[][] getCells() {
        return cells;
    }
}
