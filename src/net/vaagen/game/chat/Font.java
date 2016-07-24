package net.vaagen.game.chat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Magnus on 7/19/16.
 */
public class Font {

    public static final String[] CHARACTER_POSITION = new String[]{
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            " ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?",
            "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_",
            "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "", "",
    };

    public static final int TEXTURE_WIDTH = 16, TEXTURE_HEIGHT = 16;
    private TextureRegion[][] fontTexture = new TextureRegion(new Texture("images/font.png")).split(26, 26);

    public TextureRegion[] getTextureFromString(String text) {
        TextureRegion[] textureRegion = new TextureRegion[text.length()];
        for (int i = 0; i < text.length(); i++)
            textureRegion[i] = getTextureFromCharacter("" + text.charAt(i));

        return textureRegion;
    }

    private TextureRegion getTextureFromCharacter(String character) {
        for (int i = 0; i < CHARACTER_POSITION.length; i++) {
            if (CHARACTER_POSITION[i].equals(character)) {
                return fontTexture[(int)(Math.floor((float) i / TEXTURE_WIDTH))][i % TEXTURE_WIDTH];

            }
        }

        return fontTexture[0][0];
    }

}
