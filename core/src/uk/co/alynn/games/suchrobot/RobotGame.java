package uk.co.alynn.games.suchrobot;

import java.util.Locale;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
                if (mode == null)
                    return false;
                if (button == 0)
                    mode.click(screenX, screenY);
                else if (button == 1)
                    mode.rightClick(screenX, screenY);
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
        int mx = Gdx.input.getX();
        int my = Gdx.input.getY();
        int px = 0, py = 0;
        int margin = Constants.SCREEN_EDGE_WIDTH.asInt();
        if (mx < margin) {
            px = -1;
        } else if (mx > (Gdx.graphics.getWidth() - margin)) {
            px = 1;
        }
        if (my < margin) {
            py = 1;
        } else if (my > (Gdx.graphics.getHeight() - margin)) {
            py = -1;
        }
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
            py += 1;
        }
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
            py += -1;
        }
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            px += -1;
        }
        if (Gdx.input.isKeyPressed(Keys.D)
                || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            px += 1;
        }
        ScreenEdge edge = ScreenEdge.getEdge(px, py);
        if (mode != null) {
            GameMode nextMode = mode.tick(edge);
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
