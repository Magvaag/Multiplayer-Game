package net.vaagen.game.multiplayer;

import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.world.entity.Player;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Magnus on 2/10/2016.
 */
public class Client {

    private Socket socket;
    private boolean hasReceivedId;
    private long lastMovePackage;

    public Client(String serverIp, int serverPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(serverIp, serverPort);

                    // We need to listen for incoming packages
                    if (socket != null && socket.isConnected())
                        new ClientPackageListener(Client.this, socket);
                } catch (IOException e) {
                    //e.printStackTrace();
                    // This exception is printed out prettier out below
                } finally {
                    if (socket != null && socket.isConnected())
                        System.out.println("Connection established on to \"" + serverIp + ":" + serverPort + "\"!");
                    else
                        System.out.println("Unable to connect to Game Server, playing offline.");
                    System.out.println();
                }
            }
        }).start();
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

    public void disconnect() {
        // The server is probably unreachable at this point, so no need to ask it anything
        socket = null;
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
}
