package me.fertiz.pathfinding.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectSet;
import me.fertiz.pathfinding.Pathfinding;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GridMap {

    public static final int CELL_SIZE = 1;

    private Cell startCell;
    private Cell endCell;
    private final Map<Long, Chunk> chunks;

    public GridMap() {
        this.chunks = new HashMap<>(128);
        this.startCell = this.getOrCreateCell(0, 0);
        this.endCell = this.getOrCreateCell(15, 15);
    }

    public void render(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        Gdx.gl20.glLineWidth(4f / camera.zoom);
        this.updateVisibleChunks(camera);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Chunk chunk : chunks.values()) {
            chunk.render(shapeRenderer);
        }

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(startCell.getX() * CELL_SIZE + CELL_SIZE / 2f, startCell.getY() * CELL_SIZE + CELL_SIZE / 2f, CELL_SIZE / 2f);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(endCell.getX() * CELL_SIZE + CELL_SIZE / 2f, endCell.getY() * CELL_SIZE + CELL_SIZE / 2f, CELL_SIZE / 2f);

        if (Pathfinding.DEBUG) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            for (Chunk chunk : chunks.values()) {
                chunk.debug(shapeRenderer);
            }
        }
        shapeRenderer.end();
    }

    public Cell getStartCell() {
        return startCell;
    }

    public void setStartCell(Cell startCell) {
        this.startCell = startCell;
    }

    public Cell getEndCell() {
        return endCell;
    }

    public void setEndCell(Cell endCell) {
        this.endCell = endCell;
    }

    public boolean canPlace(Cell cell) {
        return !(startCell.equals(cell) || endCell.equals(cell));
    }

    private long getChunkKey(int x, int y) {
        return ((long) x << 32) | ((y & 0xFFFFFFFFL));
    }

    public Chunk getChunk(float x, float y) {
        int chunkX = Math.floorDiv(MathUtils.floor(x), Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv(MathUtils.floor(y), Chunk.CHUNK_SIZE);
        return chunks.get(this.getChunkKey(chunkX, chunkY));
    }

    private Chunk getOrCreateChunk(int chunkX, int chunkY) {
        long key = this.getChunkKey(chunkX, chunkY);
        Chunk existing = chunks.get(key);
        if (existing != null) return existing;
        Chunk created = new Chunk(chunkX, chunkY);
        chunks.put(key, created);
        return created;
    }

    public boolean isWalkable(Cell cell) {
        return cell.getType() == CellType.PATH;
    }

    public Cell getCell(int worldX, int worldY) {
        int chunkX = Math.floorDiv(worldX, Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.CHUNK_SIZE);

        Chunk chunk = chunks.get(this.getChunkKey(chunkX, chunkY));

        if (chunk == null) return null;

        int localX = Math.floorMod(worldX, Chunk.CHUNK_SIZE);
        int localY = Math.floorMod(worldY, Chunk.CHUNK_SIZE);

        return chunk.getCell(localX, localY);
    }

    public Cell getOrCreateCell(int worldX, int worldY) {
        int chunkX = Math.floorDiv(worldX, Chunk.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldY, Chunk.CHUNK_SIZE);
        Chunk chunk = this.getOrCreateChunk(chunkX, chunkY);
        int localX = Math.floorMod(worldX, Chunk.CHUNK_SIZE);
        int localY = Math.floorMod(worldY, Chunk.CHUNK_SIZE);
        return chunk.getCell(localX, localY);
    }

    private final ObjectSet<Chunk> visibleChunks = new ObjectSet<>();

    private void updateVisibleChunks(OrthographicCamera camera) {
        visibleChunks.clear();

        float scaledViewWidth = camera.viewportWidth * camera.zoom;
        float scaledViewHeight = camera.viewportHeight * camera.zoom;

        float startX = (camera.position.x - scaledViewWidth / 2f) / CELL_SIZE;
        float startY = (camera.position.y - scaledViewHeight / 2f) / CELL_SIZE;
        float endX = (camera.position.x + scaledViewWidth / 2f) / CELL_SIZE;
        float endY = (camera.position.y + scaledViewHeight / 2f) / CELL_SIZE;

        int minChunkX = MathUtils.floor(startX / Chunk.CHUNK_SIZE);
        int maxChunkX = MathUtils.ceil(endX / Chunk.CHUNK_SIZE);
        int minChunkY = MathUtils.floor(startY / Chunk.CHUNK_SIZE);
        int maxChunkY = MathUtils.ceil(endY / Chunk.CHUNK_SIZE);

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int y = minChunkY; y <= maxChunkY; y++) {
                long key = this.getChunkKey(x, y);

                final int finalX = x;
                final int finalY = y;

                visibleChunks.add(chunks.computeIfAbsent(key, ignored -> new Chunk(finalX, finalY)));
            }
        }
    }

    public Collection<Chunk> getLoadedChunks() {
        return chunks.values();
    }
}
