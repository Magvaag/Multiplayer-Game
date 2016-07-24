package net.vaagen.game.inventory.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Magnus on 7/21/16.
 */
public class ItemArrow implements Item {

    @Override
    public TextureRegion getTexture() {
        return new TextureRegion(new Texture("images/arrow.png"));
    }

    @Override
    public void onUse() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void render(SpriteBatch spriteBatch, float x, float y, float scaleX, float scaleY) {
        spriteBatch.draw(getTexture(), x, y, scaleX, scaleY);
    }
}
