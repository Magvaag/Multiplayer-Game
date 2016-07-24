package net.vaagen.game.multiplayer;

/**
 * Created by Magnus on 7/24/2016.
 */
public interface Package {

    String encode();
    void decode(String pack);
    String getKeyword();

}
