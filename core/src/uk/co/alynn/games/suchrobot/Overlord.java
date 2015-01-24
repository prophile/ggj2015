package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;

public final class Overlord {
    private static Overlord s_instance = null;
    
    public final AssetManager assetManager;
    
    private Overlord() {
        assetManager = new AssetManager();
    }

    private void configure() {
        initSprites();
        assetManager.finishLoading(); // TODO: improve me
    }
    
    private static void initSprites() {
        for (Sprite sprite : Sprite.values()) {
            sprite.queueLoad();
        }
    }
    
    public static void init() {
        if (s_instance != null) {
            throw new RuntimeException("Reinit of Overlord");
        }
        s_instance = new Overlord();
        s_instance.configure();
    }
    
    public static Overlord get() {
        if (s_instance == null) {
            throw new RuntimeException("Overlord not initialised.");
        }
        return s_instance;
    }
}
