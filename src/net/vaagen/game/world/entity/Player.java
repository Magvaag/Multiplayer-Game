<<<<<<< HEAD:src/net/vaagen/game/world/entity/Player.java
package net.vaagen.game.world.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.controller.PlayerController;
import net.vaagen.game.inventory.Inventory;
import net.vaagen.game.inventory.item.Item;
import net.vaagen.game.inventory.item.ItemArrow;
import net.vaagen.game.inventory.item.Items;
import net.vaagen.game.view.WorldRenderer;
import net.vaagen.game.world.Block;
import net.vaagen.game.world.Grass;
import net.vaagen.game.world.World;
import net.vaagen.game.world.projectile.Arrow;

import java.util.List;

/**
 * Created by Magnus on 2/6/2016.
 */
public class Player {

    public enum State {
        IDLE, WALKING, JUMPING, FLYING /*Not incorporated yet*/, DYING, SLIDING, WALL_SLIDE
    }

    public static final float SIZE = 0.8F; // half a unit
    public static final float PICKUP_RADIUS = 1F;

    /** Textures **/
    private static TextureRegion idleLeft;
    private static TextureRegion idleRight;

    /** Animations **/
    private static Animation idleRightAnimation;
    private static Animation idleLeftAnimation;
    private static Animation runningRightAnimation;
    private static Animation runningLeftAnimation;
    private static Animation slidingRightAnimation;
    private static Animation slidingLeftAnimation;
    private static Animation wallSlidingRightAnimation;
    private static Animation wallSlidingLeftAnimation;
    private static Animation fallRightAnimation;
    private static Animation fallLeftAnimation;

    World       world;
    Inventory   inventory;
    Vector2     position = new Vector2();
    Vector2 	acceleration = new Vector2();
    Vector2 	velocity = new Vector2();
    Rectangle   bounds = new Rectangle();
    State		state = State.IDLE;
    boolean		facingLeft = true;
    float		stateTime = 0;
    boolean		longJump = false;
    float       wallSlideTime = 0;
    int         playerId;

    public Player() {
        this.position = new Vector2(0, 0);
        this.bounds.x = position.x;
        this.bounds.y = position.y;
        this.bounds.height = SIZE - 1/16F;
        this.bounds.width = SIZE / 2;
        this.inventory = new Inventory();

        setFacingLeft(false);
    }

    public static void loadTextures() {
        (idleLeft = new TextureRegion(new Texture("images/player.png"))).flip(true, false);
        idleRight = new TextureRegion(new Texture("images/player.png"));
        TextureRegion playerIdleAnimationAtlas = new TextureRegion(new Texture("images/player_idle_animation.png"));

        float idleAnimationSpeed = 0.12F;
        TextureRegion[][] textureArray = playerIdleAnimationAtlas.split(16, 16);
        TextureRegion[] idleRightFrames = new TextureRegion[playerIdleAnimationAtlas.getRegionWidth() / 16 * playerIdleAnimationAtlas.getRegionHeight() / 16];
        TextureRegion[] idleLeftFrames = new TextureRegion[idleRightFrames.length];
        for (int i = 0; i < idleRightFrames.length; i++)
            idleRightFrames[i] = textureArray[0][i];
        idleRightAnimation = new Animation(idleAnimationSpeed, idleRightFrames);
        // This is necessary to keep the idleRight and idleLeft apart // not clones of each other
        textureArray = playerIdleAnimationAtlas.split(16, 16);
        for (int i = 0; i < idleLeftFrames.length; i++) {
            idleLeftFrames[i] = textureArray[0][i];
            idleLeftFrames[i].flip(true, false);
        }
        idleLeftAnimation = new Animation(idleAnimationSpeed, idleLeftFrames);


        float runningAnimationSpeed = 0.12F;
        TextureRegion playerRunningAnimationAtlas = new TextureRegion(new Texture("images/player_run_animation.png"));
        textureArray = playerRunningAnimationAtlas.split(16, 16);
        TextureRegion[] runningRightFrames = new TextureRegion[playerRunningAnimationAtlas.getRegionWidth() / 16 * playerRunningAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < runningRightFrames.length; i++) {
            runningRightFrames[i] = textureArray[0][i];
        }
        runningRightAnimation = new Animation(runningAnimationSpeed, runningRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerRunningAnimationAtlas.split(16, 16);
        TextureRegion[] runningLeftFrames = new TextureRegion[playerRunningAnimationAtlas.getRegionWidth() / 16 * playerRunningAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < runningLeftFrames.length; i++) {
            runningLeftFrames[i] = textureArray[0][i];
            runningLeftFrames[i].flip(true, false);
        }
        runningLeftAnimation = new Animation(runningAnimationSpeed, runningLeftFrames);


        float slidingAnimationSpeed = 0.25F;
        TextureRegion playerSlidingAnimationAtlas = new TextureRegion(new Texture("images/player_slide_animation.png"));
        textureArray = playerSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] slidingRightFrames = new TextureRegion[playerSlidingAnimationAtlas.getRegionWidth() / 16 * playerSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < slidingRightFrames.length; i++) {
            slidingRightFrames[i] = textureArray[0][i];
        }
        slidingRightAnimation = new Animation(slidingAnimationSpeed, slidingRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] slidingLeftFrames = new TextureRegion[playerSlidingAnimationAtlas.getRegionWidth() / 16 * playerSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < slidingLeftFrames.length; i++) {
            slidingLeftFrames[i] = textureArray[0][i];
            slidingLeftFrames[i].flip(true, false);
        }
        slidingLeftAnimation = new Animation(slidingAnimationSpeed, slidingLeftFrames);

        float wallSlidingAnimationSpeed = 0.25F;
        TextureRegion playerWallSlidingAnimationAtlas = new TextureRegion(new Texture("images/player_wallslide_animation.png"));
        textureArray = playerWallSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] wallSlidingRightFrames = new TextureRegion[playerWallSlidingAnimationAtlas.getRegionWidth() / 16 * playerWallSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < wallSlidingRightFrames.length; i++) {
            wallSlidingRightFrames[i] = textureArray[0][i];
        }
        wallSlidingRightAnimation = new Animation(wallSlidingAnimationSpeed, wallSlidingRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerWallSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] wallSlidingLeftFrames = new TextureRegion[playerWallSlidingAnimationAtlas.getRegionWidth() / 16 * playerWallSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < wallSlidingLeftFrames.length; i++) {
            wallSlidingLeftFrames[i] = textureArray[0][i];
            wallSlidingLeftFrames[i].flip(true, false);
        }
        wallSlidingLeftAnimation = new Animation(wallSlidingAnimationSpeed, wallSlidingLeftFrames);

        float fallingAnimationSpeed = 0.25F;
        TextureRegion playerFallingAnimationAtlas = new TextureRegion(new Texture("images/player_fall_animation.png"));
        textureArray = playerFallingAnimationAtlas.split(16, 16);
        TextureRegion[] fallingRightFrames = new TextureRegion[playerFallingAnimationAtlas.getRegionWidth() / 16 * playerFallingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < fallingRightFrames.length; i++) {
            fallingRightFrames[i] = textureArray[0][i];
        }
        fallRightAnimation = new Animation(fallingAnimationSpeed, fallingRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerFallingAnimationAtlas.split(16, 16);
        TextureRegion[] fallingLeftFrames = new TextureRegion[playerFallingAnimationAtlas.getRegionWidth() / 16 * playerFallingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < fallingLeftFrames.length; i++) {
            fallingLeftFrames[i] = textureArray[0][i];
            fallingLeftFrames[i].flip(true, false);
        }
        fallLeftAnimation = new Animation(fallingAnimationSpeed, fallingLeftFrames);
    }
    public boolean isFacingLeft() {
        return facingLeft;
    }
    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }
    public Vector2 getPosition() {
        return position;
    }
    public Vector2 getAcceleration() {
        return acceleration;
    }
    public Vector2 getVelocity() {
        return velocity;
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public State getState() {
        return state;
    }
    public void setState(State newState) {
        if (!this.state.equals(newState)) {
            // If you just started wall sliding, reset the timer
            if (newState.equals(State.WALL_SLIDE))
                wallSlideTime = 0;

            this.state = newState;
            this.stateTime = 0;
        }
    }
    public float getStateTime() {
        return stateTime;
    }
    public boolean isLongJump() {
        return longJump;
    }
    public void setLongJump(boolean longJump) {
        this.longJump = longJump;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
        updateBounds();
    }
    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }
    public float getWallSlideTime() {
        return wallSlideTime;
    }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    public void respawn(World world) {
        this.world = world;
        getVelocity().x = 0;
        getVelocity().y = 0;
        setPosition(world.getLevel().getSpawnPosition().cpy());
    }

    public void render(SpriteBatch spriteBatch) {
        TextureRegion frame;

        if (getState().equals(State.WALKING)) {
            frame = isFacingLeft() ? runningLeftAnimation.getKeyFrame(getStateTime(), true) : runningRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.SLIDING)) {
            frame = isFacingLeft() ? slidingLeftAnimation.getKeyFrame(getStateTime(), true) : slidingRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.WALL_SLIDE)) {
            frame = isFacingLeft() ? wallSlidingLeftAnimation.getKeyFrame(getStateTime(), true) : wallSlidingRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getVelocity().y < -1F) { // Falling
            frame = isFacingLeft() ? fallLeftAnimation.getKeyFrame(getStateTime(), true) : fallRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.IDLE)) {
            frame = isFacingLeft() ? idleLeftAnimation.getKeyFrame(getStateTime(), true) : idleRightAnimation.getKeyFrame(getStateTime(), true);
        } else
            frame = isFacingLeft() ? idleLeft : idleRight;

        spriteBatch.draw(frame, getPosition().x-SIZE/2, getPosition().y, Player.SIZE, Player.SIZE);
        spriteBatch.draw(frame, getPosition().x-SIZE/2, getPosition().y + world.getLevel().getHeight(), Player.SIZE, Player.SIZE);
        spriteBatch.draw(frame, getPosition().x-SIZE/2, getPosition().y - world.getLevel().getHeight(), Player.SIZE, Player.SIZE);
    }

    public void renderDebug(ShapeRenderer debugRenderer) {
        // render Player
        Player player = Game.gameScreen.getPlayer();
        Rectangle rect = player.getBounds();
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        Rectangle circle = getPickupCircle();
        debugRenderer.setColor(Color.YELLOW);
        debugRenderer.rect(circle.x, circle.y, circle.width, circle.height);
    }

    public void updateBounds() {
		bounds.y = position.y;

        if (getState().equals(State.SLIDING)) {
            bounds.x = position.x-SIZE/2;
            bounds.width = SIZE;
            bounds.height = SIZE / 2;
        } else {
            bounds.x = position.x + (1 - SIZE)-SIZE/2;
            bounds.width = SIZE / 2;
            bounds.height = SIZE;
        }
    }

    public Rectangle getPickupCircle() {
        Rectangle rectangle = new Rectangle(getPosition().x - PICKUP_RADIUS/2, getPosition().y + getBounds().height/2 - PICKUP_RADIUS / 2, PICKUP_RADIUS, PICKUP_RADIUS);
        return rectangle;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void update(float delta) {
        stateTime += delta;
        if (getState().equals(State.WALL_SLIDE))
            wallSlideTime += delta;

        Rectangle rectangle = getPickupCircle();
        for (Arrow arrow : world.getProjectileList()) {
            if (arrow.isDead() || !arrow.hasCollided())
                continue;
            boolean collision = arrow.getBounds().contains(rectangle.getX(), rectangle.getY()) || arrow.getBounds().contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY()) || arrow.getBounds().contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight()) || arrow.getBounds().contains(rectangle.getX(), rectangle.getY() + rectangle.getHeight());

            float[] vertices = arrow.getBounds().getTransformedVertices();
            for (int v = 0; v < vertices.length; v += 2) {
                if (rectangle.contains(vertices[v], vertices[v + 1])) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                pickupItem(Items.itemArrow);
                arrow.setDead(true);
            }
        }

        Grass.applyMovementToGrass(world, getPosition().x, getPosition().y, getVelocity().x, getVelocity().y, 1.5F, 1);
    }

    public void pickupItem(Item item) {
        getInventory().pickupItem(item);
    }

    public World getWorld() {
        return world;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
=======
package net.vaagen.game.world.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.controller.PlayerController;
import net.vaagen.game.inventory.Inventory;
import net.vaagen.game.inventory.item.Item;
import net.vaagen.game.inventory.item.ItemArrow;
import net.vaagen.game.inventory.item.Items;
import net.vaagen.game.view.WorldRenderer;
import net.vaagen.game.world.Block;
import net.vaagen.game.world.Grass;
import net.vaagen.game.world.World;
import net.vaagen.game.world.projectile.Arrow;

import java.util.List;

/**
 * Created by Magnus on 2/6/2016.
 */
public class Player {

    public enum State {
        IDLE, WALKING, JUMPING, FLYING /*Not incorporated yet*/, DYING, SLIDING, WALL_SLIDE
    }

    public static final float SIZE = 0.8F; // half a unit
    public static final float PICKUP_RADIUS = 1F;

    /** Textures **/
    private static TextureRegion idleLeft;
    private static TextureRegion idleRight;

    /** Animations **/
    private static Animation idleRightAnimation;
    private static Animation idleLeftAnimation;
    private static Animation runningRightAnimation;
    private static Animation runningLeftAnimation;
    private static Animation slidingRightAnimation;
    private static Animation slidingLeftAnimation;
    private static Animation wallSlidingRightAnimation;
    private static Animation wallSlidingLeftAnimation;
    private static Animation fallRightAnimation;
    private static Animation fallLeftAnimation;

    World       world;
    Inventory   inventory;
    Vector2     position = new Vector2();
    Vector2 	acceleration = new Vector2();
    Vector2 	velocity = new Vector2();
    Rectangle   bounds = new Rectangle();
    State		state = State.IDLE;
    boolean		facingLeft = true;
    float		stateTime = 0;
    boolean		longJump = false;
    float       wallSlideTime = 0;
    int         playerId;

    public Player() {
        this.position = new Vector2(0, 0);
        this.bounds.x = position.x;
        this.bounds.y = position.y;
        this.bounds.height = SIZE - 1/16F;
        this.bounds.width = SIZE / 2;
        this.inventory = new Inventory();

        setFacingLeft(false);
    }

    public static void loadTextures() {
        (idleLeft = new TextureRegion(new Texture("images/player.png"))).flip(true, false);
        idleRight = new TextureRegion(new Texture("images/player.png"));
        TextureRegion playerIdleAnimationAtlas = new TextureRegion(new Texture("images/player_idle_animation.png"));

        float idleAnimationSpeed = 0.12F;
        TextureRegion[][] textureArray = playerIdleAnimationAtlas.split(16, 16);
        TextureRegion[] idleRightFrames = new TextureRegion[playerIdleAnimationAtlas.getRegionWidth() / 16 * playerIdleAnimationAtlas.getRegionHeight() / 16];
        TextureRegion[] idleLeftFrames = new TextureRegion[idleRightFrames.length];
        for (int i = 0; i < idleRightFrames.length; i++)
            idleRightFrames[i] = textureArray[0][i];
        idleRightAnimation = new Animation(idleAnimationSpeed, idleRightFrames);
        // This is necessary to keep the idleRight and idleLeft apart // not clones of each other
        textureArray = playerIdleAnimationAtlas.split(16, 16);
        for (int i = 0; i < idleLeftFrames.length; i++) {
            idleLeftFrames[i] = textureArray[0][i];
            idleLeftFrames[i].flip(true, false);
        }
        idleLeftAnimation = new Animation(idleAnimationSpeed, idleLeftFrames);


        float runningAnimationSpeed = 0.12F;
        TextureRegion playerRunningAnimationAtlas = new TextureRegion(new Texture("images/player_run_animation.png"));
        textureArray = playerRunningAnimationAtlas.split(16, 16);
        TextureRegion[] runningRightFrames = new TextureRegion[playerRunningAnimationAtlas.getRegionWidth() / 16 * playerRunningAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < runningRightFrames.length; i++) {
            runningRightFrames[i] = textureArray[0][i];
        }
        runningRightAnimation = new Animation(runningAnimationSpeed, runningRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerRunningAnimationAtlas.split(16, 16);
        TextureRegion[] runningLeftFrames = new TextureRegion[playerRunningAnimationAtlas.getRegionWidth() / 16 * playerRunningAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < runningLeftFrames.length; i++) {
            runningLeftFrames[i] = textureArray[0][i];
            runningLeftFrames[i].flip(true, false);
        }
        runningLeftAnimation = new Animation(runningAnimationSpeed, runningLeftFrames);


        float slidingAnimationSpeed = 0.25F;
        TextureRegion playerSlidingAnimationAtlas = new TextureRegion(new Texture("images/player_slide_animation.png"));
        textureArray = playerSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] slidingRightFrames = new TextureRegion[playerSlidingAnimationAtlas.getRegionWidth() / 16 * playerSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < slidingRightFrames.length; i++) {
            slidingRightFrames[i] = textureArray[0][i];
        }
        slidingRightAnimation = new Animation(slidingAnimationSpeed, slidingRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] slidingLeftFrames = new TextureRegion[playerSlidingAnimationAtlas.getRegionWidth() / 16 * playerSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < slidingLeftFrames.length; i++) {
            slidingLeftFrames[i] = textureArray[0][i];
            slidingLeftFrames[i].flip(true, false);
        }
        slidingLeftAnimation = new Animation(slidingAnimationSpeed, slidingLeftFrames);

        float wallSlidingAnimationSpeed = 0.25F;
        TextureRegion playerWallSlidingAnimationAtlas = new TextureRegion(new Texture("images/player_wallslide_animation.png"));
        textureArray = playerWallSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] wallSlidingRightFrames = new TextureRegion[playerWallSlidingAnimationAtlas.getRegionWidth() / 16 * playerWallSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < wallSlidingRightFrames.length; i++) {
            wallSlidingRightFrames[i] = textureArray[0][i];
        }
        wallSlidingRightAnimation = new Animation(wallSlidingAnimationSpeed, wallSlidingRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerWallSlidingAnimationAtlas.split(16, 16);
        TextureRegion[] wallSlidingLeftFrames = new TextureRegion[playerWallSlidingAnimationAtlas.getRegionWidth() / 16 * playerWallSlidingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < wallSlidingLeftFrames.length; i++) {
            wallSlidingLeftFrames[i] = textureArray[0][i];
            wallSlidingLeftFrames[i].flip(true, false);
        }
        wallSlidingLeftAnimation = new Animation(wallSlidingAnimationSpeed, wallSlidingLeftFrames);

        float fallingAnimationSpeed = 0.25F;
        TextureRegion playerFallingAnimationAtlas = new TextureRegion(new Texture("images/player_fall_animation.png"));
        textureArray = playerFallingAnimationAtlas.split(16, 16);
        TextureRegion[] fallingRightFrames = new TextureRegion[playerFallingAnimationAtlas.getRegionWidth() / 16 * playerFallingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < fallingRightFrames.length; i++) {
            fallingRightFrames[i] = textureArray[0][i];
        }
        fallRightAnimation = new Animation(fallingAnimationSpeed, fallingRightFrames);
        // This is necessary to keep the right and left apart // not clones of each other
        textureArray = playerFallingAnimationAtlas.split(16, 16);
        TextureRegion[] fallingLeftFrames = new TextureRegion[playerFallingAnimationAtlas.getRegionWidth() / 16 * playerFallingAnimationAtlas.getRegionHeight() / 16];
        for (int i = 0; i < fallingLeftFrames.length; i++) {
            fallingLeftFrames[i] = textureArray[0][i];
            fallingLeftFrames[i].flip(true, false);
        }
        fallLeftAnimation = new Animation(fallingAnimationSpeed, fallingLeftFrames);
    }
    public boolean isFacingLeft() {
        return facingLeft;
    }
    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }
    public Vector2 getPosition() {
        return position;
    }
    public Vector2 getAcceleration() {
        return acceleration;
    }
    public Vector2 getVelocity() {
        return velocity;
    }
    public Rectangle getBounds() {
        return bounds;
    }
    public State getState() {
        return state;
    }
    public void setState(State newState) {
        if (!this.state.equals(newState)) {
            // If you just started wall sliding, reset the timer
            if (newState.equals(State.WALL_SLIDE))
                wallSlideTime = 0;

            this.state = newState;
            this.stateTime = 0;
        }
    }
    public float getStateTime() {
        return stateTime;
    }
    public boolean isLongJump() {
        return longJump;
    }
    public void setLongJump(boolean longJump) {
        this.longJump = longJump;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
        updateBounds();
    }
    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }
    public float getWallSlideTime() {
        return wallSlideTime;
    }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    public void respawn(World world) {
        this.world = world;
        getVelocity().x = 0;
        getVelocity().y = 0;
        setPosition(world.getLevel().getSpawnPosition().cpy());
    }

    public void render(SpriteBatch spriteBatch) {
        TextureRegion frame;

        if (getState().equals(State.WALKING)) {
            frame = isFacingLeft() ? runningLeftAnimation.getKeyFrame(getStateTime(), true) : runningRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.SLIDING)) {
            frame = isFacingLeft() ? slidingLeftAnimation.getKeyFrame(getStateTime(), true) : slidingRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.WALL_SLIDE)) {
            frame = isFacingLeft() ? wallSlidingLeftAnimation.getKeyFrame(getStateTime(), true) : wallSlidingRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getVelocity().y < -1F) { // Falling
            frame = isFacingLeft() ? fallLeftAnimation.getKeyFrame(getStateTime(), true) : fallRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.IDLE)) {
            frame = isFacingLeft() ? idleLeftAnimation.getKeyFrame(getStateTime(), true) : idleRightAnimation.getKeyFrame(getStateTime(), true);
        } else
            frame = isFacingLeft() ? idleLeft : idleRight;

        spriteBatch.draw(frame, getPosition().x-SIZE/2, getPosition().y, Player.SIZE, Player.SIZE);
        spriteBatch.draw(frame, getPosition().x-SIZE/2, getPosition().y + world.getLevel().getHeight(), Player.SIZE, Player.SIZE);
        spriteBatch.draw(frame, getPosition().x-SIZE/2, getPosition().y - world.getLevel().getHeight(), Player.SIZE, Player.SIZE);
    }

    public void renderDebug(ShapeRenderer debugRenderer) {
        // render Player
        Player player = Game.gameScreen.getPlayer();
        Rectangle rect = player.getBounds();
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        Rectangle circle = getPickupCircle();
        debugRenderer.setColor(Color.YELLOW);
        debugRenderer.rect(circle.x, circle.y, circle.width, circle.height);
    }

    public void updateBounds() {
		bounds.y = position.y;

        if (getState().equals(State.SLIDING)) {
            bounds.x = position.x-SIZE/2;
            bounds.width = SIZE;
            bounds.height = SIZE / 2;
        } else {
            bounds.x = position.x + (1 - SIZE)-SIZE/2;
            bounds.width = SIZE / 2;
            bounds.height = SIZE;
        }
    }

    public Rectangle getPickupCircle() {
        Rectangle rectangle = new Rectangle(getPosition().x - PICKUP_RADIUS/2, getPosition().y + getBounds().height/2 - PICKUP_RADIUS / 2, PICKUP_RADIUS, PICKUP_RADIUS);
        return rectangle;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void update(float delta) {
        stateTime += delta;
        if (getState().equals(State.WALL_SLIDE))
            wallSlideTime += delta;

        Rectangle rectangle = getPickupCircle();
        for (Arrow arrow : world.getProjectileList()) {
            if (arrow.isDead() || !arrow.hasCollided())
                continue;
            boolean collision = arrow.getBounds().contains(rectangle.getX(), rectangle.getY()) || arrow.getBounds().contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY()) || arrow.getBounds().contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight()) || arrow.getBounds().contains(rectangle.getX(), rectangle.getY() + rectangle.getHeight());

            float[] vertices = arrow.getBounds().getTransformedVertices();
            for (int v = 0; v < vertices.length; v += 2) {
                if (rectangle.contains(vertices[v], vertices[v + 1])) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                pickupItem(Items.itemArrow);
                arrow.setDead(true);
            }
        }

        Grass.applyMovementToGrass(world, getPosition().x, getPosition().y, getVelocity().x, getVelocity().y, 1.5F, 1);
    }

    public void pickupItem(Item item) {
        getInventory().pickupItem(item);
    }

    public World getWorld() {
        return world;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
>>>>>>> origin/master:src/net/vaagen/game/src/net/vaagen/game/world/entity/Player.java
