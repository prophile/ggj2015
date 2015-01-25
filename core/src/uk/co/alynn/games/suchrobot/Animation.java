package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;

public class Animation {
    public static final int FPS = 24;
    private static double baseTime;
    private static boolean startedFrame;
    private static float phase = 0.0f;
    private static boolean phasing = false;

    public static int frameIndex() {
        if (phasing) {
            return (int) (phase * FPS);
        } else {
            return (int) (baseTime * FPS);
        }
    }

    public static void update() {
        int prevIndex = frameIndex();
        baseTime += Gdx.graphics.getDeltaTime();
        int newIndex = frameIndex();
        startedFrame = newIndex > prevIndex;
    }

    public static boolean startedFrame() {
        return startedFrame;
    }

    public static void beginPhase(float timeSinceStart) {
        phase = timeSinceStart;
        phasing = true;
    }

    public static void endPhase() {
        phasing = false;
    }
}
