package me.fertiz.pathfinding;

import com.badlogic.gdx.Game;
import me.fertiz.pathfinding.screen.PathfindingScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Pathfinding extends Game {

    public static boolean DEBUG = false;

    @Override
    public void create() {
        this.setScreen(new PathfindingScreen());
    }

    @Override
    public void render() {
        super.render();
    }
}
