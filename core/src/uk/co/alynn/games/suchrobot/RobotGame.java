package uk.co.alynn.games.suchrobot;

import java.util.Locale;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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

    private static void loadConstants() {
        // load constants file
        FileHandle handle = Gdx.files.external("constants.txt");
        if (handle.exists()) {
            System.err.println("USING OVERLOADED CONSTANTS");
        } else {
            handle = Gdx.files.internal("constants.txt");
        }
        Scanner scn = new Scanner(handle.read());
        Constants.loadConstants(scn.useLocale(Locale.UK));
        scn.close();
    }

    @Override
    public void create() {
        loadConstants();
        Overlord.init();
        setMode(new LoadingScreen());
        Gdx.input.setInputProcessor(new InputProcessor() {
            private int oldX, oldY;

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer,
                    int button) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (mode == null)
                    return false;
                if (pointer != 0)
                    return false;
                int dx = screenX - oldX;
                int dy = screenY - oldY;
                oldX = screenX;
                oldY = screenY;
                mode.drag(dx, -dy);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer,
                    int button) {
                if (mode == null)
                    return false;
                if (button == 0)
                    mode.click(screenX, screenY);
                else
                    return false;
                return true;
            }

            @Override
            public boolean scrolled(int amount) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                oldX = screenX;
                oldY = screenY;
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
        SFX.updateLoops();
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        if (mode != null) {
            mode.resize(screenWidth, screenHeight);
        }
    }
}
