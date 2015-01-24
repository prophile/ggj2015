package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public enum Sprite {
    BADLOGIC("badlogic.jpg"),
    NODE_DEBUG("node.png");
    
    private String path;

    private Sprite(String path) {
        this.path = path;
    }
    
    public void queueLoad() {
        AssetManager mgr = Overlord.get().assetManager;
        mgr.load(path, Texture.class);
    }
    
    public Texture getTexture() {
        AssetManager mgr = Overlord.get().assetManager;
        Texture tex = mgr.get(path, Texture.class);
        if (tex == null) {
            throw new RuntimeException("Could not get texture " + name());
        }
        return tex;
    }
}
