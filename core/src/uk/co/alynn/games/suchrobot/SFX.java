package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public enum SFX {
    WIND("audio/ambience loops/wind loop ogg.ogg"), BRAINS(
            "audio/sfx/robot noises/robot brains.ogg"), THUNK(
            "audio/sfx/hits/metal hit 1.ogg");

    public String path;

    private SFX(String path) {
        this.path = path;
    }

    public void queueLoad() {
        AssetManager mgr = Overlord.get().assetManager;
        mgr.load(this.path, Sound.class);
    }

    public void play() {
        AssetManager mgr = Overlord.get().assetManager;
        Sound snd = mgr.get(this.path, Sound.class);
        if (snd == null) {
            throw new RuntimeException("Unloaded sound: " + name());
        }
        snd.play();
    }
}
