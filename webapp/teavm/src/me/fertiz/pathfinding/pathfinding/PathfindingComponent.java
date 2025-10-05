package me.fertiz.pathfinding.pathfinding;

import com.badlogic.gdx.math.Vector2;
import me.fertiz.pathfinding.entity.Entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class PathfindingComponent {
    private static final float RECALC_INTERVAL = 2f;
    private static final float WAYPOINT_DIST = 0.1f;
    private static final float TARGET_CHANGE_THRESHOLD = 2f;

    private final Entity entity;
    private final Pathfinder pathfinder;
    private Deque<Vector2> path;
    private Vector2 target;
    private float recalcTimer = 0f;

    private final float speed;

    public PathfindingComponent(Entity entity, Pathfinder pathfinder, float speed) {
        this.entity = entity;
        this.pathfinder = pathfinder;
        this.speed = speed;
    }

    public void update(float delta) {
        if (entity == null || target == null) return;

        this.followPath(delta);

        this.recalcTimer += delta;
        if (recalcTimer >= RECALC_INTERVAL) {
            this.recalcTimer = 0f;
            if (this.shouldRecalcPath()) {
                this.calculatePath(target);
            }
        }
    }

    public void setTarget(Vector2 newTarget) {
        if (newTarget == null) return;
        this.target = new Vector2(newTarget);
        this.calculatePath(target);
        this.recalcTimer = 0f;
    }

    private boolean shouldRecalcPath() {
        if (path == null || path.isEmpty()) return true;
        Vector2 last = path.getLast();
        return last.dst2(target) > TARGET_CHANGE_THRESHOLD * TARGET_CHANGE_THRESHOLD;
    }

    private void calculatePath(Vector2 target) {
        if (entity == null || target == null) {
            return;
        }
//        pathfinder.findPath().thenAccept((newPath) -> {
//            if (newPath == null || newPath.isEmpty()) {
//                return;
//            }
//            this.path = new ArrayDeque<>(newPath);
//            if (!path.isEmpty() && path.getFirst().dst2(entity.getPosition()) < WAYPOINT_DIST * WAYPOINT_DIST) {
//                path.removeFirst();
//            }
//        });
        ArrayDeque<Vector2> newPath = pathfinder.findPath();
        if (newPath == null || newPath.isEmpty()) {
            return;
        }
        this.path = new ArrayDeque<>(newPath);
        if (!path.isEmpty() && path.getFirst().dst2(entity.getPosition()) < WAYPOINT_DIST * WAYPOINT_DIST) {
            path.removeFirst();
        }
    }

    private void followPath(float delta) {
        if (path == null || path.isEmpty()) {
            return;
        }

        Vector2 waypoint = path.getFirst();
        Vector2 pos = entity.getPosition();

        float dist2 = waypoint.dst2(pos);
        if (dist2 < WAYPOINT_DIST * WAYPOINT_DIST) {
            path.removeFirst();
            if (path.isEmpty()) {
                this.clearPath();
            }
            return;
        }

        float invDist = speed / (float) Math.sqrt(dist2) * delta;

        float vx = (waypoint.x - pos.x) * invDist;
        float vy = (waypoint.y - pos.y) * invDist;

        entity.addPosition(vx, vy);
    }

    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }
    public void clearPath() {
        this.path = null;
    }
    public Vector2 getCurrentWaypoint() {
        return path != null ? path.getFirst() : null;
    }
    public List<Vector2> getCurrentPath() {
        return path != null ? List.copyOf(path) : null;
    }

    public void dispose() {
        this.clearPath();
    }
}
