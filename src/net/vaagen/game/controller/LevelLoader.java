package net.vaagen.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.world.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Magnus on 10/15/2015.
 */
public class LevelLoader {

    private static final String LEVEL_PREFIX    = "levels/level-";

    private static final int    BLOCK           = 0x000000; // black
    private static final int    EMPTY           = 0xffffff; // white
    private static final int    START_POS       = 0x0000ff; // blue

    public static Level loadLevel(World world, int number) {
        Level level = new Level();

        // Loading the png into a Pixmap
        Pixmap pixmap = new Pixmap(Gdx.files.internal(LEVEL_PREFIX + number + ".png"));

        // setting the size of the level based on the size of the pixmap
        level.setWidth(pixmap.getWidth());
        level.setHeight(pixmap.getHeight());

        // creating the backing blocks array
        Block[][] blocks = new Block[level.getWidth()][level.getHeight()];
        for (int col = 0; col < level.getWidth(); col++) {
            for (int row = 0; row < level.getHeight(); row++) {
                blocks[col][row] = null;
            }
        }

        Random random = new Random();
        for (int row = 0; row < level.getHeight(); row++) {
            for (int col = 0; col < level.getWidth(); col++) {
                int pixel = (pixmap.getPixel(col, row) >>> 8) & 0xffffff;
                Color color = new Color(pixel);
                int iRow = level.getHeight() - 1 - row;
                if (color.getBlue() == 0) { // It's a block if the color does not contain blue
                    // adding a block
                    blocks[col][iRow] = new Block(new Vector2(col, iRow), new Color(pixel).getRed());
                } else if (pixel == START_POS) {
                    level.setSpawnPosition(new Vector2(col, iRow));
                }
            }
        }

        // setting the blocks
        level.setBlocks(blocks);

        Grass[][][] grasses = new Grass[blocks.length][blocks[0].length][4]; // Max three layers with grass :O

        applyGrass(grasses, 6, 1, 3);
        applyGrass(grasses, 14, 1, 5);
        applyGrass(grasses, 7, 5, 2);
        applyGrass(grasses, 9, 6, 1);
        applyGrass(grasses, 1, 8, 4);
        applyGrass(grasses, 15, 8, 4);
        applyGrass(grasses, 1, 4, 1);

        Bridge[][] bridges = new Bridge[blocks.length][blocks[0].length];
        bridges[1][0] = new Bridge(new Vector2(1, 0));
        bridges[2][0] = new Bridge(new Vector2(2, 0));
        bridges[3][0] = new Bridge(new Vector2(3, 0));

        bridges[16][0] = new Bridge(new Vector2(16, 0));
        bridges[17][0] = new Bridge(new Vector2(17, 0));
        bridges[18][0] = new Bridge(new Vector2(18, 0));
        level.setBridges(bridges);

        level.setGrass(grasses);
        level.setWorld(world);
        return level;
    }

    private static void applyGrass(Grass[][][] grasses, int startX, int startY, int length) {
        addGrass(grasses, startX, startY, 1);
        addGrass(grasses, startX, startY, 0);
        if (length > 1) {
            addGrass(grasses, startX, startY, startX + 0.5F, startY, 3);
            addGrass(grasses, startX, startY, startX + 0.5F, startY, 2);

            for (int x = startX + 1; x < startX + length - 1; x++) {
                addGrass(grasses, x, startY, 3);
                addGrass(grasses, x, startY, 2);
                addGrass(grasses, x, startY, x + 0.5F, startY, 3);
                addGrass(grasses, x, startY, x + 0.5F, startY, 2);
            }

            addGrass(grasses, startX + length - 1, startY, 5);
            addGrass(grasses, startX + length - 1, startY, 4);
        }
    }

    private static void addGrass(Grass[][][] grasses, int x, int y, float realX, float realY, int type) {
        int layer = 0;
        while(layer < grasses[x][y].length && grasses[x][y][layer] != null)
            layer++;
        if (layer >= grasses[x][y].length) {
            System.out.println("Not enough grass at this position!");
            return;
        }
        grasses[x][y][layer] = new Grass(new Vector2(realX, realY), type);
    }

    private static void addGrass(Grass[][][] grasses, int x, int y, int type) {
        addGrass(grasses, x, y, x, y, type);
    }

}
