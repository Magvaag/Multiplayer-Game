package net.vaagen.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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

    List<Player> playerList;
    /** A world has a level through which Player needs to go through **/
    Level level;
    Vector2 spawnPosition;

    /** The collision boxes **/
    Array<Rectangle> collisionRects = new Array<Rectangle>();

    public void update() {
        Grass[][][] grasses = level.getGrass();
        for (int x = 0; x < grasses.length; x++) {
            for (int y = 0; y < grasses[x].length; y++) {
                for (int z = 0; z < grasses[x][y].length; z++) {
                    Grass grass = grasses[x][y][z];
                    if (grass != null)
                        grass.update();
                }
            }
        }
    }

    // Getters -----------

    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }
    public Level getLevel() {
        return level;
    }

    /** Return only the blocks that need to be drawn **/
    public List<Block> getDrawableBlocks(Player player, int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height - 1;
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
    public List<Grass> getDrawableGrass(Player player, int width, int height) {
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

    public List<Grass> getGrassInRangeWithId(float x, float y, float range, int id) {
        List<Grass> grassList = new ArrayList<>();

        // Has to be on the same y-level, so no need looping through that
        for (int x2 = (int)(x - Math.ceil(range)); x2 <= x + Math.ceil(range); x2++) {
            if (x2 < 0 || x2 >= level.getWidth())
                continue;

            Grass[] grasses = level.getGrass()[x2][(int)y];
            for (Grass g : grasses) {
                if (g != null && g.getId() == id) {
                    grassList.add(g);
                }
            }
        }

        return grassList;
    }

    // --------------------
    public World() {
        createWorld();
    }

    public void addPlayer(Player player) {
        playerList.add(player);
        player.setPosition(spawnPosition.cpy());
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Player getPlayerFromId(int id) {
        for (Player player : playerList) {
            //System.out.println("I found a player with the id of " + player.getPlayerId() + ", but it is not " + id + " like you searched for..");
            if (player.getPlayerId() == id)
                return player;
        }

        return null;
    }

    private void createWorld() {
        level = LevelLoader.loadLevel(this, 2);
        playerList = new ArrayList<>();
        spawnPosition = level.getSpanPosition();
    }

}
