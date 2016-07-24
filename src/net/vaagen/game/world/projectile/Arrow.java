package net.vaagen.game.world.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.world.Block;
import net.vaagen.game.world.Grass;
import net.vaagen.game.world.World;
import net.vaagen.game.world.entity.Player;

import static net.vaagen.game.view.WorldRenderer.CAMERA_HEIGHT;
import static net.vaagen.game.view.WorldRenderer.CAMERA_WIDTH;

/**
 * Created by Magnus on 2/12/2016.
 */
public class Arrow {

    private static final Vector2 CENTER_OF_MASS = new Vector2(1/4F, 1/2F);
    private static TextureRegion textureRegion;

    private Player owner;
    private float prevDelta;
    private boolean facingLeft;
    private boolean hasCollided;
    private boolean dead;
    private Vector2 velocity;
    private Vector2 position;
    private Vector2 prevPosition;

    public Arrow(Player owner, Vector2 position, Vector2 velocity) {
        this.owner = owner;
        this.position = position;
        this.prevPosition = position.cpy();
        this.velocity = velocity;
        this.facingLeft = this.velocity.x < 0;
        this.hasCollided = false;
    }

    public static void loadTextures() {
        textureRegion = new TextureRegion(new Texture("images/arrow.png"));
    }

    public void render(SpriteBatch spriteBatch) {
        float rotation = getRotation();

        spriteBatch.draw(textureRegion, position.x, position.y, CENTER_OF_MASS.x, CENTER_OF_MASS.y, 1, 1, 1, 1, (float) Math.toDegrees(rotation) % 360); //  + 1 / textureRegion.getRegionHeight() / 2
        spriteBatch.draw(textureRegion, position.x, position.y + Game.gameScreen.getWorld().getLevel().getHeight(), 1F/4, 1F/2, 1, 1, 1, 1, (float) Math.toDegrees(rotation) % 360); //  + 1 / textureRegion.getRegionHeight() / 2
        spriteBatch.draw(textureRegion, position.x, position.y - Game.gameScreen.getWorld().getLevel().getHeight(), 1F/4, 1F/2, 1, 1, 1, 1, (float) Math.toDegrees(rotation) % 360); //  + 1 / textureRegion.getRegionHeight() / 2
    }

    public float getRotation() {
        return (float) (Math.atan2(velocity.y, velocity.x) - Math.PI);
    }

    public float getX() {
        return position.x;
    }
    public float getY() {
        return position.y;
    }
    public float getWidth() {
        return 19F/32F;
    }
    public float getHeight() {
        return 6F/32F;
    }
    public Polygon getBounds() {
        float rotation = getRotation();
        float[] vertices = new float[]{
                (1-getWidth())/2-getCenterOfMass().x, (1-getHeight())/2-getCenterOfMass().y,
                getWidth() + (1-getWidth())/2-getCenterOfMass().x, (1-getHeight())/2-getCenterOfMass().y,
                getWidth() + (1-getWidth())/2-getCenterOfMass().x, getHeight() + (1-getHeight())/2-getCenterOfMass().y,
                (1-getWidth())/2-getCenterOfMass().x, getHeight() + (1-getHeight())/2-getCenterOfMass().y,
        };
        Polygon polygon = new Polygon(vertices);
        polygon.translate(getX()+getCenterOfMass().x, getY()+getCenterOfMass().y);
        polygon.rotate((float) Math.toDegrees(rotation));
        return polygon;//new Rectangle(getX() - (float)(Math.sin(rotation) * (0.5F - (getHeight() / 2)) - Math.sin(rotation + Math.PI / 2) * (0.5F - (getWidth() / 2))), (float) (getY() + Math.sin(rotation + Math.PI / 2) * (0.5 - getHeight()/2) + Math.sin(rotation) * (0.5F - (getWidth() / 2))), getWidth(), getHeight());
    }
    public Vector2 getCenterOfMass() {
        return CENTER_OF_MASS;
    }

    public void update(float delta){
        prevPosition = position.cpy();

        if (!hasCollided) {
            Polygon polygon = getBounds();
            for (Rectangle rectangle : owner.getWorld().getLevel().getCollisionRectangles()) {
                boolean collision = polygon.contains(rectangle.getX(), rectangle.getY()) || polygon.contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY()) || polygon.contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight()) || polygon.contains(rectangle.getX(), rectangle.getY() + rectangle.getHeight());

                float[] vertices = polygon.getTransformedVertices();
                for (int v = 0; v < vertices.length; v += 2) {
                    if (rectangle.contains(vertices[v], vertices[v + 1])) {
                        collision = true;
                    }
                }

                if (collision) {
                    hasCollided = true;
                }
            }
        }

        if (!hasCollided) {
            position.add(velocity.cpy().scl(delta));
            velocity.scl(0.98F, 1).add(0, -5F * delta);
            Grass.applyMovementToGrass(owner.getWorld(), getPosition().x, getPosition().y, getVelocity().x, getVelocity().y, 1.5F, 0.3F);
        }

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

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
