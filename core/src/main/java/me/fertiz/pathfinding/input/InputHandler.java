package me.fertiz.pathfinding.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import me.fertiz.pathfinding.map.Cell;
import me.fertiz.pathfinding.map.GridMap;
import me.fertiz.pathfinding.screen.PathfindingScreen;

public class InputHandler extends InputAdapter {

    private final PathfindingScreen pathfinding;

    public InputHandler(PathfindingScreen pathfinding) {
        this.pathfinding = pathfinding;
    }

    private boolean draggingStart = false;
    private boolean draggingEnd = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        GridMap map = pathfinding.getGridMap();
        Cell clicked = this.getHoveredCell(pointer);
        if (clicked == null) return false;

        if (clicked.equals(map.getStartCell())) {
            this.draggingStart = true;
        } else if (clicked.equals(map.getEndCell())) {
            this.draggingEnd = true;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        GridMap map = pathfinding.getGridMap();
        Cell hovered = this.getHoveredCell(pointer);
        if (hovered == null) return false;

        if (draggingStart && map.canPlace(hovered)) {
            map.setStartCell(hovered);
        } else if (draggingEnd && map.canPlace(hovered)) {
            map.setEndCell(hovered);
        }
        return true;
    }

    private Cell getHoveredCell(int pointer) {
        if (pointer != Input.Buttons.LEFT) return null;
        GridMap map = pathfinding.getGridMap();
        Vector2 mousePos = pathfinding.getMouseCoords();
        return map.getOrCreateCell(MathUtils.floor(mousePos.x), MathUtils.floor(mousePos.y));
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.draggingStart = false;
        this.draggingEnd = false;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        OrthographicCamera camera = pathfinding.getCamera();
        if (amountY > 0) {
            // zoom in
            float zoom = camera.zoom + 0.2f;
            camera.zoom = Math.min(zoom, 10f);
        } else if (amountY < 0) {
            // zoom out
            float zoom = camera.zoom - 0.2f;
            camera.zoom = Math.max(zoom, 1f);
        }
        return super.scrolled(amountX, amountY);
    }

    public boolean isDragging() {
        return draggingStart || draggingEnd;
    }
}
