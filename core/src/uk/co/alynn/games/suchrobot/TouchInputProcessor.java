package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.InputProcessor;

final class TouchInputProcessor implements InputProcessor {
    private final RobotGame robotGame;

    TouchInputProcessor(RobotGame robotGame) {
        this.robotGame = robotGame;
    }

    private int oldX, oldY;

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer,
            int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (robotGame.mode == null)
            return false;
        if (pointer != 0)
            return false;
        int dx = screenX - oldX;
        int dy = screenY - oldY;
        oldX = screenX;
        oldY = screenY;
        robotGame.mode.drag(dx, -dy);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer,
            int button) {
        if (robotGame.mode == null)
            return false;
        if (button == 0)
            robotGame.mode.click(screenX, screenY);
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
}