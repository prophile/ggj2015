package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public enum Sprite {
    ROBOT_DEBUG("robot.png", 1, 1.0f),
    NODE_DEBUG("node.png", 1, 1.0f),
    ROBOT_IDLE("Animations/Robo-idle/RoboIdle_%.png", 24, 0.1f);
    
    private String path;
    private float scale;

    private Sprite(String path, int frameCount, float scale) {
        this.path = path.replace("%", "00000");
        this.scale = scale;
    }
    
    public void queueLoad() {
        TextureParameter param = new TextureParameter();
        param.minFilter = TextureFilter.MipMapLinearLinear;
        param.genMipMaps = true;
        AssetManager mgr = Overlord.get().assetManager;
        mgr.load(path, Texture.class);
    }
    
    private Texture getTexture() {
        AssetManager mgr = Overlord.get().assetManager;
        Texture tex = mgr.get(path, Texture.class);
        if (tex == null) {
            throw new RuntimeException("Could not get texture " + name());
        }
        return tex;
    }
    
    public void draw(SpriteBatch batch, float x, float y) {
        Texture tex = getTexture();
        batch.draw(tex, x, y, tex.getWidth() * scale, tex.getHeight() * scale);
    }
}
