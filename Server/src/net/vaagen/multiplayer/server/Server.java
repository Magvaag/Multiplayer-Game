package net.vaagen.multiplayer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Magnus on 2/10/2016.
 */
public class Server extends Thread {

    private ServerGameRoom gameRoom;
    private volatile boolean running;
    private int port;
    private int maxQ;

    // Main constructor
    public Server(int port, int maxQ) {
        // Set the port to listen for incoming calls
        this.port = port;
        // Set the amount of people able to listen to this port at once
        this.maxQ = maxQ;
    }

    public void run() {
        gameRoom = new ServerGameRoom();
        running = true;

        try {
            ServerSocket serverSocket = new ServerSocket(port, maxQ);

            System.out.println("Server initialized on port " + serverSocket.getLocalPort());
            System.out.println("");

            // Accept incoming transmissions
            while (true) {
                // This will just wait until a user connects
                Socket clientSocket = serverSocket.accept();
                new ServerSocketGameThread(clientSocket, gameRoom);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Creates a server with the specified port, and make a call to start the thread
        Server server = new Server(13338, 10);
        server.start();
    }

}
