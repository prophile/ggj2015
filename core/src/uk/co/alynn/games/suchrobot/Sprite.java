package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public enum Sprite {
    ROBOT_DEBUG("robot.png", 1, 1.0f, 16, 16), NODE_DEBUG("node.png", 1, 1.0f,
            16, 16), ROBOT_IDLE(
            "Animations/RoboidleAlpha/RoboIdle/RoboIdle_00001_%.png", 24, 0.1f,
            512, 380), ROBOT_WALK("Animations/Robowalk/RoboWalk_%.png", 24,
            0.1f, 512, 380), NODE_MINE("NodesRough/MetalRough.png", 1, 0.1f,
            425, 150), NODE_SALVAGE("NodesRough/SalvageRough.png", 1, 0.1f,
            500, 354), NODE_SHIP("NodesRough/ShipRought.png", 1, 0.1f, 710, 522), NODE_WELL(
            "NodesRough/WellRough.png", 1, 0.1f, 441, 120), NODE_WELL_DEPLETED(
            "NodesRough/WellBrokenRough.png", 1, 0.1f, 441, 120), NODE_MINE_DEPLETED(
            "NodesRough/MetalEmptyrough.png", 1, 0.1f, 425, 150), ICON_WATER(
            "UI/CarryIcons/Water.png", 1, 0.035f, 1417 / 2, 1276 / 2), ICON_METAL(
            "UI/CarryIcons/Metal.png", 1, 0.035f, 1417 / 2, 1276 / 2), ICON_SALVAGE(
            "UI/CarryIcons/Salvage.png", 1, 0.035f, 1417 / 2, 1276 / 2);

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

    public void draw(SpriteBatch batch, float x, float y) {
        Texture tex = getTexture();
        batch.draw(tex, x - (this.anchorX * scale), y - (this.anchorY * scale),
                tex.getWidth() * scale, tex.getHeight() * scale);
    }

    public void drawFlipped(SpriteBatch batch, float x, float y) {
        Texture tex = getTexture();
        batch.draw(tex, x - (this.anchorX * scale) + tex.getWidth() * scale, y
                - (this.anchorY * scale), -tex.getWidth() * scale,
                tex.getHeight() * scale);
    }
}
