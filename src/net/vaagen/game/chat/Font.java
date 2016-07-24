package net.vaagen.game.chat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Magnus on 7/19/16.
 */
public class Font {

    public static Font font_1 = new Font(
            new String[]{
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    " ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/",
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?",
                    "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                    "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_",
                    "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                    "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "", "",
            }, 16, 16, new TextureRegion(new Texture("images/font.png")).split(26, 26)
    );
    public static Font font_2 = new Font(
            new String[]{
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    " ", "!", "\"", "#", "%", "", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/",
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?",
                    "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                    "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_",
                    "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                    "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "", "",
            }, 16, 16, new TextureRegion(new Texture("images/font_3.png")).split(12, 12)
    );

    private String[] characterPosition;

    private int textureWidth, textureHeight;
    private TextureRegion[][] fontTexture;

    public Font(String[] characterPosition, int textureWidth, int textureHeight, TextureRegion[][] fontTexture) {
        this.characterPosition = characterPosition;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.fontTexture = fontTexture;
    }

    public TextureRegion[] getTextureFromString(String text) {
        TextureRegion[] textureRegion = new TextureRegion[text.length()];
        for (int i = 0; i < text.length(); i++)
            textureRegion[i] = getTextureFromCharacter("" + text.charAt(i));

        return textureRegion;
    }

    private TextureRegion getTextureFromCharacter(String character) {
        for (int i = 0; i < characterPosition.length; i++) {
            if (characterPosition[i].equals(character)) {
                return fontTexture[(int)(Math.floor((float) i / textureWidth))][i % textureWidth];
            }
        }

        return fontTexture[0][0];
    }

}
