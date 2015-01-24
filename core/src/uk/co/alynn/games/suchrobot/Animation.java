package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;

public class Animation {
    public static final int FPS = 24;
    private static double baseTime;
    private static boolean startedFrame;
    
    public static int frameIndex() {
        return (int)(baseTime * FPS);
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
}
