package net.vaagen.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import net.vaagen.game.controller.LevelLoader;
import net.vaagen.game.view.WorldRenderer;
import net.vaagen.game.world.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 2/6/2016.
 */
public class World {

    /** Our player controlled hero **/
    Player player;
    /** A world has a level through which Player needs to go through **/
    Level level;

    /** The collision boxes **/
    Array<Rectangle> collisionRects = new Array<Rectangle>();

    public void update() {
        for (Grass grass : getDrawableGrass((int)Math.ceil(WorldRenderer.CAMERA_WIDTH), (int)Math.ceil(WorldRenderer.CAMERA_HEIGHT)))
            grass.update();
    }

    // Getters -----------

    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }
    public Player getPlayer() {
        return player;
    }
    public Level getLevel() {
        return level;
    }
    /** Return only the blocks that need to be drawn **/
    public List<Block> getDrawableBlocks(int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height;
        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }
        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 >= level.getWidth()) {
            x2 = level.getWidth() - 1;
        }
        if (y2 >= level.getHeight()) {
            y2 = level.getHeight() - 1;
        }

        List<Block> blocks = new ArrayList();
        Block block;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                block = level.getBlocks()[col][row];
                if (block != null) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    /** Return only the blocks that need to be drawn **/
    public List<Grass> getDrawableGrass(int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height;
        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }
        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 >= level.getWidth()) {
            x2 = level.getWidth() - 1;
        }
        if (y2 >= level.getHeight()) {
            y2 = level.getHeight() - 1;
        }

        List<Grass> grasses = new ArrayList();
        Grass[] grass;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                grass = level.getGrass()[col][row];
                for (Grass g : grass) {
                    if (g != null) {
                        grasses.add(g);
                    }
                }
            }
        }
        return grasses;
    }

    // --------------------
    public World() {
        createWorld();
    }

    private void createWorld() {
        level = LevelLoader.loadLevel(2);
        player = new Player(level.getSpanPosition());
    }

}
