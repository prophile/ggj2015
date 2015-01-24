package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen implements GameMode {
    private ShapeRenderer renderer;
    private Viewport port;

    @Override
    public void start() {
        renderer = new ShapeRenderer();
        port = new ScreenViewport();
        port.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void stop() {
        renderer.dispose();
    }

    @Override
    public GameMode tick(ScreenEdge screenEdge) {
        AssetManager mgr = Overlord.get().assetManager;
        boolean complete = mgr.update(50);
        if (complete) {
            return new TitleScreen();
        }
        renderer.setProjectionMatrix(port.getCamera().combined);
        renderer.setColor(Color.WHITE);
        renderer.begin(ShapeType.Filled);
        renderer.arc(0.0f, 0.0f, Gdx.graphics.getHeight() * 0.02f, 90.0f,
                -360.0f * mgr.getProgress(),
                3 + (int) (180 * mgr.getProgress()));
        renderer.end();
        return this;
    }

    @Override
    public void resize(int width, int height) {
        port.update(width, height);
    }

    @Override
    public void click(int mouseX, int mouseY) {
    }

    @Override
    public void rightClick(int mouseX, int mouseY) {
    }

}
