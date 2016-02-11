package net.vaagen.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import net.vaagen.game.controller.PlayerController;
import net.vaagen.game.multiplayer.Client;
import net.vaagen.game.view.WorldRenderer;
import net.vaagen.game.world.World;
import net.vaagen.game.world.entity.Player;

/**
 * Created by Magnus on 2/6/2016.
 */
public class GameScreen implements Screen, InputProcessor {

    private Client client;
    private World world;
    private WorldRenderer renderer;
    private PlayerController controller;

    private int width, height;

    @Override
    public void show() {
        world = new World();
        controller = new PlayerController(world);
        renderer = new WorldRenderer(world, false);
        Gdx.input.setInputProcessor(this);
        client = new Client("137.117.248.37", 50001); // 137.117.248.37 -> scratchforfun.net // 25.141.152.199 -> Hamatchi
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2353f, 0.9373f, 0.898f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Just setting the lowest fps possible, just to make sure the user doesn't clip through the ground / other objects
        if (delta > 1F/20)
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
        if (PlayerController.Keys.LEFT.getInputKey().contains(keycode))
            controller.leftPressed();
        if (PlayerController.Keys.RIGHT.getInputKey().contains(keycode))
            controller.rightPressed();
        if (PlayerController.Keys.JUMP.getInputKey().contains(keycode))
            controller.jumpPressed();
        if (PlayerController.Keys.SLIDE.getInputKey().contains(keycode))
            controller.slidePressed();
        if (PlayerController.Keys.FIRE.getInputKey().contains(keycode))
            controller.firePressed();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (PlayerController.Keys.LEFT.getInputKey().contains(keycode))
            controller.leftReleased();
        if (PlayerController.Keys.RIGHT.getInputKey().contains(keycode))
            controller.rightReleased();
        if (PlayerController.Keys.JUMP.getInputKey().contains(keycode))
            controller.jumpReleased();
        if (PlayerController.Keys.SLIDE.getInputKey().contains(keycode))
            controller.slideReleased();
        if (PlayerController.Keys.FIRE.getInputKey().contains(keycode))
            controller.fireReleased();
        if (keycode == Input.Keys.D) // Debug
            renderer.setDebug(!renderer.isDebug());
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        if (x < width / 2 && y > height / 2) {
            controller.leftPressed();
        }
        if (x > width / 2 && y > height / 2) {
            controller.rightPressed();
        }
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;
        if (x < width / 2 && y > height / 2) {
            controller.leftReleased();
        }
        if (x > width / 2 && y > height / 2) {
            controller.rightReleased();
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
}
