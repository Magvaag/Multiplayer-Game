package net.vaagen.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import net.vaagen.desktopdownloader.DownloaderUtil;
import net.vaagen.game.Game;

/**
 * Created by Magnus on 10/15/2015.
 */
public class GameDesktop {

    public static String VERSION = "Pre-Alpha v0.002";
    private static boolean upToDate = true;

    public static void main(String[] args) {
        new LwjglApplication(new Game(), "Libgdx Game #1!", 1280, 720);
    }

    private static void keepGameUpdated() {
        String latestVersion = DownloaderUtil.getLatestVersion();
        if (!latestVersion.equals(VERSION))
            upToDate = false;
    }

}
