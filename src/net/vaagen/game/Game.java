package net.vaagen.game;

import net.vaagen.game.screens.GameScreen;

/**
 * Created by Magnus on 2/6/2016.
 */
public class Game extends com.badlogic.gdx.Game {

    @Override
    public void create() {
        setScreen(new GameScreen());
    }

}
