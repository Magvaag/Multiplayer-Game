package net.vaagen.game.inventory.item;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Magnus on 7/21/16.
 */
public interface Item {

    TextureRegion getTexture();
    void onUse();
    String getName();
    void render(SpriteBatch spriteBatch, float x, float y, float scaleX, float scaleY);

}
