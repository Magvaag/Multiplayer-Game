package net.vaagen.multiplayer.server;

import java.io.*;
import java.net.Socket;
import java.sql.Time;

/**
 * Created by Magnus on 2/10/2016.
 */
public class ServerSocketGameThread extends Thread {

    private ServerGameRoom gameRoom;
    private Socket socket;
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
        System.out.println("[" + new Time(System.currentTimeMillis()).toString() + "]: User " + playerId + " connected to the server.");
        gameRoom.addToRoom(this);

        // We are now connected and added to the room
        try {
            while (socket.isConnected() && gameRoom.isThreadStillConnected(this)) { // TODO : Is socket closed?
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader inputStream = new BufferedReader(inputStreamReader);
                String input = inputStream.readLine();

                gameRoom.sendPackageToAll(this, input);
            }
        } catch (IOException e) {
            gameRoom.playerDisconnect(this);
            System.out.println("[" + new Time(System.currentTimeMillis()).toString() + "]: User " + playerId + " disconnected from the server.");
        }
    }

}
