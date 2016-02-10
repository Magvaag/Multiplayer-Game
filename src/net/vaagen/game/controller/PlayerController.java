package net.vaagen.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import net.vaagen.game.world.Block;
import net.vaagen.game.world.World;
import net.vaagen.game.world.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Magnus on 10/15/2015.
 */
public class PlayerController {

    public enum Keys {
        LEFT(Input.Keys.LEFT), RIGHT(Input.Keys.RIGHT), JUMP(Input.Keys.SPACE, Input.Keys.UP), SLIDE(Input.Keys.DOWN), FIRE(Input.Keys.Z);

        private List<Integer> inputKey;
        Keys(Integer... inputKey) {
            this.inputKey = Arrays.asList(inputKey);
        }

        public List<Integer> getInputKey() {
            return inputKey;
        }
    }

    private static final long LONG_JUMP_PRESS 	= 350l;
    private static final float ACCELERATION 	= 35f;
    private static final float GRAVITY 			= -27f;
    private static final float MAX_JUMP_SPEED	= 9f;
    private static final float GROUND_DAMP = 0.89f;
    private static final float AIR_DAMP = 0.96f;
    private static final float SLIDING_DAMP = 0.98f;
    private static final float AIR_SLIDING_DAMP = 0.994f;
    private static final float WALL_SLIDE_DAMP = 0.87F;
    private static final float WALL_SLIDE_KICK_OFF = 200F;
    private static final float WALL_SLIDE_MAX_JUMP_TIME = 0.1F;
    private static final float WALL_SLIDE_JUMP_BOOST = 1.25F;
    private static final float MAX_VEL 			= 7f;

    private World world;
    private Player player;
    private long	jumpPressedTime;
    private boolean jumpingPressed;
    private long wallSlideMoveDisabledTime;
    private boolean grounded = false;

    // This is the rectangle pool used in collision detection
    // Good to avoid instantiation each frame
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };

    static Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.JUMP, false);
        keys.put(Keys.SLIDE, false);
        keys.put(Keys.FIRE, false);
    };

    // Blocks that Player can collide with any given frame
    private Array<Block> collidable = new Array<Block>();

    public PlayerController(World world) {
        this.world = world;
        this.player = world.getPlayer();
    }

    // ** Key presses and touches **************** //

    public void leftPressed() {
        keys.get(keys.put(Keys.LEFT, true));
    }
    public void rightPressed() {
        keys.get(keys.put(Keys.RIGHT, true));
    }
    public void jumpPressed() {
        keys.get(keys.put(Keys.JUMP, true));
    }
    public void slidePressed() {
        keys.get(keys.put(Keys.SLIDE, true));
    }
    public void firePressed() {
        keys.get(keys.put(Keys.FIRE, false));
    }

    public void leftReleased() {
        keys.get(keys.put(Keys.LEFT, false));
    }
    public void rightReleased() {
        keys.get(keys.put(Keys.RIGHT, false));
    }
    public void jumpReleased() {
        keys.get(keys.put(Keys.JUMP, false));
        jumpingPressed = false;
    }
    public void slideReleased() {
        keys.get(keys.put(Keys.SLIDE, false));
    }
    public void fireReleased() {
        keys.get(keys.put(Keys.FIRE, false));
    }

    /** The main update method **/
    public void update(float delta) {
        // Processing the input - setting the states of Player
        processInput();

        wallSlideMoveDisabledTime -= delta;

        // If Player is grounded then reset the state to IDLE
        if (grounded && player.getState().equals(Player.State.JUMPING)) {
            player.setState(Player.State.IDLE);
        }

        // Setting initial vertical acceleration
        player.getAcceleration().y = GRAVITY;

        // Convert acceleration to frame time
        player.getAcceleration().scl(delta);

        // apply acceleration to change velocity
        player.getVelocity().add(player.getAcceleration().x, player.getAcceleration().y);

        // checking collisions with the surrounding blocks depending on Player's velocity
        checkCollisionWithBlocks(delta);

        // apply damping to halt Player nicely, but only if he's grounded
        if (player.getState().equals(Player.State.SLIDING)) {
            if (grounded)
                player.getVelocity().x *= SLIDING_DAMP;
            else
                player.getVelocity().x *= AIR_SLIDING_DAMP;
        } else {
            if (grounded)
                player.getVelocity().x *= GROUND_DAMP;
            else
                player.getVelocity().x *= AIR_DAMP;
        }

        if (player.getState().equals(Player.State.WALL_SLIDE)) {
            player.getVelocity().y *= WALL_SLIDE_DAMP;
        }

        // ensure terminal velocity is not exceeded
        if (player.getVelocity().x > MAX_VEL) {
            player.getVelocity().x = MAX_VEL;
        }
        if (player.getVelocity().x < -MAX_VEL) {
            player.getVelocity().x = -MAX_VEL;
        }

        // simply updates the state time
        player.update(delta);
    }

    /** Collision checking **/
    private void checkCollisionWithBlocks(float delta) {
        // scale velocity to frame units
        player.getVelocity().scl(delta);

        // Obtain the rectangle from the pool instead of instantiating it
        Rectangle playerRect = rectPool.obtain();
        // set the rectangle to player's bounding box
        playerRect.set(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);

        populateHorizontalCollidableBlocks(playerRect);

        // simulate player's movement on the X
        playerRect.x += player.getVelocity().x;

        // clear collision boxes in world
        world.getCollisionRects().clear();

        // if player collides, make his horizontal velocity 0
        for (Block block : collidable) {
            if (block == null) continue;
            if (playerRect.overlaps(block.getBounds())) {
                player.getVelocity().x = 0;
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }

        // reset the x position of the collision box
        playerRect.x = player.getBounds().x;


        populateVerticalCollidableBlocks();
        playerRect.y += player.getVelocity().y;

        for (Block block : collidable) {
            if (block == null) continue;
            if (playerRect.overlaps(block.getBounds())) {
                if (player.getVelocity().y < 0) {
                    grounded = true;
                }
                player.getVelocity().y = 0;
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }
        // reset the collision box's position on Y
        playerRect.y = player.getBounds().y;

        // update Player's position
        player.getPosition().add(player.getVelocity());
        player.updateBounds();

        // Update the position
        if (player.getPosition().x > world.getLevel().getWidth())
            player.getPosition().x -= world.getLevel().getWidth();
        if (player.getPosition().x < 0)
            player.getPosition().x += world.getLevel().getWidth();

        // un-scale velocity (not in frame time)
        player.getVelocity().scl(1 / delta);
    }

    private void populateHorizontalCollidableBlocks(Rectangle rectangle) {
        // we first check the movement on the horizontal X axis
        int startX, endX;
        int startY = (int) rectangle.y;
        int endY = (int) (rectangle.y + rectangle.height);
        // if Player is heading left then we check if he collides with the block on his left
        // we check the block on his right otherwise
        if (player.getVelocity().x < 0) {
            startX = endX = (int) Math.floor(rectangle.x + player.getVelocity().x);
        } else {
            startX = endX = (int) Math.floor(rectangle.x + rectangle.width + player.getVelocity().x);
        }

        // get the block(s) player can collide with
        populateCollidableBlocks(startX, startY, endX, endY);
    }

    private void populateVerticalCollidableBlocks() {
        // the same thing but on the vertical Y axis
        int startY, endY;
        int startX = (int) player.getBounds().x;
        int endX = (int) (player.getBounds().x + player.getBounds().width);
        if (player.getVelocity().y < 0) {
            startY = endY = (int) Math.floor(player.getBounds().y + player.getVelocity().y);
        } else {
            startY = endY = (int) Math.floor(player.getBounds().y + player.getBounds().height + player.getVelocity().y);
        }

        populateCollidableBlocks(startX, startY, endX, endY);
    }

    /** populate the collidable array with the blocks found in the enclosing coordinates **/
    private void populateCollidableBlocks(int startX, int startY, int endX, int endY) {
        collidable.clear();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (x >= 0 && x < world.getLevel().getWidth() && y >=0 && y < world.getLevel().getHeight()) {
                    collidable.add(world.getLevel().get(x, y));
                }
            }
        }

        for (int y = startY; y <= endY; y++) {
            Block block = world.getLevel().get(world.getLevel().getWidth()-1, y); // Grab the last block in the row
            if (block != null) {
                Block clone = new Block(new Vector2(-1, y), block.getId());
                collidable.add(clone);
            }
        }

        for (int y = startY; y <= endY; y++) {
            Block block = world.getLevel().get(0, y); // Grab the last block in the row
            if (block != null) {
                Block clone = new Block(new Vector2(world.getLevel().getWidth(), y), block.getId());
                collidable.add(clone);
            }
        }
    }

    /** Change Player's state and parameters based on input controls **/
    private boolean processInput() {
        boolean activeInput = false; // If we actually did anything
        boolean onGroundReadyToJump = (grounded || jumpingPressed);
        if (keys.get(Keys.JUMP) && !player.getState().equals(Player.State.SLIDING) && (onGroundReadyToJump || (player.getState().equals(Player.State.WALL_SLIDE) && canWallSlideJump()))) {
            boolean wallSliding = player.getState().equals(Player.State.WALL_SLIDE);
            // Give the player a push from the wall
            if (wallSliding) {
                wallSlideMoveDisabledTime = (long) 10;
                if (player.isFacingLeft()) {
                    player.getAcceleration().x -= WALL_SLIDE_KICK_OFF;
                    player.getPosition().x -= 0.1F;
                } else {
                    player.getAcceleration().x += WALL_SLIDE_KICK_OFF;
                    player.getPosition().x += 0.1F;
                }
            }

            if (!player.getState().equals(Player.State.JUMPING)) {
                jumpingPressed = wallSliding ? false : true;
                jumpPressedTime = System.currentTimeMillis();
                player.setState(Player.State.JUMPING);
                player.getVelocity().y = MAX_JUMP_SPEED * (wallSliding ? WALL_SLIDE_JUMP_BOOST : 1);
                grounded = false;
            } else {
                if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
                    jumpingPressed = false;
                } else {
                    if (jumpingPressed) {
                        player.getVelocity().y = MAX_JUMP_SPEED;
                    }
                }
            }

            if (wallSliding)
                jumpReleased();

            activeInput = true;
        }

        if (wallSlideMoveDisabledTime <= 0) {
            if (keys.get(Keys.LEFT)) {
                if (!player.getState().equals(Player.State.SLIDING)) {
                    // Obtain the rectangle from the pool instead of instantiating it
                    Rectangle playerRect = rectPool.obtain();
                    // set the rectangle to player's bounding box
                    playerRect.set(player.getBounds().x - 0.01F, player.getBounds().y, player.getBounds().width, player.getBounds().height);

                    boolean canWallSlide = false;
                    // Make sure the player is still in the air
                    if (!grounded && player.getVelocity().y < 0) {
                        // if player collides, make his horizontal velocity 0
                        for (Rectangle rectangle : world.getCollisionRects()) {
                            if (playerRect.overlaps(rectangle)) {
                                canWallSlide = true;
                                break;
                            }
                        }
                    }

                    // left is pressed
                    if (canWallSlide) {
                        player.setFacingLeft(false); // It will have a inverted direction
                        player.setState(Player.State.WALL_SLIDE);

                        // Make sure the player is not already jumping
                        jumpReleased();
                    } else if (!player.getState().equals(Player.State.JUMPING)) {
                        player.setFacingLeft(true);
                        player.setState(Player.State.WALKING);
                    }
                    player.getAcceleration().x = -ACCELERATION;

                    activeInput = true;
                }
            } else if (keys.get(Keys.RIGHT)) {
                if (!player.getState().equals(Player.State.SLIDING)) {
                    // Obtain the rectangle from the pool instead of instantiating it
                    Rectangle playerRect = rectPool.obtain();
                    // set the rectangle to player's bounding box
                    playerRect.set(player.getBounds().x + 0.01F, player.getBounds().y, player.getBounds().width, player.getBounds().height);

                    boolean canWallSlide = false;
                    // Make sure the player is still in the air
                    if (!grounded && player.getVelocity().y < 0) {
                        // if player collides, make his horizontal velocity 0
                        for (Rectangle rectangle : world.getCollisionRects()) {
                            if (playerRect.overlaps(rectangle)) {
                                canWallSlide = true;
                                break;
                            }
                        }
                    }

                    // right is pressed
                    if (canWallSlide) {
                        player.setFacingLeft(true); // It will have a inverted direction
                        player.setState(Player.State.WALL_SLIDE);

                        // Make sure the player is not already jumping
                        jumpReleased();
                    } else if (!player.getState().equals(Player.State.JUMPING)) {
                        player.setFacingLeft(false);
                        player.setState(Player.State.WALKING);
                    }
                    player.getAcceleration().x = ACCELERATION;

                    activeInput = true;
                }
            }
        }

        if (keys.get(Keys.SLIDE)) {
            player.setState(Player.State.SLIDING);
            activeInput = true;
        }

        if (!activeInput) {
            if (!player.getState().equals(Player.State.JUMPING)) {
                player.setState(Player.State.IDLE);
            }
            player.getAcceleration().x = 0;
        }
        return false;
    }

    public boolean canWallSlideJump() {
        return player.getWallSlideTime() > PlayerController.WALL_SLIDE_MAX_JUMP_TIME;
    }
}
