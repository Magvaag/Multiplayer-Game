package net.vaagen.game.multiplayer;

import net.vaagen.game.Game;
import net.vaagen.game.chat.Chat;
import net.vaagen.game.world.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Magnus on 7/24/2016.
 */
public class ChatPackage implements Package {

    private Chat.ChatMessage chatMessage;

    public ChatPackage(Chat.ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    @Override
    public String encode() {
        String s = getKeyword() + ":{" + chatMessage.getSender().getPlayerId() + "," + chatMessage.getDate() + "," + chatMessage.getMessage() + "}";
        return s;
    }

    @Override
    public void decode(String pack) {
        String[] code = pack.substring((getKeyword() + ":{").length(), pack.length()-1).split(",");
        Player player = Game.gameScreen.getWorld().getPlayerFromId(Integer.parseInt(code[0]));
        long ms = Long.parseLong(code[1]);
        String message = Arrays.stream(Arrays.copyOfRange(code, 2, code.length)).collect(Collectors.joining(" "));

        Chat.ChatMessage chatMessage = new Chat.ChatMessage(player, message, ms);
        Game.gameScreen.getClient().getChat().printText(chatMessage);
    }

    @Override
    public String getKeyword() {
        return "chat";
    }
}
