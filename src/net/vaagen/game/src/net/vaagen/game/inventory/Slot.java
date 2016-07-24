package net.vaagen.game.inventory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.vaagen.game.inventory.item.Item;

/**
 * Created by Magnus on 7/21/16.
 */
public class Slot {

    private int amount;
    private Item item;

    public Slot(Item item, int amount){
        this.item = item;
        this.amount = amount;
    }

    public void render(SpriteBatch spriteBatch, float x, float y, float scaleX, float scaleY) {
        item.render(spriteBatch, x, y, scaleX, scaleY);
    }

    public int getAmount() {
        return amount;
    }

    public int addAmount(int amount) {
        this.amount += amount;
        return this.amount;
    }
}
