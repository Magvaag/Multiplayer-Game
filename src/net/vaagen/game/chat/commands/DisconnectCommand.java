package net.vaagen.game.chat.commands;

import net.vaagen.game.Game;

/**
 * Created by Magnus on 7/20/16.
 */
public class DisconnectCommand implements Command {
    @Override
    public boolean onCommandExecute(String command, String[] arguments) {
        if (arguments.length > 0) {
            Game.gameScreen.getClient().getChat().addText("Usage: /Disconnect");
            return true;
        }

        if (!Game.gameScreen.getClient().isConnected()) {
            Game.gameScreen.getClient().getChat().addText("Error: Not connected to any server.");
            return true;
        }

        Game.gameScreen.getClient().disconnectFromServer();
        return true;
    }

    @Override
    public String[] getCommandKeyWords() {
        return new String[]{"Disconnect"};
    }
}
