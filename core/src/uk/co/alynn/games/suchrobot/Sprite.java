package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum Sprite {
    ROBOT_DEBUG("robot-debug", 1, 1.0f, 16, 16),
    NODE_DEBUG("node-debug", 1, 1.0f, 16, 16),
    ROBOT_IDLE("animations-robot-idle-%", 24, 0.5f, 102, 76),
    ROBOT_WALK("animations-robot-walk-%", 24, 0.5f, 102, 76),
    ROBOT_EAT_DIRT("animations-robot-eat-dirt-%", 24, 0.5f, 102, 76),
    ROBOT_EAT_WATER("animations-robot-eat-water-%", 24, 0.5f, 102, 76),
    ROBOT_FLAIL_START("animations-robot-flail-start-%", 24, 0.5f, 102, 76),
    ROBOT_FLAIL("animations-robot-flail-%", 24, 0.5f, 102, 76),
    ROBOT_FLAIL_END("animations-robot-flail-end-%", 24, 0.5f, 102, 76),
    NODE_MINE("nodesrough-node-mine", 1, 0.5f, 85, 30),
    NODE_SALVAGE("nodesrough-node-salvage", 1, 0.5f, 100, 70),
    NODE_SHIP("nodesrough-node-ship", 1, 0.5f, 142, 104),
    NODE_WELL("nodesrough-node-well", 1, 0.5f, 88, 24),
    NODE_WELL_DEPLETED("nodesrough-node-well-depleted", 1, 0.5f, 88, 24),
    NODE_MINE_DEPLETED("nodesrough-node-mine-depleted", 1, 0.5f, 85, 30),
    ICON_WATER("ui-icon-water", 1, 0.5f, 49, 44),
    ICON_METAL("ui-icon-metal", 1, 0.5f, 49, 44),
    ICON_SALVAGE("ui-icon-salvage", 1, 0.5f, 49, 44),
    ICON_OFFSCREEN_FLAIL("ui-icon-offscreen-flail", 1, 0.5f, 49, 44),
    ICON_OFFSCREEN_IDLE("ui-icon-offscreen-idle", 1, 0.5f, 49, 44),
    ICON_OFFSCREEN_WALK("ui-icon-offscreen-walk", 1, 0.5f, 49, 44),
    CHEVRONS_L1("ui-chevrons-l1", 1, 0.5f, 28, 16),
    CHEVRONS_L2("ui-chevrons-l2", 1, 0.5f, 28, 16),
    CHEVRONS_L3("ui-chevrons-l3", 1, 0.5f, 28, 16);

    private static final String ATLAS = "sprites.atlas";
    private static TextureAtlas s_atlas = null;

    private String path;
    private float scale;
    private int anchorX, anchorY;
    private int frames;

    private TextureAtlas.AtlasRegion[] regions = null;

    private Sprite(String path, int frameCount, float scale, int anchorX,
            int anchorY) {
        this.path = path;
        frames = frameCount;
        this.scale = scale;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    public static void queueAtlasLoad() {
        AssetManager mgr = Overlord.get().assetManager;
        mgr.load(ATLAS, TextureAtlas.class);
    }

    private static TextureAtlas getAtlas() {
        if (s_atlas != null) {
            return s_atlas;
        }
        AssetManager mgr = Overlord.get().assetManager;
        s_atlas = mgr.get(ATLAS, TextureAtlas.class);
        if (s_atlas == null) {
            throw new RuntimeException("Could not get texture atlas");
        }
        return s_atlas;
    }

    private TextureAtlas.AtlasRegion getRegion() {
        if (regions == null) {
            regions = new TextureAtlas.AtlasRegion[frames];
            TextureAtlas atlas = getAtlas();
            for (int frame = 0; frame < frames; ++frame) {
                String longDigits = String.format("%05d", frame);
                String truePath = path.replace("%", longDigits);
                TextureAtlas.AtlasRegion rgn = atlas.findRegion(truePath);
                if (rgn == null) {
                    throw new RuntimeException("Frame " + frame + " missing on sprite " + name());
                }
                regions[frame] = rgn;
            }
        }
        int frameIndex = Animation.frameIndex() % frames;
        return regions[frameIndex];
    }

    private void drawDispatch(SpriteBatch batch, float x, float y, float scl, boolean flipX) {
        TextureAtlas.AtlasRegion tex = getRegion();
        float scale_ = scl * scale;
        batch.draw(tex,
                   x - anchorX*scale_ + tex.offsetX*scale_ + (flipX ? scale_*tex.packedWidth : 0.0f),
                   y - anchorY*scale_ + tex.offsetY*scale_,
                   0.0f,
                   0.0f,
                   tex.packedWidth,
                   tex.packedHeight,
                   scale_ * (flipX ? -1.0f : 1.0f),
                   scale_,
                   0.0f);
        batch.begin();
    }

    public void draw(SpriteBatch batch, float x, float y, float scl) {
        drawDispatch(batch, x, y, scl, false);
    }

    public void draw(SpriteBatch batch, float x, float y) {
        draw(batch, x, y, 1.0f);
    }

    public void drawFlipped(SpriteBatch batch, float x, float y, float scl) {
        drawDispatch(batch, x, y, scl, true);
    }

    public void drawFlipped(SpriteBatch batch, float x, float y) {
        drawFlipped(batch, x, y, 1.0f);
    }
}
