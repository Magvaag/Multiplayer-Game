package net.vaagen.game.chat.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 7/20/16.
 */
public class CommandManager {

    private static List<Command> registeredCommands = new ArrayList<>();

    public static void registerCommands() {
        registerCommand(new ConnectCommand());
        registerCommand(new DisconnectCommand());
    }

    public static void registerCommand(Command command) {
        // Loop through all the keywords of the command
        for (String keyWord : command.getCommandKeyWords()) {
            for (Command c : registeredCommands) {
                for (String k : c.getCommandKeyWords()) {
                    if (keyWord.equalsIgnoreCase(k)) {
                        System.out.println("Error : registerCommand:CommandManager.java");
                        return;
                    }
                }
            }
        }

        registeredCommands.add(command);
    }

    public static boolean executeCommand(String command, String[] arguments) {
        System.out.println("User  issued command \"" + command + "\"");

        for (Command c : registeredCommands) {
            for (String keyWord : c.getCommandKeyWords()) {
                if (command.equalsIgnoreCase(keyWord)) {
                    return c.onCommandExecute(command, arguments);
                }
            }
        }

        System.out.println("Invalid command :: \"" + command + "\".");
        return false;
    }

}
