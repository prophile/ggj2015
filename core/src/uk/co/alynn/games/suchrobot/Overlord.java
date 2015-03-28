package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
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
        initSFX();
    }

    private static void initSFX() {
        for (SFX sfx : SFX.values()) {
            sfx.queueLoad();
        }
    }

    private static void initFont() {
        BitmapFontParameter param = new BitmapFontParameter();
        param.genMipMaps = true;
        param.magFilter = TextureFilter.Linear;
        param.minFilter = TextureFilter.MipMapLinearLinear;
        s_instance.assetManager.load("bitstream.fnt", BitmapFont.class, param);
    }

    private static void initBGImages() {
        AssetManager mgr = s_instance.assetManager;
        TextureParameter param = new TextureParameter();
        param.genMipMaps = true;
        param.minFilter = TextureFilter.MipMapLinearLinear;

        mgr.load("Layout/LayoutPanes/Filter.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/FrontObjects.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Nightcover.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Parallaxfront.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Parallaxrear1.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Parallaxrear2.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Robotplane.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Roughpaths.png", Texture.class, param);
        mgr.load("Layout/LayoutPanes/Sky.png", Texture.class, param);

        mgr.load("UI/NightUIRough/NextDayUI.png", Texture.class, param);
        mgr.load("UI/NightUIRough/NextDayUIMO.png", Texture.class, param);
        mgr.load("UI/NightUIRough/Addrobotbutton.png", Texture.class, param);
        mgr.load("UI/NightUIRough/Inactive robot.png", Texture.class, param);
        mgr.load("UI/NightUIRough/Robotmenubase.png", Texture.class, param);
        mgr.load("UI/NightUIRough/Level up button.png", Texture.class, param);
        mgr.load("UI/NightUIRough/Inactive robotMO.png", Texture.class, param);
        mgr.load("UI/NightUIRough/RoboBGlvl1.png", Texture.class, param);
        mgr.load("UI/NightUIRough/RoboBGlvl2.png", Texture.class, param);
        mgr.load("UI/NightUIRough/RoboBGlvl3.png", Texture.class, param);
        mgr.load("UI/NightUIRough/RoboMOlvl1.png", Texture.class, param);
        mgr.load("UI/NightUIRough/RoboMOlvl2.png", Texture.class, param);
        mgr.load("UI/NightUIRough/RoboMOlvl3.png", Texture.class, param);
        mgr.load("UI/NightUIRough/Level up button.png", Texture.class, param);
        mgr.load("UI/Titles.png", Texture.class, param);
    }

    private static void initSprites() {
        Sprite.queueAtlasLoad();
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
