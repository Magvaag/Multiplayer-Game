package net.vaagen.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.screens.GameScreen;
import net.vaagen.game.world.*;
import net.vaagen.game.world.entity.Player;
import net.vaagen.game.world.projectile.Arrow;

/**
 * Created by Magnus on 10/15/2015.
 */
public class WorldRenderer {

    private static float ZOOM = 1.5F;
    public static final float SCREEN_WIDTH = 1280F;
    public static final float SCREEN_HEIGHT = 720F;
    public static final float CAMERA_WIDTH = SCREEN_WIDTH / 100 * ZOOM;
    public static final float CAMERA_HEIGHT = SCREEN_HEIGHT / 100 * ZOOM;
    private static final float RUNNING_FRAME_DURATION = 0.06f;

    private World world;
    private OrthographicCamera cam;

    /** for debug rendering **/
    ShapeRenderer debugRenderer = new ShapeRenderer();

    /** Textures **/
    private TextureRegion blockTexture;

    private SpriteBatch spriteBatch;
    private boolean debug = false;
    private int width;
    private int height;
    //private float ppuX;	// pixels per unit on the X axis
    //private float ppuY;	// pixels per unit on the Y axis

    public WorldRenderer(World world, boolean debug) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.cam.update();
        this.debug = debug;
        spriteBatch = new SpriteBatch();
        loadTextures();
    }

    private void loadTextures() {
        Game.gameScreen.getPlayer().loadTextures();
        Block.loadTextures();
        Grass.loadTextures();
        Bridge.loadTextures();
        Arrow.loadTextures();
        Cloud.loadTextures();
    }

    public void render() {
        this.cam.position.set(getCameraXForPlayer(Game.gameScreen.getPlayer()), getCameraYForPlayer(Game.gameScreen.getPlayer()), 0);
        this.cam.update();
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        drawClouds();
        drawBlocks();
        drawPlayers();
        drawBridges();
        drawArrows();
        spriteBatch.end();
        debugRenderer.setProjectionMatrix(cam.combined);
        drawChat();
        drawCollisionBlocks();

        drawGrass();
        if (debug)
            drawDebug();

        Game.gameScreen.getPlayer().getInventory().render(spriteBatch);
    }

    private void drawChat() {
        Game.gameScreen.getClient().getChat().render(spriteBatch, debugRenderer);
    }

    private void drawBlocks() {
        for (Block block : world.getDrawableBlocks(Game.gameScreen.getPlayer(), (int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            block.render(spriteBatch);
        }
    }

    private void drawPlayers() {
        // Implies that I want more players!
        for (Player player : world.getPlayerList()) {
            player.render(spriteBatch);
        }
    }

    private void drawGrass() {
        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (Grass grass : world.getDrawableGrass(Game.gameScreen.getPlayer(), (int)CAMERA_WIDTH, (int)CAMERA_HEIGHT))
            grass.render(spriteBatch);
    }

    private void drawBridges() {
        for (Bridge bridge : world.getDrawableBridges(Game.gameScreen.getPlayer(), (int)CAMERA_WIDTH, (int)CAMERA_HEIGHT))
            bridge.render(spriteBatch);
    }

    private void drawArrows() {
        for (Arrow arrow : world.getProjectileList())
            arrow.render(spriteBatch);
    }

    private void drawClouds() {
        for (Cloud cloud : world.getCloudList())
            cloud.render(spriteBatch);
    }

    private void drawDebug() {
        // render blocks
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(new Color(1, 0, 0, 1));
        for (Block block : world.getDrawableBlocks(Game.gameScreen.getPlayer(), (int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            Rectangle rect = block.getBounds();
            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
        debugRenderer.setColor(Color.BLUE);
        for (Rectangle rect : world.getLevel().getBridgeRectangles()) {
            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        debugRenderer.setColor(Color.YELLOW);
        for (Arrow arrow : world.getProjectileList()) {
            Polygon rect = arrow.getBounds();
            // TODO : The arrow texture is not parallel
            debugRenderer.polygon(rect.getTransformedVertices());
            //debugRenderer.rect(rect., rect.y, arrow.getCenterOfMass().x, arrow.getCenterOfMass().y, rect.width, rect.height, 1, 1, (float) Math.toDegrees(arrow.getRotation()) % 360);
        }
        // Player debug
        Game.gameScreen.getPlayer().renderDebug(debugRenderer);
        debugRenderer.end();
    }

    private void drawCollisionBlocks() {
        if (debug) {
            debugRenderer.setProjectionMatrix(cam.combined);
            debugRenderer.begin(ShapeType.Filled);
            debugRenderer.setColor(Color.WHITE);
            for (Rectangle rect : world.getCollisionRects()) {
                debugRenderer.rect(rect.x, rect.y, rect.width, rect.height, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
            }
            debugRenderer.end();
        }
    }

    public float getCameraXForPlayer(Player player) {
        float camX = player.getPosition().x;
        if (camX < CAMERA_WIDTH / 2)
            camX = CAMERA_WIDTH / 2;
        else if (camX > world.getLevel().getWidth() - CAMERA_WIDTH / 2)
            camX = world.getLevel().getWidth() - CAMERA_WIDTH / 2;

        return camX;
    }

    public float getCameraYForPlayer(Player player) {
        float camY = player.getPosition().y;
        /*if (camY < CAMERA_HEIGHT / 2)
            camY = CAMERA_HEIGHT / 2;
        else if (camY > world.getLevel().getHeight() - CAMERA_HEIGHT / 2)
            camY = world.getLevel().getHeight() - CAMERA_HEIGHT / 2;*/
        return camY;
    }

    public void setSize (int w, int h) {
        this.width = w;
        this.height = h;
        //ppuX = (float)width / CAMERA_WIDTH;
        //ppuY = (float)height / CAMERA_HEIGHT;
    }
    public boolean isDebug() {
        return debug;
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
}
