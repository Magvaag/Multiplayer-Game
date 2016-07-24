package net.vaagen.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 10/15/2015.
 */
public class Level {

    private int width;
    private int height;
    private Block[][] blocks;
    private Grass[][][] grass;
    private Bridge[][] bridges;
    private Vector2 spawnPosition;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public void setGrass(Grass[][][] grass) {
        this.grass = grass;
    }

    public Grass[][][] getGrass() {
        return grass;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

    public Bridge[][] getBridges() {
        return bridges;
    }

    public void setBridges(Bridge[][] bridges) {
        this.bridges = bridges;
    }

    public void setWorld(World world) {
        // Currently only for grass
        for (int x = 0; x < grass.length; x++) {
            for (int y = 0; y < grass[0].length; y++) {
                for (int z = 0; z < grass[0][0].length; z++) {
                    if (grass[x][y][z] != null)
                        grass[x][y][z].setWorld(world);
                }
            }
        }
    }

    public Block get(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            return null;

        return blocks[x][y];
    }

    public Vector2 getSpawnPosition() {
        return spawnPosition;
    }

    public List<Rectangle> getBlockRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();

        for (int x = 0; x < blocks.length; x++)
            for (int y = 0; y < blocks[0].length; y++)
                if (blocks[x][y] != null)
                    rectangles.add(blocks[x][y].getBounds());

        return rectangles;
    }

    public List<Rectangle> getBridgeRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();

        for (int x = 0; x < bridges.length; x++)
            for (int y = 0; y < bridges[0].length; y++)
                if (bridges[x][y] != null)
                    rectangles.add(bridges[x][y].getBounds());

        return rectangles;
    }

    public List<Rectangle> getCollisionRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();

        rectangles.addAll(getBlockRectangles());
        rectangles.addAll(getBridgeRectangles());

        return rectangles;
    }

    public void setSpawnPosition(Vector2 spanPosition) {
        this.spawnPosition = spanPosition;
    }

}