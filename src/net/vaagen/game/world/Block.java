package net.vaagen.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Magnus on 10/15/2015.
 */
public class Block {

    public static final float SIZE = 1f;
    private static TextureRegion[] blockTextures = new TextureRegion[256];

    private int id;

    Vector2 position = new Vector2();
    Rectangle bounds = new Rectangle();

    public Block(Vector2 pos, int id) {
        this.position = pos;
        this.bounds.setX(pos.x);
        this.bounds.setY(pos.y);
        this.bounds.width = SIZE;
        this.bounds.height = SIZE;
        this.id = id;
    }

    public static void loadTextures() {
        TextureRegion blocks = new TextureRegion(new Texture("images/blocks.png"));
        TextureRegion[][] splitBlocks = blocks.split(16, 16);
        for (int x = 0; x < splitBlocks.length; x++) {
            for (int y = 0; y < splitBlocks[0].length; y++) {
                blockTextures[x * splitBlocks[0].length + y] = splitBlocks[x][y];
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(blockTextures[id], getPosition().x, getPosition().y, Block.SIZE, Block.SIZE);
    }

    public Vector2 getPosition() {
        return position;
    }
    public Rectangle getBounds() {
        return bounds;
    }

    public int getId() {
        return id;
    }
}
