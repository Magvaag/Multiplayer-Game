package net.vaagen.game.chat.commands;

import net.vaagen.game.Game;
import net.vaagen.game.multiplayer.Client;

/**
 * Created by Magnus on 7/20/16.
 */
public class ConnectCommand implements Command {

    @Override
    public boolean onCommandExecute(String command, String[] arguments) {
        if (arguments.length == 1) {
            String ip = arguments[0].split(":")[0];

            int port = Client.DEFAULT_PORT;
            try {
                if (arguments[0].contains(":"))
                    port = Integer.parseInt(arguments[0].split(":")[1]);
            } catch (Exception e) {
                System.out.println("Error: Port invalid.");
                return true;
            }

            Game.gameScreen.getClient().connectToServer(ip, port);
            return true;
        } else
            System.out.println("Usage: /connect [IP-Address]");
        return true;
    }

    @Override
    public String[] getCommandKeyWords() {
        return new String[]{"Connect"};
    }
}
