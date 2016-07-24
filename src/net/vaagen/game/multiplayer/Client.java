package net.vaagen.game.multiplayer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.chat.Chat;
import net.vaagen.game.world.entity.Player;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Magnus on 2/10/2016.
 */
public class Client {

    public static final int DEFAULT_PORT = 50001;

    private Socket socket;
    private Thread serverConnectionThread;
    private Chat chat;
    private boolean hasReceivedId;
    private long lastMovePackage;
    private String serverIp;
    private int serverPort;

    public Client(String serverIp, int serverPort) {
        chat = new Chat();
        chat.addText("Press T/Enter to chat!");

        connectToServer(serverIp, serverPort);
    }

    public void disconnectFromServer() {
        if (serverConnectionThread != null) {
            try { serverConnectionThread.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
            serverConnectionThread = null;
        }

        socket = null;
        getChat().addText("Disconnected from server.");
    }

    public boolean isConnected() {
        return (serverConnectionThread != null && serverConnectionThread.isAlive()) || socket != null;
    }

    public void connectToServer(String serverIp, int serverPort) {
        // Try to disconnect from the server if we are already connected
        if (isConnected())
            disconnectFromServer();

        this.serverIp = serverIp;
        this.serverPort = serverPort;

        serverConnectionThread = new Thread(() -> {
            try {

                System.out.println("Connecting to \"" + serverIp + ":" + serverPort + "\" ...");
                socket = new Socket(serverIp, serverPort);

                // We need to listen for incoming packages
                if (socket != null && socket.isConnected())
                    new ClientPackageListener(Client.this, socket);
            } catch (IOException e) {
                //e.printStackTrace();
                // This exception is printed out prettier below
            } finally {
                if (socket != null && socket.isConnected())
                    getChat().addText("Connection established to \"" + serverIp + ":" + serverPort + "\"!");
                else
                    getChat().addText("Unable to connect to game server, playing offline.");
            }
        });

        serverConnectionThread.start();
    }

    public void sendPackage(String input) {
        if (socket == null || !socket.isConnected())
            return;

        try {
            PrintStream inputStream = new PrintStream(socket.getOutputStream());

            // Make sure you have actually established contact
            if (hasReceivedId) {
                inputStream.println(input);
                //System.out.println("Sending!: " + input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatePackage() {
        Player player = Game.gameScreen.getPlayer();
        sendPackage("player-info:{" + player.getPlayerId() + "," + player.getPosition().x + "," + player.getPosition().y + "," + player.getVelocity().x + "," + player.getVelocity().y + "," + player.getState().ordinal() + "," + player.isFacingLeft() + "}");
        lastMovePackage = System.currentTimeMillis();
    }

    public void readPackage(String sPackage) {
        if (socket == null || !socket.isConnected())
            return;

        if (sPackage.equals(""))
            return;

        try {
            String[] args = sPackage.split(":");
            String action = args[0];
            String[] values = args[1].replace("{", "").replace("}", "").split(",");

            if (action.equals("add-player")) {
                Player player = new Player();
                player.setPlayerId(Integer.parseInt(values[0]));
                Game.gameScreen.getWorld().addPlayer(player);
            } else if (action.equals("player-info")) {
                Player player = Game.gameScreen.getWorld().getPlayerFromId(Integer.parseInt(values[0]));
                player.setPosition(new Vector2(Float.parseFloat(values[1]), Float.parseFloat(values[2])));
                player.setVelocity(new Vector2(Float.parseFloat(values[3]), Float.parseFloat(values[4])));
                player.setState(Player.State.values()[Integer.parseInt(values[5])]);
                player.setFacingLeft(Boolean.parseBoolean(values[6]));
            } else if (action.equals("your-id")) {
                Game.gameScreen.getPlayer().setPlayerId(Integer.parseInt(values[0]));
                hasReceivedId = true;

                try {
                    PrintStream inputStream = new PrintStream(socket.getOutputStream());
                    inputStream.println("I have received my id!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (action.equals("player-disconnect")) {
                Game.gameScreen.getWorld().removePlayerFromId(Integer.parseInt(values[0]));
            } else if (action.equals("get-player-info")) {
                sendUpdatePackage();
            }
            //System.out.println("Package received! " + sPackage);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading package: " + sPackage);
        }
    }

    public Chat getChat() {
        return chat;
    }
}