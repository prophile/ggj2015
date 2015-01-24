package uk.co.alynn.games.suchrobot;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMode implements GameMode {
    public static final float WORLD_WIDTH = 1024.0f;
    public static final float WORLD_HEIGHT = 640.0f;
    private SpriteBatch batch = null;
    private Viewport viewport = null;
    private NodeSet nodes = null;

    @Override
    public void start() {
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        batch = new SpriteBatch();

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        try {
            nodes = NodeSetReader.readNodeSet("nodes.txt");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read nodes", e);
        }
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public void draw() {
        Texture img = Sprite.NODE_DEBUG.getTexture();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        for (PathNode node : nodes) {
            batch.draw(img, node.x.toFloat(), node.y.toFloat());
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

}
