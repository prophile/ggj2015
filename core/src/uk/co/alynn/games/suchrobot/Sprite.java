package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public enum Sprite {
    ROBOT_DEBUG("robot.png", 1, 1.0f, 16, 16),
    NODE_DEBUG("node.png", 1, 1.0f, 16, 16),
    ROBOT_IDLE("Animations/RoboidleAlpha/RoboIdle/RoboIdle_00001_%.png", 24, 0.1f, 512, 380);
    
    private String path;
    private float scale;
    private int anchorX, anchorY;

    private Sprite(String path, int frameCount, float scale, int anchorX, int anchorY) {
        this.path = path.replace("%", "00000");
        this.scale = scale;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
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
        batch.draw(tex, x - (this.anchorX * scale), y - (this.anchorY * scale), tex.getWidth() * scale, tex.getHeight() * scale);
    }
}
