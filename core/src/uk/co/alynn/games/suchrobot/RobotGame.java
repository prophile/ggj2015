package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
        Gdx.input.setInputProcessor(new InputProcessor() {

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer,
                    int button) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer,
                    int button) {
                if (button != 0)
                    return false;
                if (mode == null)
                    return false;
                mode.click(screenX, screenY);
                return true;
            }

            @Override
            public boolean scrolled(int amount) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                // TODO Auto-generated method stub
                return false;
            }
        });
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
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        if (mode != null) {
            mode.resize(screenWidth, screenHeight);
        }
    }
}
