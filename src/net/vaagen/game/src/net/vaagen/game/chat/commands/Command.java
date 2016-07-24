package net.vaagen.game.chat.commands;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Magnus on 7/20/16.
 */
public interface Command {

    boolean onCommandExecute(String command, String[] arguments);
    String[] getCommandKeyWords();

}
