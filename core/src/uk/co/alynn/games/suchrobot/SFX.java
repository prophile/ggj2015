package uk.co.alynn.games.suchrobot;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public enum SFX {
    SELECT_ROBOT("sfx/menu sounds/click 3.ogg"), SELECT_TARGET(
            "sfx/menu sounds/new new robo attention.ogg"), PURCHASE_NEW(
            "sfx/roboto upgrade/electric saw spraying hammer.ogg"), PURCHASE_UPGRADE(
            "sfx/roboto upgrade/robo upgrade tada.ogg"), WIN(
            "sfx/lift off softer.ogg"), LOSE("sfx/new alarm.ogg"), MUSIC(
            "music tracks/main track 70 seconds.ogg");

    private static Set<Sound> previousLooping = new HashSet<Sound>();
    private static Set<Sound> currentLooping = new HashSet<Sound>();

    public String path;

    private SFX(String path) {
        this.path = "audio/" + path;
    }

    public void queueLoad() {
        AssetManager mgr = Overlord.get().assetManager;
        mgr.load(this.path, Sound.class);
    }

    public void play() {
        Sound snd = getSound();
        snd.play();
    }

    private Sound getSound() {
        AssetManager mgr = Overlord.get().assetManager;
        Sound snd = mgr.get(this.path, Sound.class);
        if (snd == null) {
            throw new RuntimeException("Unloaded sound: " + name());
        }
        return snd;
    }

    public void loop(float pan) {
        Sound snd = getSound();
        currentLooping.add(snd);
    }

    public static void updateLoops() {
        for (Sound snd : currentLooping) {
            if (!(previousLooping.contains(snd))) {
                snd.loop();
            }
        }
        for (Sound snd : previousLooping) {
            if (!(currentLooping.contains(snd))) {
                snd.stop();
            }
        }
        Set<Sound> tmp = previousLooping;
        previousLooping = currentLooping;
        tmp.clear();
        currentLooping = tmp;
    }
}
