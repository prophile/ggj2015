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
    private Robot robot = null;
    private static final float dt = 1 / 30.0f;

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
        
        robot = new Robot();
        robot.sourceNode = nodes.lookup("home");
        robot.destNode = nodes.lookup("well_near");
        robot.progress = 0;
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public void draw() {
        robot.update(dt);
        
        Texture debugNode = Sprite.NODE_DEBUG.getTexture();
        Texture debugRobot = Sprite.ROBOT_DEBUG.getTexture();
        
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        for (PathNode node : nodes) {
            batch.draw(debugNode, node.x, node.y);
        }
        batch.draw(debugRobot, robot.x(), robot.y());
        
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

}
