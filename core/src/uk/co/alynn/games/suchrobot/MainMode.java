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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMode implements GameMode {
    private float THE_SCALE = Constants.SCALE.asFloat();
    public static final float WORLD_WIDTH = 1024.0f;
    public static final float WORLD_HEIGHT = 640.0f;
    private SpriteBatch batch = null;
    private Viewport viewport = null;
    private static NodeSet nodes = null;
    private List<Robot> robots = new ArrayList<Robot>();
    private Robot selectedRobot = null;
    private double dayCounter;

    private final boolean DEBUG = Constants.DEBUG_NODES.asBoolean();
    private final Box box;
    private final Box initialBox;

    private float offX, offY;

    public static void init() {
        try {
            nodes = NodeSetReader.readNodeSet("nodes.txt");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read nodes", e);
        }
    }

    public MainMode(Box box) {
        this.box = box;
        initialBox = box.copy();
    }

    @Override
    public void start() {
        viewport = new ExtendViewport(WORLD_WIDTH / THE_SCALE, WORLD_HEIGHT
                / THE_SCALE);
        batch = new SpriteBatch();

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (nodes == null) {
            throw new RuntimeException("nodes not initted");
        }

        int requiredRobotIndex = 0;
        for (PathNode node : nodes) {
            while (requiredRobotIndex < box.robots.length
                    && box.robots[requiredRobotIndex] == RobotClass.RINGO) {
                ++requiredRobotIndex;
            }
            if (requiredRobotIndex >= box.robots.length)
                break;
            if (node.type == NodeType.SPAWNER) {
                Robot robot = new Robot(box.robots[requiredRobotIndex], nodes,
                        node);
                robots.add(robot);
                ++requiredRobotIndex;
            }
        }

        dayCounter = 0;
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public GameMode tick(ScreenEdge screenEdge) {
        final float CURVE_A = 10.0f, CURVE_B = 8.0f, CURVE_C = 7.0f;
        float dt = Gdx.graphics.getDeltaTime();

        pan(screenEdge, dt);

        batch.setProjectionMatrix(new Matrix4());
        renderBG();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        Texture l1 = Overlord.get().assetManager.get(
                "Layout/LayoutPanes/Robotplane.png", Texture.class);
        batch.begin();
        float worldPlaneWidth = l1.getWidth() * 0.5f;
        float worldPlaneHeight = l1.getHeight() * 0.5f;
        batch.draw(l1, -worldPlaneWidth / 2, -worldPlaneHeight / 2,
                worldPlaneWidth, worldPlaneHeight);
        batch.end();

        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(viewport.getCamera().combined);

        for (Robot robot : robots) {
            robot.update(dt);

            PathNode visited = robot.at();
            if (visited != null) {
                switch (visited.type) {
                case WELL:
                    if (robot.available()
                            && robot.gatheredFor(Constants.WELL_PUMP_TIME
                                    .asFloat()) && visited.reserves != 0) {
                        robot.pickUp(CargoType.WATER);
                        if (visited.reserves > 0) {
                            visited.reserves -= 1;
                        }
                    }
                    break;
                case SPAWNER:
                    if (visited != robot.spawnNode) {
                        break;
                    }
                    // fall-through
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
                            && robot.gatheredFor(Constants.SALVAGE_TIME
                                    .asFloat()) && visited.reserves != 0) {
                        robot.pickUp(CargoType.SALVAGE);
                        visited.reserves -= 1;
                    }
                    break;
                case MINE:
                    if (robot.available()
                            && robot.gatheredFor(Constants.METAL_MINE_TIME
                                    .asFloat()) && visited.reserves != 0) {
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
            case WRECKAGE:
                if (node.reserves != 0) {
                    spr = Sprite.NODE_SALVAGE;
                }
                break;
            case BASE:
            case WAYPOINT:
            case SPAWNER:
            case QUICKSAND:
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
            Sprite drawnSprite = Sprite.ROBOT_IDLE;
            if (robot.peril > 0.0f) {
                if (robot.perilDelta > 0.0f) {
                    Animation.beginPhase(robot.peril);
                    drawnSprite = Sprite.ROBOT_FLAIL_START;
                } else if (robot.perilDelta < 0.0f) {
                    Animation.beginPhase(1.0f - robot.peril);
                    drawnSprite = Sprite.ROBOT_FLAIL_END;
                } else {
                    drawnSprite = Sprite.ROBOT_FLAIL;
                }
            } else if (robot.sourceNode != robot.destNode) {
                drawnSprite = Sprite.ROBOT_WALK;
            } else if (robot.available() && robot.sourceNode.reserves != 0) {
                switch (robot.sourceNode.type) {
                case WELL:
                    drawnSprite = Sprite.ROBOT_EAT_WATER;
                    break;
                case WRECKAGE:
                case MINE:
                    drawnSprite = Sprite.ROBOT_EAT_DIRT;
                    break;
                default:
                    break;
                }
            }
            if (robot.flipped) {
                drawnSprite.drawFlipped(batch, robot.x(), robot.y());
            } else {
                drawnSprite.draw(batch, robot.x(), robot.y());
            }
            Animation.endPhase();
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

        Texture fplane = Overlord.get().assetManager.get(
                "Layout/LayoutPanes/FrontObjects.png", Texture.class);
        batch.begin();
        batch.draw(fplane, -worldPlaneWidth / 2, -worldPlaneHeight / 2,
                worldPlaneWidth, worldPlaneHeight);

        Matrix4 proj = new Matrix4(batch.getProjectionMatrix());
        batch.setProjectionMatrix(new Matrix4());
        renderFG();
        batch.end();

        proj.val[12] = 0.0f;
        proj.val[13] = 0.0f;
        proj.val[14] = 0.0f;
        proj.val[0] /= THE_SCALE;
        proj.val[5] /= THE_SCALE;
        proj.val[10] /= THE_SCALE;
        batch.setProjectionMatrix(proj);
        batch.begin();

        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                BitmapFont.class);
        batch.setShader(Overlord.get().getFontShader());
        fnt.drawMultiLine(batch, "Time: " + (int) dayCounter + "\nDay: "
                + box.day, -(1024 / 2) + 10, (640 / 2) - 10);
        batch.setShader(null);
        box.displayInfo(batch, (1024 / 2) - 30, (640 / 2) - 30, 1.0f, false);
        batch.end();

        dayCounter += dt;

        if (dayCounter > Constants.DAY_LENGTH.asFloat()) {
            final float ROBOT_SAFE_DISTANCE = Constants.SAFE_ZONE_RADIUS
                    .asFloat();
            box.clearRobots();
            int robotIndex = 0;
            PathNode home = nodes.lookup("home");
            for (Robot robot : robots) {
                float distance = (float) Math.hypot(robot.x() - home.x,
                        robot.y() - home.y);
                if (distance <= ROBOT_SAFE_DISTANCE) {
                    box.robots[robotIndex] = robot.cls;
                    ++robotIndex;
                }
            }

            if (box.isWin()) {
                ArrayList<String> results = new ArrayList<String>();
                results.add("win");
                return new ResultsScreen(results, null);
            }
            if (box.isLoss()) {
                ArrayList<String> results = new ArrayList<String>();
                results.add("lose");
                return new ResultsScreen(results, null);
            }

            NightMode nm = new NightMode(box);
            List<String> messages = new ArrayList<String>();
            messages.add("Day " + box.day + " is over.");
            if (initialBox.activeRobots() > box.activeRobots()) {
                messages.add((initialBox.activeRobots() - box.activeRobots())
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

    private void renderFG() {
        Texture l3 = Overlord.get().assetManager.get(
                "Layout/LayoutPanes/Filter.png", Texture.class);
        batch.draw(l3, -1, -1, 2, 2);
    }

    private void pan(ScreenEdge screenEdge, float dt) {
        float dx = 0.0f, dy = 0.0f;
        float diagonalPan = 0.78f;
        switch (screenEdge) {
        case NONE:
            break;
        case BOTTOM:
            dy = -1;
            break;
        case BOTTOM_LEFT:
            dx = -diagonalPan;
            dy = -diagonalPan;
            break;
        case BOTTOM_RIGHT:
            dx = diagonalPan;
            dy = -diagonalPan;
            break;
        case LEFT:
            dx = -1;
            break;
        case RIGHT:
            dx = 1;
            break;
        case TOP:
            dy = 1;
            break;
        case TOP_LEFT:
            dx = -diagonalPan;
            dy = diagonalPan;
            break;
        case TOP_RIGHT:
            dx = diagonalPan;
            dy = diagonalPan;
            break;
        default:
            break;
        }
        float ss = Constants.SCROLL_SPEED.asFloat();
        viewport.getCamera().translate(dx * ss * dt, dy * ss * dt, 0.0f);
        offX += dx * dt * ss;
        offY += dy * dt * ss;
        viewport.getCamera().update();
    }

    private void renderBG() {
        Texture l1 = Overlord.get().assetManager.get(
                "Layout/LayoutPanes/Parallaxrear1.png", Texture.class);
        Texture l2 = Overlord.get().assetManager.get(
                "Layout/LayoutPanes/Parallaxrear2.png", Texture.class);
        Texture l3 = Overlord.get().assetManager.get(
                "Layout/LayoutPanes/Sky.png", Texture.class);
        batch.begin();
        final float L1_FACTOR = 0.0003f;
        final float L2_FACTOR = 0.00006f;
        batch.draw(l3, -1, -1, 2, 2);
        batch.draw(l2, -1 - offX * L2_FACTOR, -1 - offY * L2_FACTOR, 2, 2);
        batch.draw(l1, -1 - offX * L1_FACTOR, -1 - offY * L1_FACTOR, 2, 2);
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
                selectedRobot.perilDelta = -1.0f;
            }
        }
    }
}
