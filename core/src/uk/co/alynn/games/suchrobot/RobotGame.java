package uk.co.alynn.games.suchrobot;

import java.util.Locale;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;

public class RobotGame extends ApplicationAdapter {
    GameMode mode;

    public void setMode(GameMode newMode) {
        if (newMode == mode)
            return;
        System.err.println("STATE CHANGE");
        if (mode != null) {
            mode.stop();
            System.err.println("  From: " + mode.getClass().getName());
        }
        mode = newMode;
        if (mode != null) {
            mode.start();
            System.err.println("    To: " + mode.getClass().getName());
        }
    }

    @Override
    public void create() {
        Overlord.init();
        setMode(new LoadingScreen());
        Gdx.input.setInputProcessor(new TouchInputProcessor(this));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Animation.update();
        if (mode != null) {
            GameMode nextMode = mode.tick();
            setMode(nextMode);
        }
        SFX.updateLoops();
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        if (mode != null) {
            mode.resize(screenWidth, screenHeight);
        }
    }
}
