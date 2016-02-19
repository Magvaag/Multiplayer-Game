package net.vaagen.game.world.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;

/**
 * Created by Magnus on 2/12/2016.
 */
public class Arrow {

    private static TextureRegion textureRegion;

    private int playerOwner;
    private float prevDelta;
    private boolean facingLeft;
    private Vector2 velocity;
    private Vector2 position;
    private Vector2 prevPosition;

    private Rectangle bounds ; // TODO _ AROWK ON!§

    public Arrow(int playerOwner, Vector2 position, Vector2 velocity) {
        this.playerOwner = playerOwner;
        this.position = position;
        this.prevPosition = position.cpy();
        this.velocity = velocity;
        this.facingLeft = this.velocity.x < 0;
    }

    public static void loadTextures() {
        textureRegion = new TextureRegion(new Texture("images/arrow.png"));
    }

    public void render(SpriteBatch spriteBatch) {
        //System.out.println("Rendering arrow! " + position.x + ", " + position.y);
        float dx = position.x - prevPosition.x;
        float dy = position.y - prevPosition.y;
        float rotation = (float) Math.toRadians(0);
        if (dx != 0)
            rotation += (float) Math.tan(dy / dx);
        if (!facingLeft)
            rotation = (float) (Math.toRadians(180) + rotation);
        if (prevDelta > 0 && Math.abs(dx) < 6F * prevDelta)
            rotation = (float) Math.toRadians(90);
        spriteBatch.draw(textureRegion, position.x, position.y, 1F/4, 1F/2, 1, 1, 1, 1, (float) Math.toDegrees(rotation) % 360); //  + 1 / textureRegion.getRegionHeight() / 2
        spriteBatch.draw(textureRegion, position.x, position.y + Game.gameScreen.getWorld().getLevel().getHeight(), 1F/4, 1F/2, 1, 1, 1, 1, (float) Math.toDegrees(rotation) % 360); //  + 1 / textureRegion.getRegionHeight() / 2
        spriteBatch.draw(textureRegion, position.x, position.y - Game.gameScreen.getWorld().getLevel().getHeight(), 1F/4, 1F/2, 1, 1, 1, 1, (float) Math.toDegrees(rotation) % 360); //  + 1 / textureRegion.getRegionHeight() / 2
    }

    /** Unused **/
    public void update(float delta){
        prevPosition = position.cpy();
        velocity.scl(0.98F, 1).add(0, -5F * delta);
        position.add(velocity.cpy().scl(delta));

        if (position.x < 0)
            position.x += Game.gameScreen.getWorld().getLevel().getWidth();
        if (position.x > Game.gameScreen.getWorld().getLevel().getWidth())
            position.x -= Game.gameScreen.getWorld().getLevel().getWidth();
        if (position.y < 0)
            position.y += Game.gameScreen.getWorld().getLevel().getHeight();
        if (position.y > Game.gameScreen.getWorld().getLevel().getHeight())
            position.y -= Game.gameScreen.getWorld().getLevel().getHeight();

        float maxVelocityY = 20;
        if (velocity.y < -maxVelocityY)
            velocity.y = -maxVelocityY;
        this.prevDelta = delta;
    }

}
