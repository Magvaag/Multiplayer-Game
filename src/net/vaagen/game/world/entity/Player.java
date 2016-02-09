package net.vaagen.game.world.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Magnus on 2/6/2016.
 */
public class Player {

    public enum State {
        IDLE, WALKING, JUMPING, FLYING /*Not incorporated yet*/, DYING
    }

    public static final float SIZE = 0.8F; // half a unit

    /** Textures **/
    private TextureRegion idleLeft;
    private TextureRegion idleRight;

    /** Animations **/
    private Animation idleRightAnimation;
    private Animation idleLeftAnimation;

    private Animation runningRightAnimation;
    private Animation runningLeftAnimation;

    Vector2 position = new Vector2();
    Vector2 	acceleration = new Vector2();
    Vector2 	velocity = new Vector2();
    Rectangle bounds = new Rectangle();
    State		state = State.IDLE;
    boolean		facingLeft = true;
    float		stateTime = 0;
    boolean		longJump = false;

    public Player(Vector2 position) {
        this.position = position;
        this.bounds.x = position.x;
        this.bounds.y = position.y;
        this.bounds.height = SIZE - 1/16F;
        this.bounds.width = SIZE / 2;

        setFacingLeft(false);
    }

    public void loadTextures() {
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
        this.bounds.setX(position.x);
        this.bounds.setY(position.y);
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

    public void render(SpriteBatch spriteBatch) {
        TextureRegion frame;

        if (getState().equals(State.IDLE)) {
            frame = isFacingLeft() ? idleLeftAnimation.getKeyFrame(getStateTime(), true) : idleRightAnimation.getKeyFrame(getStateTime(), true);
        } else if (getState().equals(State.WALKING)) {
            frame = isFacingLeft() ? runningLeftAnimation.getKeyFrame(getStateTime(), true) : runningRightAnimation.getKeyFrame(getStateTime(), true);
        } else
            frame = isFacingLeft() ? idleLeft : idleRight;

        spriteBatch.draw(frame, getPosition().x, getPosition().y, Player.SIZE, Player.SIZE);
    }

    public void updateBounds() {
        bounds.x = position.x + (1 - SIZE);
		bounds.y = position.y;
    }

    public void update(float delta) {
        stateTime += delta;
    }

}
