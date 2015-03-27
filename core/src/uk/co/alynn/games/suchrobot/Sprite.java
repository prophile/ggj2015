package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public enum Sprite {
    ROBOT_DEBUG("robot-debug.png", 1, 1.0f, 16, 16),
    NODE_DEBUG("node-debug.png", 1, 1.0f, 16, 16),
    ROBOT_IDLE("animations-robot-idle-%.png", 24, 0.5f, 102, 76),
    ROBOT_WALK("animations-robot-walk-%.png", 24, 0.5f, 102, 76),
    ROBOT_EAT_DIRT("animations-robot-eat-dirt-%.png", 24, 0.5f, 102, 76),
    ROBOT_EAT_WATER("animations-robot-eat-water-%.png", 24, 0.5f, 102, 76),
    ROBOT_FLAIL_START("animations-robot-flail-start-%.png", 24, 0.5f, 102, 76),
    ROBOT_FLAIL("animations-robot-flail-%.png", 24, 0.5f, 102, 76),
    ROBOT_FLAIL_END("animations-robot-flail-end-%.png", 24, 0.5f, 102, 76),
    NODE_MINE("nodesrough-node-mine.png", 1, 0.5f, 85, 30),
    NODE_SALVAGE("nodesrough-node-salvage.png", 1, 0.5f, 100, 70),
    NODE_SHIP("nodesrough-node-ship.png", 1, 0.5f, 142, 104),
    NODE_WELL("nodesrough-node-well.png", 1, 0.5f, 88, 24),
    NODE_WELL_DEPLETED("nodesrough-node-well-depleted.png", 1, 0.5f, 88, 24),
    NODE_MINE_DEPLETED("nodesrough-node-mine-depleted.png", 1, 0.5f, 85, 30),
    ICON_WATER("ui-icon-water.png", 1, 0.5f, 49, 44),
    ICON_METAL("ui-icon-metal.png", 1, 0.5f, 49, 44),
    ICON_SALVAGE("ui-icon-salvage.png", 1, 0.5f, 49, 44),
    ICON_OFFSCREEN_FLAIL("ui-icon-offscreen-flail.png", 1, 0.5f, 49, 44),
    ICON_OFFSCREEN_IDLE("ui-icon-offscreen-idle.png", 1, 0.5f, 49, 44),
    ICON_OFFSCREEN_WALK("ui-icon-offscreen-walk.png", 1, 0.5f, 49, 44),
    CHEVRONS_L1("ui-chevrons-l1.png", 1, 0.5f, 28, 16),
    CHEVRONS_L2("ui-chevrons-l2.png", 1, 0.5f, 28, 16),
    CHEVRONS_L3("ui-chevrons-l3.png", 1, 0.5f, 28, 16);

    private String path;
    private float scale;
    private int anchorX, anchorY;
    private int frames;

    private Sprite(String path, int frameCount, float scale, int anchorX,
            int anchorY) {
        this.path = path;
        this.frames = frameCount;
        this.scale = scale;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    public void queueLoad() {
        TextureParameter param = new TextureParameter();
        param.minFilter = TextureFilter.MipMapLinearLinear;
        param.genMipMaps = true;
        AssetManager mgr = Overlord.get().assetManager;
        for (int i = 0; i < this.frames; ++i) {
            String longDigits = String.format("%05d", i);
            String truePath = this.path.replace("%", longDigits);
            mgr.load(truePath, Texture.class, param);
        }
    }

    private Texture getTexture() {
        AssetManager mgr = Overlord.get().assetManager;
        int frameIndex = Animation.frameIndex() % this.frames;
        String longDigits = String.format("%05d", frameIndex);
        String truePath = this.path.replace("%", longDigits);
        Texture tex = mgr.get(truePath, Texture.class);
        if (tex == null) {
            throw new RuntimeException("Could not get texture " + name());
        }
        return tex;
    }

    public void draw(SpriteBatch batch, float x, float y, float scl) {
        Texture tex = getTexture();
        float scale_ = scl * scale;
        batch.draw(tex, x - (this.anchorX * scale_), y
                - (this.anchorY * scale_), tex.getWidth() * scale_,
                tex.getHeight() * scale_);
    }

    public void draw(SpriteBatch batch, float x, float y) {
        draw(batch, x, y, 1.0f);
    }

    public void drawFlipped(SpriteBatch batch, float x, float y, float scl) {
        Texture tex = getTexture();
        float scale_ = scl * scale;
        batch.draw(tex, x - (this.anchorX * scale_) + tex.getWidth() * scale_,
                y - (this.anchorY * scale_), -tex.getWidth() * scale_,
                tex.getHeight() * scale_);
    }

    public void drawFlipped(SpriteBatch batch, float x, float y) {
        drawFlipped(batch, x, y, 1.0f);
    }
}
