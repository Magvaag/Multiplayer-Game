package net.vaagen.game.multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Magnus on 2/10/2016.
 */
public class ClientPackageListener extends Thread {

    private Client client;
    private Socket socket;

    public ClientPackageListener(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;

        start();
    }

    public void run() {
        while (!socket.isClosed() && socket.isConnected()) {
            try {
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String sPackage = inputStream.readLine();

                client.readPackage(sPackage);
            } catch (IOException e) {
                client.disconnectFromServer();
            }
        }
    }

}
