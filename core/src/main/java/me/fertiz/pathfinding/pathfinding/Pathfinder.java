package me.fertiz.pathfinding.pathfinding;

import com.badlogic.gdx.math.Vector2;
import me.fertiz.pathfinding.map.Cell;
import me.fertiz.pathfinding.map.CellType;
import me.fertiz.pathfinding.map.Chunk;
import me.fertiz.pathfinding.map.GridMap;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Pathfinder {

    private final GridMap map;

    private static final int MARGIN = 0;
    private static final int[] DX = {1, -1, 0, 0, 1, 1, -1, -1};
    private static final int[] DY = {0, 0, 1, -1, 1, -1, 1, -1};

    public Pathfinder(GridMap map) {
        this.map = map;
    }

//    public CompletableFuture<ArrayDeque<Vector2>> findPath() {
//        return CompletableFuture.supplyAsync(() -> {
//            if (map == null) return new ArrayDeque<>();
//            Vector2 startPos = map.getStartCell().getPosition().cpy().add(0.5f, 0.5f); // offset to center cell
//            Vector2 endPos = map.getEndCell().getPosition().cpy().add(0.5f, 0.5f); // offset to center cell
//            Node s = Node.of(startPos);
//            Node e = Node.of(endPos);
//            if (!this.isWalkable(e.x, e.y)) return new ArrayDeque<>();
//
//            PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
//            Map<Node, Node> cameFrom = new HashMap<>();
//            Map<Node, Float> gScore = new HashMap<>();
//
//            open.add(s);
//            gScore.put(s, 0f);
//
//            while (!open.isEmpty()) {
//                Node cur = open.poll();
//                if (cur.equals(e)) {
//                    return new ArrayDeque<>(this.reconstruct(cameFrom, cur, startPos, endPos));
//                }
//
//                for (int i = 0; i < 8; i++) {
//                    int nx = cur.x + DX[i];
//                    int ny = cur.y + DY[i];
//
//                    if (!this.isWalkable(nx, ny)) continue;
//
//                    if (i >= 4) {
//                        int adj1x = cur.x + DX[i];
//                        int adj1y = cur.y;
//                        int adj2x = cur.x;
//                        int adj2y = cur.y + DY[i];
//
//                        if (!this.isWalkable(adj1x, adj1y) || !this.isWalkable(adj2x, adj2y)) {
//                            continue;
//                        }
//                    }
//
//                    Node neigh = new Node(nx, ny);
//                    float tentative = gScore.get(cur) + cur.dst(neigh);
//                    if (tentative < gScore.getOrDefault(neigh, Float.MAX_VALUE)) {
//                        cameFrom.put(neigh, cur);
//                        gScore.put(neigh, tentative);
//                        neigh.g = tentative;
//                        neigh.h = neigh.dst(e);
//                        open.add(neigh);
//                    }
//                }
//            }
//
//            return new ArrayDeque<>();
//        });
//    }

    public ArrayDeque<Vector2> findPath() {
        if (map == null) return new ArrayDeque<>();
        Vector2 startPos = map.getStartCell().getPosition().cpy().add(0.5f, 0.5f); // offset to center cell
        Vector2 endPos = map.getEndCell().getPosition().cpy().add(0.5f, 0.5f); // offset to center cell
        Node s = Node.of(startPos);
        Node e = Node.of(endPos);
        if (!this.isWalkable(e.x, e.y)) return new ArrayDeque<>();

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Float> gScore = new HashMap<>();

        open.add(s);
        gScore.put(s, 0f);

        while (!open.isEmpty()) {
            Node cur = open.poll();
            if (cur.equals(e)) {
                return new ArrayDeque<>(this.reconstruct(cameFrom, cur, startPos, endPos));
            }

            for (int i = 0; i < 8; i++) {
                int nx = cur.x + DX[i];
                int ny = cur.y + DY[i];

                if (!this.isWalkable(nx, ny)) continue;

                if (i >= 4) {
                    int adj1x = cur.x + DX[i];
                    int adj1y = cur.y;
                    int adj2x = cur.x;
                    int adj2y = cur.y + DY[i];

                    if (!this.isWalkable(adj1x, adj1y) || !this.isWalkable(adj2x, adj2y)) {
                        continue;
                    }
                }

                Node neigh = new Node(nx, ny);
                float tentative = gScore.get(cur) + cur.dst(neigh);
                if (tentative < gScore.getOrDefault(neigh, Float.MAX_VALUE)) {
                    cameFrom.put(neigh, cur);
                    gScore.put(neigh, tentative);
                    neigh.g = tentative;
                    neigh.h = neigh.dst(e);
                    open.add(neigh);
                }
            }
        }

        return new ArrayDeque<>();
    }

    private boolean isWalkable(int x, int y) {
        Cell cell = this.getCell(x, y);
        if (cell == null) return false;

        if (cell.getType() == CellType.WALL) return false;

        for (int dx = -MARGIN; dx <= MARGIN; dx++) {
            for (int dy = -MARGIN; dy <= MARGIN; dy++) {
                Cell neighbor = this.getCell(x + dx, y + dy);
                if (neighbor != null && !map.isWalkable(neighbor)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Cell getCell(int x, int y) {
        Cell[][] cells = this.getCellsAt(x, y);
        return (cells == null) ? null : cells[x & 15][y & 15];
    }

    private Cell[][] getCellsAt(int x, int y) {
        Chunk chunk = map.getChunk(x, y);
        return chunk != null ? chunk.getCells() : null;
    }

    private List<Vector2> reconstruct(Map<Node, Node> cameFrom, Node cur,
                                      Vector2 start, Vector2 end) {
        Deque<Vector2> path = new ArrayDeque<>();
        while (cur != null) {
            path.addFirst(new Vector2(cur.x + 0.5f, cur.y + 0.5f));
            cur = cameFrom.get(cur);
        }
        List<Vector2> result = new ArrayList<>(path);
        if (!result.isEmpty()) {
            result.set(0, new Vector2(start)); // start
            result.set(result.size() - 1, new Vector2(end)); // end
        }
        return result;
    }
}
