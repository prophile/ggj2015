package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class Overlord {
    private static Overlord s_instance = null;

    public final AssetManager assetManager;

    private ShaderProgram fontShader;

    private Overlord() {
        assetManager = new AssetManager();
        fontShader = new ShaderProgram(Gdx.files.internal("text.vert"),
                Gdx.files.internal("text.frag"));
    }

    private void configure() {
        initSprites();
        initFont();
        initBGImages();
        assetManager.finishLoading(); // TODO: improve me
    }

    private static void initFont() {
        s_instance.assetManager.load("bitstream.fnt", BitmapFont.class);
    }

    private static void initBGImages() {
        AssetManager mgr = s_instance.assetManager;
        TextureParameter param = new TextureParameter();
        param.genMipMaps = true;
        param.minFilter = TextureFilter.MipMapLinearLinear;
        mgr.load("Layout/Rough/Level1.png", Texture.class, param);
        mgr.load("Layout/Rough/Level2.png", Texture.class, param);
        mgr.load("Layout/Rough/Level3.png", Texture.class, param);
        mgr.load("Layout/Rough/Decorativemountains.png", Texture.class, param);
        mgr.load("Layout/Rough/Sky.png", Texture.class, param);
        mgr.load("UI/NightUIRough/NextDayUI.png", Texture.class, param);
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

    public ShaderProgram getFontShader() {
        return fontShader;
    }
}
