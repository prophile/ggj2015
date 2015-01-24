package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RobotGame extends ApplicationAdapter {
    GameMode mode;

    public void setMode(GameMode newMode) {
        if (mode != null) {
            mode.stop();
        }
        mode = newMode;
        if (mode != null) {
            mode.start();
        }
    }

    @Override
    public void create () {
        Overlord.init();
        setMode(new MainMode());
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (mode != null) {
            mode.draw();
        }
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        if (mode != null) {
            mode.resize(screenWidth, screenHeight);
        }
    }
}
