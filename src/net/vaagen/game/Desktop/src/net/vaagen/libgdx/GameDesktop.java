package net.vaagen.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import net.vaagen.desktopdownloader.DownloaderUtil;
import net.vaagen.game.Game;

/**
 * Created by Magnus on 10/15/2015.
 */
public class GameDesktop {

    public static void main(String[] args) {
        // Workaround for : Pixel format not accelerated, not working
        // https://github.com/libgdx/libgdx/issues/997

        //System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
        new LwjglApplication(new Game(), "Multiplayer Game! -Magnus Morud Våågen, February 2k16", 1280, 720);
    }

}
