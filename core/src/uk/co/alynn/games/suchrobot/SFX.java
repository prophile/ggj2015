package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public enum SFX {
    SELECT_ROBOT("sfx/menu sounds/click 3.ogg"), SELECT_TARGET(
            "sfx/menu sounds/new new robo attention.ogg"), PURCHASE_NEW(
            "sfx/roboto upgrade/electric saw spraying hammer.ogg"), PURCHASE_UPGRADE(
            "sfx/roboto upgrade/robo upgrade tada.ogg"), WIN(
            "sfx/lift off softer.ogg"), LOSE("sfx/new alarm.ogg"), MUSIC(
            "music tracks/main track 70 seconds.ogg");

    public String path;

    private SFX(String path) {
        this.path = "audio/" + path;
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
