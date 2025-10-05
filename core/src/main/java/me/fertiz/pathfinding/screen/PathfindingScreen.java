package me.fertiz.pathfinding.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import me.fertiz.pathfinding.Pathfinding;
import me.fertiz.pathfinding.entity.Entity;
import me.fertiz.pathfinding.input.InputHandler;
import me.fertiz.pathfinding.map.Cell;
import me.fertiz.pathfinding.map.CellType;
import me.fertiz.pathfinding.map.GridMap;
import me.fertiz.pathfinding.pathfinding.Pathfinder;

import java.util.ArrayDeque;

public class PathfindingScreen implements Screen {

    private static final Color CLEAR_COLOR = new Color(0.1f, 0.5f, 0.1f, 1f);

    private static final float WORLD_WIDTH = 16f;
    private static final float WORLD_HEIGHT = 16f;

    private static PathfindingScreen INSTANCE;

    private final OrthographicCamera camera;
    private final ExtendViewport viewport;

    private final InputHandler inputHandler;
    private final Vector2 mouseCoords;

    private final ShapeRenderer shapeRenderer;
    private final GridMap gridMap;

    private final Pathfinder pathfinder;
    private final Entity entity;

    public PathfindingScreen() {
        INSTANCE = this;

        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        this.mouseCoords = new Vector2();

        this.inputHandler = new InputHandler(this);
        Gdx.input.setInputProcessor(inputHandler);

        this.gridMap = new GridMap();

        this.pathfinder = new Pathfinder(gridMap);

        this.entity = new Entity(gridMap.getStartCell().getPosition(), pathfinder);
    }

    @Override
    public void show() {

    }

    private void update() {
        this.updateMouseCoords();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !inputHandler.isDragging()) {
            Cell cell = gridMap.getCell(MathUtils.floor(mouseCoords.x), MathUtils.floor(mouseCoords.y));

            if (cell != null && gridMap.canPlace(cell)) {
                cell.setType(CellType.WALL);
            }
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !inputHandler.isDragging()) {
            Cell cell = gridMap.getCell(MathUtils.floor(mouseCoords.x), MathUtils.floor(mouseCoords.y));

            if (cell != null && gridMap.canPlace(cell)) {
                cell.setType(CellType.PATH);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            Pathfinding.DEBUG = !Pathfinding.DEBUG;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            entity.resetPosition(gridMap.getStartCell().getPosition());
            entity.setTarget(gridMap.getEndCell().getPosition());
        }
    }

    private void updateMouseCoords() {
        mouseCoords.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseCoords);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR);

        this.update();

        viewport.apply(true);

        shapeRenderer.setProjectionMatrix(camera.combined);
        gridMap.render(shapeRenderer, camera);

        entity.render(shapeRenderer, delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Vector2 getMouseCoords() {
        return mouseCoords;
    }

    public GridMap getGridMap() {
        return gridMap;
    }

    public ExtendViewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public static PathfindingScreen getInstance() {
        return INSTANCE;
    }
}
