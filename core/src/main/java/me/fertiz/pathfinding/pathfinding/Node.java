package me.fertiz.pathfinding.pathfinding;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class Node {
    public final int x, y;
    public float g, h;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Node of(Vector2 v) {
        return new Node(MathUtils.floor(v.x), MathUtils.floor(v.y));
    }

    public float f() {
        return g + h;
    }

    public float dst(Node o) {
        return Vector2.len(o.x - x, o.y - y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    @Override
    public boolean equals(Object o) {
        return (o instanceof Node n) && n.x == x && n.y == y;
    }
}
