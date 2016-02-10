package net.vaagen.multiplayer.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 2/10/2016.
 */
public class ServerGameRoom {

    private List<ServerSocketGameThread> socketGameThreadList;
    private int nextId = 0;

    public ServerGameRoom() {
        socketGameThreadList = new ArrayList<>();
    }

    public void addToRoom(ServerSocketGameThread thread) {
        if(!socketGameThreadList.contains(thread)) {
            // Add him to the room
            thread.setGameRoom(this);

            // Loop through before adding him to the list
            for (ServerSocketGameThread gameThread : socketGameThreadList) {
                // Let the other players know you joined
                sendJoinPackage(thread, gameThread);

                // Send information about the players already on the server
                sendJoinPackage(gameThread, thread);

                // TODO : Send the new player information about player position!
            }

            // Add him to the list
            socketGameThreadList.add(thread);

            System.out.println("Adding user to the game room! with the id " + thread.getPlayerId());
        } else
            System.out.println("User already in game room.");
    }

    public int getGameRoomId() {
        nextId++;
        return nextId-1;
    }

    public void sendJoinPackage(ServerSocketGameThread sender, ServerSocketGameThread receiver) {
        System.out.println("Sending, add player to id " + receiver.getPlayerId() + ", and the id I am sending is " + receiver.getPlayerId());
        receiver.sendPackageToClient("add-player:{" + sender.getPlayerId() + "}");
    }

    public void sendPackageToAll(ServerSocketGameThread sender, String sPackage) {
        for (ServerSocketGameThread gameThread : socketGameThreadList) {
            //System.out.println(socketGameThreadList.size());
            if (!sender.equals(gameThread)) {
                //System.out.println("Sending package!");
                gameThread.sendPackageToClient(sPackage);
            }
        }
    }



}
