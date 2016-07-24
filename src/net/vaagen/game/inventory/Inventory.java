package net.vaagen.game.inventory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.vaagen.game.Game;
import net.vaagen.game.inventory.item.Item;
import net.vaagen.game.inventory.item.ItemArrow;
import net.vaagen.game.inventory.item.Items;

/**
 * Created by Magnus on 7/21/16.
 */
public class Inventory {

    private Slot arrowSlot;
    private float arrowSlotX = -9.5F, arrowSlotY = -5.3F;

    public Inventory() {
        arrowSlot = new Slot(Items.itemArrow, 20);
    }

    public void render(SpriteBatch spriteBatch) {
        // Render arrow slot
        Game.gameScreen.getClient().getChat().renderText(spriteBatch, "x" + arrowSlot.getAmount(), arrowSlotX, arrowSlotY, 1, 1);
        spriteBatch.begin();
        arrowSlot.render(spriteBatch, arrowSlotX + 1.2F + Game.gameScreen.getRenderer().getCameraXForPlayer(Game.gameScreen.getPlayer()), arrowSlotY + Game.gameScreen.getRenderer().getCameraYForPlayer(Game.gameScreen.getPlayer()), 1, 1);
        spriteBatch.end();
    }

    public void pickupItem(Item item) {
        if (item.equals(Items.itemArrow)) {
            arrowSlot.addAmount(1);
        }
    }

    public Slot getArrowSlot() {
        return arrowSlot;
    }
}
