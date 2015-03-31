package uk.co.alynn.games.suchrobot;

import java.util.Set;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public enum SFX {
    SELECT_ROBOT("sfx/menu sounds/click 3"),
    SELECT_TARGET("sfx/menu sounds/new new robo attention"),
    PURCHASE_NEW("sfx/roboto upgrade/electric saw spraying hammer"),
    PURCHASE_UPGRADE("sfx/roboto upgrade/robo upgrade tada"),
    WIN("sfx/lift off softer"),
    LOSE("sfx/new alarm"),
    MUSIC("music tracks/main track 70 seconds"),
    METAL_MINE("sfx/hits/eargty hit 2"),
    SALVAGE("sfx/hits/metal hit 1"),
    WATER_PUMP("sfx/hits/water loop"),
    QUICKSAND("sfx/robot noises/robot in distress 2"),
    ENGINES("sfx/engine/new engine/full rev"),
    NIGHT_AMB("ambience loops/wind loop ogg");

    private static Set<Sound> previousLooping = new ArraySet<Sound>(32);
    private static Set<Sound> currentLooping = new ArraySet<Sound>(32);

    private final static boolean SORRY_CHARLIE = true;

    public String path;

    private SFX(String path) {
        this.path = "audio/" + path + ".ogg";
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
