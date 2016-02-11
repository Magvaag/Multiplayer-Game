package net.vaagen.multiplayer.server;

import net.vaagen.multiplayer.server.player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Magnus on 2/10/2016.
 */
public class ServerSocketGameThread extends Thread {

    private ServerGameRoom gameRoom;
    private Socket socket;
    private Player player;
    private int playerId;

    public ServerSocketGameThread(Socket socket, ServerGameRoom gameRoom) {
        this.socket = socket;
        this.gameRoom = gameRoom;
        this.setPriority(Thread.MIN_PRIORITY);

        this.start();
    }

    public void setGameRoom(ServerGameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public void sendPackageToClient(String sPackage) {
        try {
            PrintStream outputStream = new PrintStream(socket.getOutputStream());
            outputStream.println(sPackage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(ServerSocketGameThread thread) {
        if (thread == null)
            return false;
        return playerId == thread.getPlayerId();
    }

    public int getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return player;
    }

    public void run() {
        playerId = gameRoom.getGameRoomId();

        try {
            // We start by sending the id
            PrintStream outputStream = new PrintStream(socket.getOutputStream());
            outputStream.println("your-id:{" + playerId + "}");

            // Then we wait for confirmation
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the player to the game room
        System.out.println("User " + playerId + " connected to the server.");
        gameRoom.addToRoom(this);

        try {
            while (socket.isConnected()) {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input = inputStream.readLine();

                gameRoom.sendPackageToAll(this, input);
            }
        } catch (IOException e) {
            gameRoom.playerDisconnect(this);
            System.out.println("User " + playerId + " disconnected from the server.");
        }
    }

}
