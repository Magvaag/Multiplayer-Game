package net.vaagen.downloader;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

/**
 * Created by Magnus on 2/9/2016.
 */
public class Main {

    public static boolean DOWNLOADING = false;
    private static final String LATEST_FILE_HTTP_LOCATION = "http://www.scratchforfun.net/download/multiplayer-game/game.jar";
    private static final String LATEST_VERSION_HTTP_LOCATION = "http://www.scratchforfun.net/download/multiplayer-game/version.txt";
    private static final String GAME_FILE_ON_COMPUTER = System.getenv("APPDATA") + "\\ScratchForFun\\game.jar";
    private static final String GAME_VERSION_ON_COMPUTER = System.getenv("APPDATA") + "\\ScratchForFun\\version.txt";

    public static void main(String[] args) {
        // Make sure the game has all the necessary files
        if (hasAllNecessaryFiles()) {
            System.out.println("You have all the necessary files!");
            if (isGameUpdated()) {
                System.out.println("Your game is up to date!");
                // Run the game
                startGame();
            } else {
                System.out.println("The game is not up to date!");
                downloadGameFiles();

                // TODO : Make sure it was a success!
                startGame();
            }
        }

        // Download all the necessary files from the interwebs
        else {
            downloadGameFiles();

            // TODO : Make sure it was a success!
            startGame();
        }
    }

    private static void startGame() {
        try {
            System.out.println("Starting game!");
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", GAME_FILE_ON_COMPUTER);
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadGameFiles() {
        JFrame frame = new JFrame("Downloading game files");
        frame.setSize(270, 100);
        frame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        final JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        frame.add(progressBar);
        frame.setVisible(true);



        System.out.println("Downloading game files!");
        try {
            // Start by creating the file so it exists
            File file = new File(GAME_FILE_ON_COMPUTER);
            file.getParentFile().mkdirs();
            file.createNewFile();
            // Download the new game file from the web
            URL website = new URL(LATEST_FILE_HTTP_LOCATION);
            URLConnection connection = website.openConnection();
            int totalSize = connection.getContentLength();

            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            final FileOutputStream fos = new FileOutputStream(GAME_FILE_ON_COMPUTER);

            DOWNLOADING = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (DOWNLOADING) {
                        try {
                            System.out.println("Percentage: " + ((double)fos.getChannel().size() / totalSize * 100) + "%");
                            progressBar.setValue((int) ((double)fos.getChannel().size() / totalSize * 100));
                            Thread.sleep(20);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            DOWNLOADING = false;

            // Start by creating the version file so it exists
            file = new File(GAME_VERSION_ON_COMPUTER);
            file.getParentFile().mkdirs();
            file.createNewFile();
            // Download the new version file from the web
            website = new URL(LATEST_VERSION_HTTP_LOCATION);
            rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos2 = new FileOutputStream(GAME_VERSION_ON_COMPUTER);
            fos2.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            frame.dispose();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasAllNecessaryFiles() {
        File file = new File(GAME_FILE_ON_COMPUTER);

        // Simple way to make sure the game files exists
        return file.exists();
    }

    private static boolean isGameUpdated() {
        String currentVersion = getCurrentVersion();
        String latestVersion = getLatestVersion();
        return currentVersion.equals(latestVersion);
    }

    private static String getCurrentVersion() {
        try {
            File file = new File(GAME_VERSION_ON_COMPUTER);
            if (!file.exists())
                return "Unknown-current";

            Scanner scanner = new Scanner(file);
            if (scanner.hasNext())
                return scanner.nextLine();
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        return "Unknown-current";
    }

    private static String getLatestVersion() {
        try {
            // Check the latest version
            URL url = new URL(LATEST_VERSION_HTTP_LOCATION);
            Scanner s = new Scanner(url.openStream());

            if (s.hasNext())
                return s.nextLine();
        } catch (FileNotFoundException e) { e.printStackTrace();
        } catch (MalformedURLException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }

        return "Unknown-latest";
    }

}
