package net.vaagen.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;

/**
 * Created by Magnus on 2/11/2016.
 */
public class Bridge {

    private static TextureRegion[] bridgeTextures = new TextureRegion[16];

    private int id;
    private int renderLevel; //if it should render above = 1, or below = -1
    private Vector2 position;
    private Rectangle bounds;

    public Bridge(Vector2 pos) {
        this.position = pos;
        this.bounds = new Rectangle();
        this.bounds.setX(pos.x);
        this.bounds.setY(pos.y);
        this.bounds.width = Block.SIZE;
        this.bounds.height = Block.SIZE;
    }

    public static void loadTextures() {
        TextureRegion blocks = new TextureRegion(new Texture("images/bridge.png"));
        TextureRegion[][] splitBlocks = blocks.split(16, 16);
        for (int x = 0; x < splitBlocks.length; x++) {
            for (int y = 0; y < splitBlocks[0].length; y++) {
                if (x * splitBlocks[0].length + y < bridgeTextures.length)
                    bridgeTextures[x * splitBlocks[0].length + y] = splitBlocks[x][y];
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(bridgeTextures[id].getTexture(), position.x, position.y, Block.SIZE, Block.SIZE);
        if (renderLevel == 1)
            spriteBatch.draw(bridgeTextures[id], getPosition().x, getPosition().y + Game.gameScreen.getWorld().getLevel().getHeight(), Block.SIZE, Block.SIZE);
        if (renderLevel == -1)
            spriteBatch.draw(bridgeTextures[id], getPosition().x, getPosition().y - Game.gameScreen.getWorld().getLevel().getHeight(), Block.SIZE, Block.SIZE);
    }

    public Vector2 getPosition() {
        return position;
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public void setRenderLevel(int renderLevel) {
        this.renderLevel = renderLevel;
    }

}
