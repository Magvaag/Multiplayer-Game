package net.vaagen.game.chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.vaagen.game.Game;
import net.vaagen.game.chat.commands.CommandManager;
import net.vaagen.game.world.entity.Player;

import java.util.*;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

/**
 * Created by Magnus on 7/17/16.
 */
public class Chat {

    private static final String COMMAND_STARTS_WITH = "/";

    private Font font;
    private float chatPosX = -9.5F, chatPosY = -3.8F;
    private final float SIZE = 0.4F;
    private long textLength = 15000L;
    private long typingCursorLength = 500L;

    private String text;
    private boolean isTyping;
    private long typeStart;

    private List<ChatMessage> chatMessageLog = new ArrayList<>();
    private List<ChatMessage> chatMessageRender = new ArrayList<>();

    public Chat(){
        font = Font.font_2;
        CommandManager.registerCommands();
    }

    public void render(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        List<ChatMessage> removedChatMessages = new ArrayList<>();
        for (int h = 0; h < chatMessageRender.size(); h++) {
            ChatMessage chatMessage = chatMessageRender.get(h);

            if (System.currentTimeMillis() - chatMessage.getDate() > textLength) {
                chatMessageLog.add(chatMessage);
                removedChatMessages.add(chatMessage);
                continue;
            }
            drawChatText(spriteBatch, shapeRenderer, chatMessage.getMessage(), chatMessageRender.size() - 1 - h);
        }

        for (ChatMessage chatMessage : removedChatMessages)
            chatMessageRender.remove(chatMessage);

        if (isTyping()) {
            spriteBatch.setColor(Color.YELLOW);
            drawChatText(spriteBatch, shapeRenderer, getTyping(), -1);
            if ((System.currentTimeMillis() - typeStart) % 1000 < 500) {
                spriteBatch.begin();
                TextureRegion textureRegion = font.getTextureFromString("_")[0];
                spriteBatch.draw(textureRegion, getChatPosX() + getTyping().length() * getCharPosition(1), getChatPosY(-1) + 0.05F, SIZE, SIZE);
                spriteBatch.end();
            }
            spriteBatch.setColor(Color.WHITE);
        }
    }

    public void drawChatText(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, String text, int h) {
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.2F));
        shapeRenderer.rect(chatPosX + Game.gameScreen.getRenderer().getCameraXForPlayer(Game.gameScreen.getPlayer()), h * SIZE + chatPosY + Game.gameScreen.getRenderer().getCameraYForPlayer(Game.gameScreen.getPlayer()), text.length() * (SIZE - 0.06F) + (h == -1 ? SIZE : 0), SIZE);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL_BLEND);

        renderText(spriteBatch, text, getChatPosX(), getChatPosY(h), SIZE, SIZE);
    }

    public void renderText(SpriteBatch spriteBatch, String text, float x, float y, float scaleX, float scaleY) {
        TextureRegion[] textureRegion = font.getTextureFromString(text);
        spriteBatch.begin();
        for (int i = 0; i < textureRegion.length; i++)
            spriteBatch.draw(textureRegion[i], x + getCharPosition(i) + Game.gameScreen.getRenderer().getCameraXForPlayer(Game.gameScreen.getPlayer()), y + Game.gameScreen.getRenderer().getCameraYForPlayer(Game.gameScreen.getPlayer()), scaleX, scaleY);
        spriteBatch.end();
    }

    public float getCharPosition(int i) {
        return i * (SIZE - 0.06F);
    }

    public float getChatPosX() {
        return chatPosX;
    }

    public float getChatPosY(int h) {
        return h * SIZE + chatPosY;
    }

    public void addText(Player player, String text, long date) {
        chatMessageRender.add(new ChatMessage(player, text, date));
    }

    public void addText(Player player, String text) {
        addText(player, text, System.currentTimeMillis());
    }

    public void addText(String text) {
        addText(null, text);
    }

    public void setTyping() {
        this.isTyping = true;
        this.text = "";
        this.typeStart = System.currentTimeMillis();
    }

    public void cancelTyping() {
        this.isTyping = false;
        this.text = "";
    }

    public void type(char keyCode) {
        if ((int)keyCode == 0 || (int)keyCode == 13)
            return;
        if ((int)keyCode == 8) {
            if (!this.text.equals(""))  {
                this.text = text.substring(0, this.text.length()-1);
            }
            return;
        }

        this.text += keyCode + "";
    }

    public void sendMessage(Player player) {
        System.out.println("[Chat]: " + getTyping());
        addText(player, getTyping(), System.currentTimeMillis());
        if (getTyping().startsWith(COMMAND_STARTS_WITH)) {
            String typing = getTyping();
            String[] split = typing.split(" ");
            String command = split[0].substring(1);
            String[] arguments = split.length == 1 ? new String[0] : Arrays.copyOfRange(split, 1, split.length);
            // Execute the command
            CommandManager.executeCommand(command, arguments);
        } else {
            // TODO : Send as package as well
        }
        cancelTyping();
    }

    public boolean isTyping() {
        return isTyping;
    }
    public String getTyping() {
        return this.text;
    }

    public class ChatMessage {

        private Player sender;
        private String message;
        private long date;

        public ChatMessage(Player player, String message, long date) {
            this.sender = player;
            this.message = message;
            this.date = date;
        }

        public Player getSender() {
            return sender;
        }

        public String getMessage() {
            return message;
        }

        public long getDate() {
            return date;
        }
    }
}
