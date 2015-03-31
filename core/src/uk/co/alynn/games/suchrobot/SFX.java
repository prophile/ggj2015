package uk.co.alynn.games.suchrobot;

import java.util.Set;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;

public enum SFX {
    SELECT_ROBOT("intf-sel"),
    SELECT_TARGET("intf-send"),
    PURCHASE_NEW("intf-purchase"),
    PURCHASE_UPGRADE("intf-upgrade"),
    WIN("victory"),
    LOSE("game-over"),
    MUSIC("music"),
    METAL_MINE("hit-mine"),
    SALVAGE("hit-salvage"),
    WATER_PUMP("hit-well"),
    QUICKSAND("quicksand"),
    ENGINES("engine"),
    NIGHT_AMB("night-wind");

    private static Set<Sound> previousLooping = new ArraySet<Sound>(32);
    private static Set<Sound> currentLooping = new ArraySet<Sound>(32);

    private final static boolean SORRY_CHARLIE = true;

    public String path;

    private static String extension() {
        switch (Gdx.app.getType()) {
        case iOS:
            return ".aifc";
        case Desktop:
            return ".ogg";
        default:
            return ".wav";
        }
    }

    private SFX(String path) {
        this.path = "audio/" + path + extension();
    }

    public void queueLoad() {
        AssetManager mgr = Overlord.get().assetManager;
        mgr.load(path, Sound.class);
    }

    public void play() {
        Sound snd = getSound();
        if (name().equals("MUSIC") && SORRY_CHARLIE) {
            return;
        }
        snd.play();
    }

    private Sound getSound() {
        AssetManager mgr = Overlord.get().assetManager;
        Sound snd = mgr.get(path, Sound.class);
        if (snd == null) {
            throw new RuntimeException("Unloaded sound: " + name());
        }
        return snd;
    }

    public void loop() {
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
