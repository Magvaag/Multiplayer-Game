package net.vaagen.desktopdownloader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

/**
 * Created by Magnus on 2/9/2016.
 */
public class DownloaderUtil {

    private static final String LATEST_VERSION_HTTP_LOCATION = "http://www.scratchforfun.net/download/multiplayer-game/version.txt";
    private static final String LATEST_FILE_HTTP_LOCATION = "http://www.scratchforfun.net/download/multiplayer-game/game.jar";
    private static final String GAME_LOCATION_ON_COMPUTER = System.getenv("APPDATA") + "\\ScratchForFun\\game.jar";

    public static String getLatestVersion() {
        try {
            URL url = new URL(LATEST_VERSION_HTTP_LOCATION);
            Scanner s = new Scanner(url.openStream());

            if (s.hasNext())
                return s.nextLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unknown latest version..";
    }

    public static void downloadLatestVersion() {
        try {
            URL website = new URL(LATEST_FILE_HTTP_LOCATION);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(GAME_LOCATION_ON_COMPUTER);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
