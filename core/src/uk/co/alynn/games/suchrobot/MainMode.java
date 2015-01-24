package uk.co.alynn.games.suchrobot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private List<Robot> robots = new ArrayList<Robot>();
    private Robot selectedRobot = null;
    private double dayCounter;

    private final static boolean DEBUG = false;
    private final Box box;
    private final Box initialBox;

    public MainMode(Box box) {
        this.box = box;
        initialBox = box.copy();
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

        int requiredRobots = box.robots;
        for (PathNode node : nodes) {
            if (node.type == NodeType.SPAWNER) {
                Robot robot = new Robot(nodes, node);
                robots.add(robot);
                requiredRobots -= 1;
                if (requiredRobots == 0)
                    break;
            }
        }
        if (requiredRobots > 0) {
            throw new RuntimeException("Not enough spawn pads.");
        }

        dayCounter = 0;
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public GameMode tick() {
        final float CURVE_A = 10.0f, CURVE_B = 8.0f, CURVE_C = 7.0f;

        batch.setProjectionMatrix(viewport.getCamera().combined);
        renderBG();
        float dt = Gdx.graphics.getDeltaTime();

        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(viewport.getCamera().combined);

        for (Robot robot : robots) {
            robot.update(dt);

            PathNode visited = robot.at();
            if (visited != null) {
                switch (visited.type) {
                case WELL:
                    if (robot.available()
                            && robot.accumulatedTimeAt > Constants.WELL_PUMP_TIME
                                    .asFloat() && visited.reserves != 0) {
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
                    if (robot.available()
                            && robot.accumulatedTimeAt > Constants.SALVAGE_TIME
                                    .asFloat() && visited.reserves != 0) {
                        robot.pickUp(CargoType.SALVAGE);
                        visited.reserves -= 1;
                    }
                    break;
                case MINE:
                    if (robot.available()
                            && robot.accumulatedTimeAt > Constants.METAL_MINE_TIME
                                    .asFloat() && visited.reserves != 0) {
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
        }
        sr.begin(ShapeType.Line);
        sr.setColor(Color.YELLOW);
        if (selectedRobot != null) {
            sr.curve(selectedRobot.x() - CURVE_A, selectedRobot.y(),
                    selectedRobot.x() - CURVE_B, selectedRobot.y() + CURVE_C,
                    selectedRobot.x() + CURVE_B, selectedRobot.y() + CURVE_C,
                    selectedRobot.x() + CURVE_A, selectedRobot.y(), 32);
        }
        sr.end();

        batch.begin();
        for (PathNode node : nodes) {
            Sprite spr = null;
            switch (node.type) {
            case WELL:
                if (node.reserves == 0) {
                    spr = Sprite.NODE_WELL_DEPLETED;
                } else {
                    spr = Sprite.NODE_WELL;
                }
                break;
            case MINE:
                if (node.reserves == 0) {
                    spr = Sprite.NODE_MINE_DEPLETED;
                } else {
                    spr = Sprite.NODE_MINE;
                }
                break;
            case BASE:
                spr = Sprite.NODE_SHIP;
                break;
            case WRECKAGE:
                if (node.reserves != 0) {
                    spr = Sprite.NODE_SALVAGE;
                }
                break;
            case WAYPOINT:
            case SPAWNER:
                if (DEBUG) {
                    spr = Sprite.NODE_DEBUG;
                }
                break;
            }
            if (spr != null) {
                spr.draw(batch, node.x, node.y);
            }
        }
        for (Robot robot : robots) {
            Sprite.ROBOT_IDLE.draw(batch, robot.x(), robot.y());
            final float ICON_OFFSET = 60.0f;
            switch (robot.carrying) {
            case NOTHING:
                break;
            case WATER:
                Sprite.ICON_WATER.draw(batch, robot.x(), robot.y()
                        + ICON_OFFSET);
                break;
            case METAL:
                Sprite.ICON_METAL.draw(batch, robot.x(), robot.y()
                        + ICON_OFFSET);
                break;
            case SALVAGE:
                Sprite.ICON_SALVAGE.draw(batch, robot.x(), robot.y()
                        + ICON_OFFSET);
                break;
            }
        }

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

        if (selectedRobot != null) {
            sr.setColor(Color.YELLOW);
            sr.curve(selectedRobot.x() - CURVE_A, selectedRobot.y(),
                    selectedRobot.x() - CURVE_B, selectedRobot.y() - CURVE_C,
                    selectedRobot.x() + CURVE_B, selectedRobot.y() - CURVE_C,
                    selectedRobot.x() + CURVE_A, selectedRobot.y(), 32);
        }
        sr.end();

        dayCounter += dt;

        if (dayCounter > Constants.DAY_LENGTH.asFloat()) {
            final float ROBOT_SAFE_DISTANCE = Constants.SAFE_ZONE_RADIUS
                    .asFloat();
            box.robots = 0;
            PathNode home = nodes.lookup("home");
            for (Robot robot : robots) {
                float distance = (float) Math.hypot(robot.x() - home.x,
                        robot.y() - home.y);
                if (distance <= ROBOT_SAFE_DISTANCE) {
                    box.robots += 1;
                }
            }

            if (box.isWin())
                return new VictoryMode();
            if (box.isLoss())
                return new GameOverMode();

            NightMode nm = new NightMode(box);
            List<String> messages = new ArrayList<String>();
            messages.add("Day " + box.day + " is over.");
            if (initialBox.robots > box.robots) {
                messages.add((initialBox.robots - box.robots)
                        + " robots didn't make it back.");
            }
            if (initialBox.salvage < box.salvage) {
                if (initialBox.salvage == 0) {
                    messages.add("Picked up the first parts of the ship today.");
                } else {
                    messages.add("Salvaged more parts from the ship.");
                }
            }
            if (initialBox.metal < box.metal) {
                messages.add("Picked up " + (box.metal - initialBox.metal)
                        + " metal.");
            }
            if (box.metal > 0) {
                messages.add(box.metal + " metal now.");
            }
            messages.add((10 - box.salvage) + " pieces of ship left to get.");
            if (box.water <= 1) {
                messages.add("Water is running very low.");
            } else {
                messages.add(box.water + " days supply of water left.");
            }
            switch (box.day) {
            case 1:
                messages.add("The CIA are on to me.");
                break;
            case 6:
                messages.add("Got to get off this rock.");
                break;
            case 9:
                messages.add("Not long now.");
                break;
            }
            return new ResultsScreen(messages, nm);
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
    public void rightClick(int mouseX, int mouseY) {
        if (selectedRobot == null)
            return;
        Vector2 worldCoords = viewport.unproject(new Vector2(mouseX, mouseY));

        PathNode nearestNode = null;
        float nearestDist = Float.POSITIVE_INFINITY;
        for (PathNode node : nodes) {
            if (node.type == NodeType.SPAWNER)
                continue;
            float dist = (float) Math.hypot(node.x - worldCoords.x, node.y
                    - worldCoords.y);
            if (dist <= nearestDist) {
                nearestDist = dist;
                nearestNode = node;
            }
        }
        selectedRobot.selectTarget(nearestNode);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        Vector2 worldCoords = viewport.unproject(new Vector2(mouseX, mouseY));

        final float ROBOT_SELECT_DISTANCE = 30.0f;

        selectedRobot = null;
        for (Robot robot : robots) {
            if (Math.hypot(robot.x() - worldCoords.x, robot.y() - worldCoords.y) < ROBOT_SELECT_DISTANCE) {
                selectedRobot = robot;
            }
        }
    }
}
