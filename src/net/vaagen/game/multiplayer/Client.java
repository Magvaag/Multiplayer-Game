package net.vaagen.game.multiplayer;

import com.badlogic.gdx.math.Vector2;
import net.vaagen.game.Game;
import net.vaagen.game.screens.GameScreen;
import net.vaagen.game.world.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Magnus on 2/10/2016.
 */
public class Client {

    private ClientPackageListener clientPackageListener;
    private Socket socket;
    private boolean hasReceivedId;

    public Client(String serverIp, int serverPort) {
        System.out.println("Client created!");

        try {
            System.out.println("Connecting to socket..");
            socket = new Socket(serverIp, serverPort);
            System.out.println("Successfully connected to server!");

            // We need to listen for incoming packages
            clientPackageListener = new ClientPackageListener(this, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPackage(String input) {
        try {
            PrintStream inputStream = new PrintStream(socket.getOutputStream());

            // Make sure you have actually established contact
            if (hasReceivedId) {
                inputStream.println(input);
                System.out.println("Sending!: " + input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMovePackage() {
        Player player = Game.gameScreen.getPlayer();
        sendPackage("player-location:{" + player.getPlayerId() + "," + player.getPosition().x + "," + player.getPosition().y + "}");
    }

    public void sendStatePackage() {
        Player player = Game.gameScreen.getPlayer();
        sendPackage("player-state:{" + player.getPlayerId() + "," + player.getState().ordinal() + "}");
    }

    public void readPackage(String sPackage) {
        String[] args = sPackage.split(":");
        String action = args[0];
        String[] values = args[1].replace("{", "").replace("}", "").split(",");

        if (action.equals("add-player")) {
            Player player = new Player();
            player.setPlayerId(Integer.parseInt(values[0]));
            Game.gameScreen.getWorld().addPlayer(player);
            System.out.println("Adding player!, he has the id of " + values[0]);
        } else if (action.equals("player-location")) {
            Player player = Game.gameScreen.getWorld().getPlayerFromId(Integer.parseInt(values[0]));
            System.out.println("package: " + sPackage + ", 1:" + values[1] + ", 2:" + values[2]);
            player.setPosition(new Vector2(Float.parseFloat(values[1]), Float.parseFloat(values[2])));
            System.out.println("Setting position for id, " + values[0]);
        } else if (action.equals("your-id")) {
            System.out.println("Setting id, " + values[0]);
            Game.gameScreen.getPlayer().setPlayerId(Integer.parseInt(values[0]));
            hasReceivedId = true;

            try {
                PrintStream inputStream = new PrintStream(socket.getOutputStream());
                inputStream.println("I have received my id!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (action.equals("player-state")) {
            Player player = Game.gameScreen.getWorld().getPlayerFromId(Integer.parseInt(values[0]));
            player.setState(Player.State.values()[Integer.parseInt(values[1])]);
            System.out.println("Setting the player to the state " + Player.State.values()[Integer.parseInt(values[1])].name());
        }

        System.out.println("Package received! " + sPackage);
    }

}
