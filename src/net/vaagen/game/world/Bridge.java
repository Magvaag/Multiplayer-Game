package net.vaagen.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Magnus on 2/11/2016.
 */
public class Bridge {

    private static TextureRegion[] grassTextures = new TextureRegion[16];

    private Vector2 position;

    public Bridge(Vector2 position) {
        this.position = position;
    }

    public static void loadTextures() {
        TextureRegion blocks = new TextureRegion(new Texture("images/bridge.png"));
        TextureRegion[][] splitBlocks = blocks.split(16, 16);
        for (int x = 0; x < splitBlocks.length; x++) {
            for (int y = 0; y < splitBlocks[0].length; y++) {
                if (x * splitBlocks[0].length + y < grassTextures.length)
                    grassTextures[x * splitBlocks[0].length + y] = splitBlocks[x][y];
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(grassTextures[0].getTexture(), position.x, position.y, Block.SIZE, Block.SIZE);
    }

}
