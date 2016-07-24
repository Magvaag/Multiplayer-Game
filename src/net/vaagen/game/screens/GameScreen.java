package net.vaagen.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.controller.PlayerController;
import net.vaagen.game.multiplayer.Client;
import net.vaagen.game.view.WorldRenderer;
import net.vaagen.game.world.Cloud;
import net.vaagen.game.world.World;
import net.vaagen.game.world.entity.Player;

import java.util.Random;

/**
 * Created by Magnus on 2/6/2016.
 */
public class GameScreen implements Screen, InputProcessor {

    private Client client;
    private World world;
    private WorldRenderer renderer;
    private PlayerController controller;
    private ShapeRenderer shapeRenderer;
    private Color backgroundBottom;
    private Color backgroundTop;

    private int width, height;

    @Override
    public void show() {
        world = new World();
        controller = new PlayerController(world);
        renderer = new WorldRenderer(world, false);
        Gdx.input.setInputProcessor(this);
        client = new Client("127.0.0.1", 50001); // 137.117.248.37 -> scratchforfun.net // 25.141.152.199 -> Hamatchi // 127.0.0.1 -> Localhost
        shapeRenderer = new ShapeRenderer();

        backgroundBottom = new Color(0.88627F, 0.945098F, 0.6117647F, 1F);
        backgroundTop = new Color(0.70588F, 1F, 0.9294117F, 1F);

        // Load the clouds, the natural way
        boolean reachedEnd = false;
        float nextCloud = 0;
        while (!reachedEnd) {
            float delta = 0.1F;
            for (Cloud cloud : world.getCloudList()) {
                cloud.update(delta);
                if (cloud.isDead()) {
                    world.getCloudList().remove(cloud);
                    reachedEnd = true;
                }
            }
            nextCloud += new Random().nextFloat() * delta;
            int amountOfClouds = (int) (nextCloud / 10F);
            for (int c = 0; c < amountOfClouds; c++) {
                nextCloud -= 10;
                world.addCloud(new Cloud(new Vector2(-4, new Random().nextFloat() * Game.gameScreen.getWorld().getLevel().getHeight()), new Vector2(0.04F + new Random().nextFloat() * 0.2F, (new Random().nextFloat() - 0.5F) * 0.01F)));
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // 0.2353f, 0.9373f, 0.898f, 1
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(renderer.getSpriteBatch().getProjectionMatrix());
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(renderer.getCameraXForPlayer(Game.gameScreen.getPlayer()) - WorldRenderer.CAMERA_WIDTH / 2, renderer.getCameraYForPlayer(Game.gameScreen.getPlayer()) - WorldRenderer.CAMERA_HEIGHT / 2, WorldRenderer.CAMERA_WIDTH, WorldRenderer.CAMERA_HEIGHT, backgroundBottom, backgroundBottom, backgroundTop, backgroundTop);
        shapeRenderer.end();

        // Just setting the lowest fps possible, just to make sure the user doesn't clip through the ground / other objects
        if (delta > 1F / 20)
            delta = 1F / 20;

        controller.update(delta);
        world.update();
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        renderer.setSize(width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
    }

    // * InputProcessor methods ***************************//

    @Override
    public boolean keyDown(int keycode) {
        if (client.getChat().isTyping() && !(PlayerController.Keys.CHAT_START.getInputKey().contains(keycode) || PlayerController.Keys.CHAT_END.getInputKey().contains(keycode)))
            return true;

        for (PlayerController.Keys key : PlayerController.Keys.values()) {
            if (key.getInputKey().contains(keycode))
                controller.keyPressed(key);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (PlayerController.Keys key : PlayerController.Keys.values()) {
            if (key.getInputKey().contains(keycode))
                controller.keyReleased(key);
        }

        if (!client.getChat().isTyping() && PlayerController.Keys.DEBUG.getInputKey().contains(keycode))
            renderer.setDebug(!renderer.isDebug());
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        if (client.getChat().isTyping() && !PlayerController.Keys.CHAT_START.getInputKey().contains(character)) {
            client.getChat().type(character);
            return true;
        }

        return true;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        if (x < width / 2 && y > height / 2) {
            controller.keyPressed(PlayerController.Keys.LEFT);
        }
        if (x > width / 2 && y > height / 2) {
            controller.keyPressed(PlayerController.Keys.RIGHT);
        }
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        if (x < width / 2 && y > height / 2) {
            controller.keyReleased(PlayerController.Keys.LEFT);
        }
        if (x > width / 2 && y > height / 2) {
            controller.keyReleased(PlayerController.Keys.RIGHT);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    public Client getClient() {
        return client;
    }

    public PlayerController getController() {
        return controller;
    }

    public Player getPlayer() {
        return controller.getPlayer();
    }

    public World getWorld() {
        return world;
    }

    public WorldRenderer getRenderer() {
        return renderer;
    }

}