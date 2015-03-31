package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.InputProcessor;

final class TouchInputProcessor implements InputProcessor {
    private final RobotGame robotGame;

    TouchInputProcessor(RobotGame robotGame) {
        this.robotGame = robotGame;
    }

    private int oldX, oldY;
    private int touchStartX, touchStartY;

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer,
            int button) {
        if (pointer != 0 || button != 0)
            return false;
        oldX = screenX;
        oldY = screenY;
        if (robotGame.mode == null)
            return false;
        int squareDistance = (screenX - touchStartX)*(screenX - touchStartX) + (screenY - touchStartY)*(screenY - touchStartY);
        if (squareDistance > 30*30) {
            return false;
        }
        robotGame.mode.click(screenX, screenY);
        return true;
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
        if (pointer != 0 || button != 0)
            return false;
        oldX = screenX;
        touchStartX = screenX;
        oldY = screenY;
        touchStartY = screenY;
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