package net.vaagen.game.multiplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Magnus on 7/24/2016.
 */
public class PackageManager {

    private static final List<Package> registeredPackages = new ArrayList<>();

    static {
        registerPackage(new ChatPackage(null));
    }

    public static void registerPackage(Package pack) {
        registeredPackages.add(pack);
    }

    public static Package getPackageForKeyword(String keyword) {
        for (Package p : registeredPackages) {
            if (p.getKeyword().equals(keyword)) {
                return p;
            }
        }

        return null;
    }

}
