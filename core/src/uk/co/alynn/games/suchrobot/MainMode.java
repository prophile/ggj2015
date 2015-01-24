package uk.co.alynn.games.suchrobot;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMode implements GameMode {
    public static final float WORLD_WIDTH = 1024.0f;
    public static final float WORLD_HEIGHT = 640.0f;
    private SpriteBatch batch = null;
    private Viewport viewport = null;
    private NodeSet nodes = null;
    private Robot robot = null;
    private int waterBase = 1;
    private int metalBase = 0;
    private int salvageBase = 0;

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

        robot = new Robot(nodes);
        robot.selectTarget(nodes.lookup("well_far"));
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public void draw() {
        robot.update(Gdx.graphics.getDeltaTime());

        PathNode visited = robot.at();
        if (visited != null) {
            switch (visited.type) {
            case WELL:
                if (robot.available() && robot.accumulatedTimeAt > 2.0
                        && visited.reserves != 0) {
                    robot.pickUp(CargoType.WATER);
                    if (visited.reserves > 0) {
                        visited.reserves -= 1;
                    }
                }
                break;
            case BASE:
                if (robot.offload(CargoType.WATER)) {
                    waterBase += 1;
                } else if (robot.offload(CargoType.METAL)) {
                    metalBase += 1;
                } else if (robot.offload(CargoType.SALVAGE)) {
                    salvageBase += 1;
                }
                break;
            case WRECKAGE:
                if (robot.available() && robot.accumulatedTimeAt > 5.0
                        && visited.reserves != 0) {
                    robot.pickUp(CargoType.SALVAGE);
                    visited.reserves -= 1;
                }
                break;
            case MINE:
                if (robot.available() && robot.accumulatedTimeAt > 3.5
                        && visited.reserves != 0) {
                    robot.pickUp(CargoType.METAL);
                    if (visited.reserves > 0) {
                        visited.reserves -= 1;
                    }
                }
                break;
            default:
                break;
            }
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        for (PathNode node : nodes) {
            Sprite.NODE_DEBUG.draw(batch, node.x, node.y);
        }
        Sprite.ROBOT_IDLE.draw(batch, robot.x(), robot.y());

        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                BitmapFont.class);
        batch.setShader(Overlord.get().getFontShader());
        fnt.drawMultiLine(batch, "Water: " + waterBase + "\nMetal: "
                + metalBase + "\nSalvg: " + salvageBase, 100, 100);
        batch.setShader(null);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        Vector2 worldCoords = viewport.unproject(new Vector2(mouseX, mouseY));

        PathNode nearestNode = null;
        float nearestDist = Float.POSITIVE_INFINITY;
        for (PathNode node : nodes) {
            float dist = (float) Math.hypot(node.x - worldCoords.x, node.y
                    - worldCoords.y);
            if (dist <= nearestDist) {
                nearestDist = dist;
                nearestNode = node;
            }
        }
        robot.selectTarget(nearestNode);
    }

}
