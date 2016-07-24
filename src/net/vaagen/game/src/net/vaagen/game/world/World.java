package net.vaagen.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.vaagen.game.controller.LevelLoader;
import net.vaagen.game.view.WorldRenderer;
import net.vaagen.game.world.entity.Player;
import net.vaagen.game.world.projectile.Arrow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Magnus on 2/6/2016.
 */
public class World {

    List<Player> playerList;
    List<Arrow> projectileList;
    List<Cloud> cloudList;
    /** A world has a level through which Player needs to go through **/
    Level level;

    /** The collision boxes **/
    Array<Rectangle> collisionRects = new Array<Rectangle>();

    public World() {
        createWorld();
        projectileList = new ArrayList<>();
        cloudList = new CopyOnWriteArrayList<>();
    }

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

        List<Arrow> deadArrows = new ArrayList<>();
        for (Arrow arrow : getProjectileList())
            if (arrow.isDead())
                deadArrows.add(arrow);
        for (Arrow arrow : deadArrows)
            projectileList.remove(arrow);
    }

    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }
    public Level getLevel() {
        return level;
    }

    // TODO : Fix this fucking mess
    /** Return only the blocks that need to be drawn **/
    public List<Block> getDrawableBlocks(Player player, int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height - 1;
        if (x < 0) {
            x = 0;
        }
/*
        if (y < 0) {
            y = 0;
        }*/

        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 >= level.getWidth()) {
            x2 = level.getWidth() - 1;
        }/*
        if (y2 >= level.getHeight()) {
            y2 = level.getHeight() - 1;
            y = y2 - 2 * height;
            if (y < 0)
                y = 0;
        }*/

        List<Block> blocks = new ArrayList();
        Block block;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                block = level.getBlocks()[col][(row+getLevel().getHeight()) % getLevel().getHeight()];
                if (block != null) {
                    if (row >= getLevel().getHeight())
                        block.setRenderLevel(1);
                    if (row < 0)
                        block.setRenderLevel(-1);
                    //Block b = block;
                    //if () {
                    //    b = new Block(new Vector2(col, row), block.getId());
                    //}
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
    /** Return only the grass that need to be drawn **/
    public List<Grass> getDrawableGrass(Player player, int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height;
        if (x < 0) {
            x = 0;
        }

        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 >= level.getWidth()) {
            x2 = level.getWidth() - 1;
        }
        /*if (y2 >= level.getHeight()) {
            y2 = level.getHeight() - 1;
            y = y2 - 2 * height;
            if (y < 0)
                y = 0;
        }*/

        List<Grass> grasses = new ArrayList();
        Grass[] grass;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                grass = level.getGrass()[col][(row+getLevel().getHeight())%getLevel().getHeight()];
                for (Grass g : grass) {
                    if (g != null) {
                        if (row >= getLevel().getHeight())
                            g.setRenderLevel(1);
                        if (row < 0)
                            g.setRenderLevel(-1);
                        grasses.add(g);
                    }
                }
            }
        }
        return grasses;
    }
    /** Return only the bridges that need to be drawn **/
    public List<Bridge> getDrawableBridges(Player player, int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height;
        if (x < 0) {
            x = 0;
        }

        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 >= level.getWidth()) {
            x2 = level.getWidth() - 1;
        }
        /*if (y2 >= level.getHeight()) {
            y2 = level.getHeight() - 1;
            y = y2 - 2 * height;
            if (y < 0)
                y = 0;
        }*/

        List<Bridge> bridges = new ArrayList();
        Bridge bridge;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                bridge = level.getBridges()[col][(getLevel().getHeight()+row)%getLevel().getHeight()];
                if (bridge != null) {
                    Bridge b = bridge;
                    if (row >= getLevel().getHeight())
                        b.setRenderLevel(1);
                    if (row < 0)
                        b.setRenderLevel(-1);
                    bridges.add(b);
                }
            }
        }
        return bridges;
    }
    public List<Grass> getGrassInRange(float x, float y, float range) {
        return getGrassInRangeWithId(x, y, range, -1);
    }

    /** An ID of -1 can be used to check for grass without any specific id */
    public List<Grass> getGrassInRangeWithId(float x, float y, float range, int id) {
        List<Grass> grassList = new ArrayList<>();

        // Has to be on the same y-level, so no need looping through that
        for (int x2 = (int)(x - Math.ceil(range)); x2 <= x + Math.ceil(range); x2++) {
            if (x2 < 0 || x2 >= level.getWidth() || (int)y < 0 || y >= level.getGrass()[0].length)
                continue;

            Grass[] grasses = level.getGrass()[x2][(int)y];
            for (Grass g : grasses) {
                if (g != null && (id == -1 || g.getId() == id)) {
                    grassList.add(g);
                }
            }
        }

        return grassList;
    }

    public void addPlayer(Player player) {
        playerList.add(player);
        player.respawn(this);
    }

    public void removePlayer(Player player) {
        playerList.remove(player);
    }

    public void addArrow(Arrow arrow) {
        projectileList.add(arrow);
    }

    public List<Arrow> getProjectileList() {
        return projectileList;
    }

    public void removePlayerFromId(int id) {
        removePlayer(getPlayerFromId(id));
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

    public List<Cloud> getCloudList() {
        return cloudList;
    }

    public void addCloud(Cloud cloud) {
        this.cloudList.add(cloud);
    }

    private void createWorld() {
        level = LevelLoader.loadLevel(this, 2);
        playerList = new CopyOnWriteArrayList<>();
    }

}
