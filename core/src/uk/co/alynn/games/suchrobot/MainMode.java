package uk.co.alynn.games.suchrobot;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
    private double dayCounter;

    private final boolean DEBUG = false;
    private Box box;

    public MainMode(Box box) {
        this.box = box;
    }

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

        dayCounter = 0;
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public GameMode tick() {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        renderBG();
        float dt = Gdx.graphics.getDeltaTime();
        robot.update(dt);

        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(viewport.getCamera().combined);

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
                    box.water += 1;
                } else if (robot.offload(CargoType.METAL)) {
                    box.metal += 1;
                } else if (robot.offload(CargoType.SALVAGE)) {
                    box.salvage += 1;
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

        sr.begin(ShapeType.Line);
        sr.setColor(Color.YELLOW);
        final float CURVE_A = 10.0f, CURVE_B = 8.0f, CURVE_C = 7.0f;
        sr.curve(robot.x() - CURVE_A, robot.y(), robot.x() - CURVE_B, robot.y()
                + CURVE_C, robot.x() + CURVE_B, robot.y() + CURVE_C, robot.x()
                + CURVE_A, robot.y(), 32);
        sr.end();

        batch.begin();
        for (PathNode node : nodes) {
            Sprite spr = null;
            switch (node.type) {
            case WELL:
                spr = Sprite.NODE_WELL;
                break;
            case MINE:
                spr = Sprite.NODE_MINE;
                break;
            case BASE:
                spr = Sprite.NODE_SHIP;
                break;
            case WRECKAGE:
                spr = Sprite.NODE_SALVAGE;
                break;
            case WAYPOINT:
                if (DEBUG) {
                    spr = Sprite.NODE_DEBUG;
                }
                break;
            }
            if (spr != null) {
                spr.draw(batch, node.x, node.y);
            }
        }
        Sprite.ROBOT_IDLE.draw(batch, robot.x(), robot.y());

        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                BitmapFont.class);
        batch.setShader(Overlord.get().getFontShader());
        fnt.drawMultiLine(batch, "Water: " + box.water + "\nMetal: "
                + box.metal + "\nSalvg: " + box.salvage + "\nTime: "
                + (int) dayCounter + "\nDay: " + box.day, 100, 300);
        batch.setShader(null);

        batch.end();

        sr.setColor(Color.CYAN);
        sr.begin(ShapeType.Line);
        if (DEBUG) {
            for (PathNode node : nodes) {
                for (PathNode node2 : nodes.connectionsFrom(node)) {
                    if (node2.hashCode() > node.hashCode()) {
                        sr.line(node2.x, node2.y, node.x, node.y);
                    }
                }
            }
        }
        sr.setColor(Color.YELLOW);
        sr.curve(robot.x() - CURVE_A, robot.y(), robot.x() - CURVE_B, robot.y()
                - CURVE_C, robot.x() + CURVE_B, robot.y() - CURVE_C, robot.x()
                + CURVE_A, robot.y(), 32);
        sr.end();

        dayCounter += dt;

        if (dayCounter > 60) {
            return new NightMode(box);
        } else {
            return this;
        }
    }

    private void renderBG() {
        Texture l1 = Overlord.get().assetManager.get("Layout/Rough/Level1.png",
                Texture.class);
        Texture l2 = Overlord.get().assetManager.get("Layout/Rough/Level2.png",
                Texture.class);
        Texture l3 = Overlord.get().assetManager.get("Layout/Rough/Level3.png",
                Texture.class);
        Texture mnt = Overlord.get().assetManager.get(
                "Layout/Rough/Decorativemountains.png", Texture.class);
        Texture sky = Overlord.get().assetManager.get("Layout/Rough/Sky.png",
                Texture.class);
        batch.begin();
        batch.draw(sky, -WORLD_WIDTH / 2, -WORLD_HEIGHT / 2, WORLD_WIDTH,
                WORLD_HEIGHT);
        batch.draw(mnt, -WORLD_WIDTH / 2, -WORLD_HEIGHT / 2, WORLD_WIDTH,
                WORLD_HEIGHT);
        batch.draw(l3, -WORLD_WIDTH / 2, -WORLD_HEIGHT / 2, WORLD_WIDTH,
                WORLD_HEIGHT);
        batch.draw(l2, -WORLD_WIDTH / 2, -WORLD_HEIGHT / 2, WORLD_WIDTH,
                WORLD_HEIGHT);
        batch.draw(l1, -WORLD_WIDTH / 2, -WORLD_HEIGHT / 2, WORLD_WIDTH,
                WORLD_HEIGHT);
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
