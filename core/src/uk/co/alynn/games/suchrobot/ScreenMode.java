package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class ScreenMode implements GameMode {
    abstract String screenPath();

    abstract GameMode advance(int x, int y);

    private Viewport viewport;
    private Texture tex;
    private SpriteBatch batch;

    private GameMode next = null;

    @Override
    public void start() {
        batch = new SpriteBatch();
        tex = Overlord.get().assetManager.get(screenPath(), Texture.class);
        viewport = new FitViewport(tex.getWidth(), tex.getHeight());
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public GameMode tick() {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(tex, -(tex.getWidth() / 2), -(tex.getHeight() / 2));
        batch.end();
        if (next != null)
            return next;
        return this;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        Vector2 worldCoords = viewport.unproject(new Vector2(mouseX, mouseY));
        if (worldCoords.x < 0 || worldCoords.y < 0)
            return;
        if (worldCoords.x >= tex.getWidth() || worldCoords.y >= tex.getHeight())
            return;
        next = advance((int) worldCoords.x, (int) worldCoords.y);
    }

}
