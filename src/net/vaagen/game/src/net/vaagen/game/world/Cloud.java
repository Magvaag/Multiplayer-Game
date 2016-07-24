package net.vaagen.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.view.WorldRenderer;

/**
 * Created by Magnus on 2/13/2016.
 */
public class Cloud {

    private static TextureRegion[] textureRegions = new TextureRegion[16];
    private int id;
    private Vector2 position;
    private Vector2 velocity;
    private boolean dead;

    public Cloud(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public static void loadTextures() {
        TextureRegion clouds = new TextureRegion(new Texture("images/clouds.png"));
        TextureRegion[][] splitClouds = clouds.split(16, 16);
        for (int x = 0; x < splitClouds.length; x++) {
            for (int y = 0; y < splitClouds[0].length; y++) {
                textureRegions[x * splitClouds[0].length + y] = splitClouds[x][y];
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(textureRegions[id].getTexture(), position.x, position.y + Game.gameScreen.getPlayer().getPosition().y / 10, 3, 3);
    }

    public void updatePosition(float positionChanged) {
        position.y += positionChanged;
    }

    public void update(float delta) {
        position.add(velocity.cpy().scl(delta));
        if (position.x > Game.gameScreen.getWorld().getLevel().getWidth())
            dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
