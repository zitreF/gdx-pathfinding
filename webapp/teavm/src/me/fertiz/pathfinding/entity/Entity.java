package me.fertiz.pathfinding.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.fertiz.pathfinding.Pathfinding;
import me.fertiz.pathfinding.pathfinding.Pathfinder;
import me.fertiz.pathfinding.pathfinding.PathfindingComponent;

import java.util.List;

public class Entity {

    private final Vector2 position;
    private final PathfindingComponent pathfindingComponent;

    public Entity(Vector2 position, Pathfinder pathfinder) {
        this.position = new Vector2(position).add(0.5f, 0.5f); // center entity on grid cell
        this.pathfindingComponent = new PathfindingComponent(this, pathfinder, 1f);
    }

    public void render(ShapeRenderer shapeRenderer, float delta) {
        pathfindingComponent.update(delta);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(position.x, position.y, 0.4f, 32);

        this.debug(shapeRenderer);

        shapeRenderer.end();
    }

    private void debug(ShapeRenderer shapeRenderer) {
        List<Vector2> path = pathfindingComponent.getCurrentPath();
        if (path != null && path.size() > 1) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.CYAN);
            for (int i = 0; i < path.size() - 1; i++) {
                Vector2 from = path.get(i);
                Vector2 to = path.get(i + 1);
                shapeRenderer.line(from.x, from.y, to.x, to.y);
            }

            Vector2 currentWaypoint = pathfindingComponent.getCurrentWaypoint();
            if (currentWaypoint != null) {
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.circle(currentWaypoint.x, currentWaypoint.y, 0.3f, 32);
            }
        }
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void addPosition(float x, float y) {
        this.position.add(x, y);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void resetPosition(Vector2 position) {
        this.position.set(position).add(0.5f, 0.5f);
    }

    public void setTarget(Vector2 newTarget) {
        this.pathfindingComponent.setTarget(newTarget);
    }
}
