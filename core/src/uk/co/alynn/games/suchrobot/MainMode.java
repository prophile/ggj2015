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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMode implements GameMode {
    private float THE_SCALE = Constants.SCALE.asFloat();
    public static final float WORLD_WIDTH = 1024.0f;
    public static final float WORLD_HEIGHT = 640.0f;
    private SpriteBatch batch = null;
    private ShapeRenderer sr = null;
    private Viewport viewport = null;
    private static NodeSet nodes = null;
    private List<Robot> robots = new ArrayList<Robot>();
    private Robot selectedRobot = null;
    private double dayCounter;

    private String dayText;

    private final boolean DEBUG = Constants.DEBUG_NODES.asBoolean();
    private final Box box;
    private final Box initialBox;

    private float offX, offY;

    private final Matrix4 overlayProjection = new Matrix4();

    public static void init() {
        if (nodes != null) {
            return;
        }
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
        viewport = new FitViewport(WORLD_WIDTH / THE_SCALE, WORLD_HEIGHT
                                   / THE_SCALE);
        batch = new SpriteBatch();
        sr = new ShapeRenderer();

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

        dayText = "Day " + box.day;

        SFX.MUSIC.play();
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    static final Matrix4 IDENTITY_MATRIX = new Matrix4();

    @Override
    public GameMode tick(ScreenEdge screenEdge) {
        float dt = Gdx.graphics.getDeltaTime();

        pan(screenEdge, dt);

        batch.setProjectionMatrix(IDENTITY_MATRIX);
        renderBG();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        Texture l1 = Overlord.get().assetManager.get("Layout/LayoutPanes/Robotplane.png", Texture.class);
        batch.begin();
        float worldPlaneWidth = l1.getWidth();
        float worldPlaneHeight = l1.getHeight();
        batch.draw(l1, -worldPlaneWidth / 2, -worldPlaneHeight / 2,
                   worldPlaneWidth, worldPlaneHeight);
        batch.end();

        sr.setProjectionMatrix(viewport.getCamera().combined);

        performRobotActions(dt);

        renderPathNodes();

        renderSelectionCircleUpper();
        renderRobots();
        renderSelectionCircleLower();

        renderDebugNodeMapOverlay();


        Texture fplane = Overlord.get().assetManager.get("Layout/LayoutPanes/FrontObjects.png", Texture.class);
        batch.begin();
        batch.draw(fplane, -worldPlaneWidth / 2, -worldPlaneHeight / 2,
                   worldPlaneWidth, worldPlaneHeight);

        Matrix4 proj = overlayProjection;
        proj.set(batch.getProjectionMatrix());
        batch.setProjectionMatrix(IDENTITY_MATRIX);
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

        renderOffscreenIndicators();

        Color INFOCOL = Color.WHITE;

        renderDayText(INFOCOL);
        renderResourceUI();
        batch.end();

        renderDayTimer(proj, INFOCOL);

        incrementDayCounter(dt);

        if (dayOver()) {
            return levelCompleteNextScreen();
        } else {
            return this;
        }
    }

    private void incrementDayCounter(float dt) {
        dayCounter += dt;
    }

    private void renderDayTimer(Matrix4 proj, Color col) {
        sr.setColor(col);
        sr.setProjectionMatrix(proj);
        sr.begin(ShapeType.Filled);
        final float LENS = 70;
        sr.rect(-LENS, 250f,
                (float) (LENS * 2f * (1.0f - (dayCounter / Constants.DAY_LENGTH
                        .asFloat()))), 30f);
        sr.end();
    }

    private void renderResourceUI() {
        box.displayInfo(batch, (1024 / 2) - 30, (640 / 2) - 30, 1.0f, false);
    }

    private void renderDayText(Color col) {
        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                                                         BitmapFont.class);
        batch.setShader(Overlord.get().getFontShader());
        fnt.setColor(col);
        fnt.drawMultiLine(batch, dayText, -(1024 / 2) + 10,
                          (640 / 2) - 10);
        fnt.setColor(Color.WHITE);
        batch.setShader(null);
    }

    private void renderSelectionCircleLower() {
        sr.begin(ShapeType.Line);
        if (selectedRobot != null) {
            sr.setColor(Color.YELLOW);
            sr.curve(selectedRobot.x() - CURVE_A, selectedRobot.y(),
                     selectedRobot.x() - CURVE_B, selectedRobot.y() - CURVE_C,
                     selectedRobot.x() + CURVE_B, selectedRobot.y() - CURVE_C,
                     selectedRobot.x() + CURVE_A, selectedRobot.y(), 32);
        }
        sr.end();
    }

    private void renderDebugNodeMapOverlay() {
        if (DEBUG) {
            sr.setColor(Color.CYAN);
            sr.begin(ShapeType.Line);
            for (PathNode node : nodes) {
                for (PathNode node2 : nodes.connectionsFrom(node)) {
                    if (node2.hashCode() > node.hashCode()) {
                        sr.line(node2.x, node2.y, node.x, node.y);
                    }
                }
            }
            sr.end();
        }
    }

    private void renderRobots() {
        batch.begin();
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
            final float ICON_OFFSET = 50.0f;
            if (robot.peril > 0.0f && robot.perilDelta >= 0.0f) {
                Sprite.ICON_OFFSCREEN_FLAIL.draw(batch, robot.x(), robot.y()
                                                 + ICON_OFFSET);
            } else {
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
            Sprite rankPips = robot.rankSprite();
            if (rankPips != null) {
                rankPips.draw(batch, robot.x() + 18, robot.y() + 17);
            }
        }

        batch.end();
    }

    private void renderSelectionCircleUpper() {
        sr.begin(ShapeType.Line);
        sr.setColor(Color.YELLOW);
        if (selectedRobot != null) {
            sr.curve(selectedRobot.x() - CURVE_A, selectedRobot.y(),
                     selectedRobot.x() - CURVE_B, selectedRobot.y() + CURVE_C,
                     selectedRobot.x() + CURVE_B, selectedRobot.y() + CURVE_C,
                     selectedRobot.x() + CURVE_A, selectedRobot.y(), 32);
        }
        sr.end();
    }

    private void renderPathNodes() {
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
        batch.end();
    }

    private void performRobotActions(float dt) {
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
                    if (visited.reserves > 0 &&
                        robot.available() &&
                        robot.carrying == CargoType.NOTHING) {
                        SFX.WATER_PUMP.loop();
                    }
                    break;
                case SPAWNER:
                    if (visited != robot.spawnNode) {
                        break;
                    }
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
                    if (visited.reserves > 0 &&
                        robot.available() &&
                        robot.carrying == CargoType.NOTHING) {
                        SFX.SALVAGE.loop();
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
                    if (visited.reserves > 0 &&
                        robot.available() &&
                        robot.carrying == CargoType.NOTHING) {
                        SFX.METAL_MINE.loop();
                    }
                    break;
                case QUICKSAND:
                    if (robot.peril > 0.0f) {
                        SFX.QUICKSAND.loop();
                    }
                    break;
                default:
                    break;
                }
            } else {
                SFX.ENGINES.loop();
            }
        }
    }

    private GameMode levelCompleteNextScreen() {
        updateForRobotLosses();

        if (box.isWin()) {
            return generateWinScreen();
        }
        if (box.isLoss()) {
            return generateLossScreen();
        }

        NightMode nm = new NightMode(box);
        List<String> messages = generateNightMessages();
        return new ResultsScreen(messages, nm);
    }

    private List<String> generateNightMessages() {
        List<String> messages = new ArrayList<String>();
        messages.add("Day " + box.day + " is over.");

        if (initialBox.activeRobots() > box.activeRobots()) {
            messages.add((initialBox.activeRobots() - box.activeRobots())
                         + " robots didn't make it back.");
        }
        if (initialBox.salvage < box.salvage) {
            if (initialBox.salvage == 0) {
                messages.add("Started the salvage today.");
            } else {
                messages.add("Salvaged more parts from the ship.");
            }
        }
        if (initialBox.metal < box.metal) {
            messages.add("Picked up " + (box.metal - initialBox.metal)
                         + " metal.");
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
        return messages;
    }

    private GameMode generateLossScreen() {
        ArrayList<String> results = new ArrayList<String>();
        if (box.water <= 0) {
            results.add("Without water I was too weak to fight");
            results.add("They came in the night to take me away");
            results.add("I'll never see my planet again...");
        } else if (box.activeRobots() <= 0) {
            results.add("The robots are gone");
            results.add("No scrap to build more, I'm done for");
            results.add("I'll never see my planet again...");
        } else {
            results.add("I took too long, they found me");
            results.add("Flashing lights and black suits");
            results.add("I'll never see my planet again...");
        }
        results.add("Game over");
        SFX.LOSE.play();
        return new ResultsScreen(results, new TitleScreen());
    }

    private GameMode generateWinScreen() {
        ArrayList<String> results = new ArrayList<String>();
        results.add("The robots proved their worth");
        results.add("I managed to make the repairs");
        results.add("Finally I can escape!");
        results.add("You Escaped!");
        SFX.WIN.play();
        return new ResultsScreen(results, new TitleScreen());
    }

    private void updateForRobotLosses() {
        final float ROBOT_SAFE_DISTANCE = Constants.SAFE_ZONE_RADIUS
                .asFloat();
        box.clearRobots();
        int robotIndex = 0;
        for (Robot robot : robots) {
            boolean isSafe = false;
            for (PathNode home : nodes) {
                if (home.type != NodeType.SPAWNER)
                    continue;
                float distance = (float) Math.hypot(robot.x() - home.x,
                                                    robot.y() - home.y);
                if (distance <= ROBOT_SAFE_DISTANCE) {
                    isSafe = true;
                }
            }
            if (isSafe) {
                box.robots[robotIndex] = robot.cls;
                ++robotIndex;
            }
        }
    }

    private boolean dayOver() {
        return dayCounter > Constants.DAY_LENGTH.asFloat();
    }

    private void renderOffscreenIndicators() {
        final float MARKER_INSET = 10.0f;
        final float W = WORLD_WIDTH / (THE_SCALE * 2);
        final float W_ = W - MARKER_INSET;
        final float H = WORLD_HEIGHT / (THE_SCALE * 2);

        final float H_ = H - MARKER_INSET;
        for (Robot robot : robots) {
            float effectiveX = robot.x() - offX;
            float effectiveY = robot.y() - offY;
            if (effectiveX < W && effectiveX > -W && effectiveY < H
                    && effectiveY > -H)
                continue;
            Sprite selectedSprite;
            if (robot.peril > 0.0f) {
                selectedSprite = Sprite.ICON_OFFSCREEN_FLAIL;
            } else if (robot.sourceNode != robot.destNode) {
                selectedSprite = Sprite.ICON_OFFSCREEN_WALK;
            } else {
                selectedSprite = Sprite.ICON_OFFSCREEN_IDLE;
            }
            if (effectiveX > W_) {
                effectiveY *= (W_ / effectiveX);
                effectiveX = W_;
            }
            if (effectiveY > H_) {
                effectiveX *= (H_ / effectiveY);
                effectiveY = H_;
            }
            if (effectiveY < -H_) {
                effectiveX *= (-H_ / effectiveY);
                effectiveY = -H_;
            }
            if (effectiveX < -W_) {
                effectiveY *= (-W_ / effectiveX);
                effectiveX = -W_;
            }
            selectedSprite.draw(batch, effectiveX * THE_SCALE, effectiveY
                                * THE_SCALE, 1.6f);
        }
    }

    private void renderFG() {
        Texture l3 = Overlord.get().assetManager.get("Layout/LayoutPanes/Filter.png",
                                                     Texture.class);
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
        final float limitX = Constants.PAN_LIMIT_X.asFloat();
        final float limitY = Constants.PAN_LIMIT_Y.asFloat();

        if (offX < -limitX && dx < 0)
            dx = 0;
        if (offX > limitX && dx > 0)
            dx = 0;
        if (offY < -limitY && dy < 0)
            dy = 0;
        if (offY > limitY && dy > 0)
            dy = 0;

        float ss = Constants.SCROLL_SPEED.asFloat();
        viewport.getCamera().translate(dx * ss * dt, dy * ss * dt, 0.0f);
        offX += dx * dt * ss;
        offY += dy * dt * ss;

        viewport.getCamera().update();
    }

    private void renderBG() {
        Texture l1 = Overlord.get().assetManager.get("Layout/LayoutPanes/Parallaxrear1.png", Texture.class);
        Texture l2 = Overlord.get().assetManager.get("Layout/LayoutPanes/Parallaxrear2.png", Texture.class);
        Texture l3 = Overlord.get().assetManager.get("Layout/LayoutPanes/Sky.png", Texture.class);
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

    private final Vector2 rcUnproject = new Vector2();
    private static final float CURVE_A = 10.0f;
    private static final float CURVE_B = 8.0f;
    private static final float CURVE_C = 7.0f;

    @Override
    public void rightClick(int mouseX, int mouseY) {
        if (selectedRobot == null)
            return;
        rcUnproject.x = mouseX;
        rcUnproject.y = mouseY;
        Vector2 worldCoords = viewport.unproject(rcUnproject);

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
        SFX.SELECT_TARGET.play();
    }

    @Override
    public void click(int mouseX, int mouseY) {
        rcUnproject.x = mouseX;
        rcUnproject.y = mouseY;
        Vector2 worldCoords = viewport.unproject(rcUnproject);

        final float ROBOT_SELECT_DISTANCE = 70.0f;

        selectedRobot = null;
        float bestPointerDistance = Float.POSITIVE_INFINITY;
        for (Robot robot : robots) {
            float distanceFromPointer = (float) Math.hypot(robot.x()
                                                           - worldCoords.x, robot.y() - worldCoords.y);
            if (distanceFromPointer < ROBOT_SELECT_DISTANCE) {
                if (selectedRobot == null
                        || distanceFromPointer < bestPointerDistance) {
                    selectedRobot = robot;
                    bestPointerDistance = distanceFromPointer;
                }
            }
        }

        if (selectedRobot != null) {
            selectedRobot.perilDelta = -1.0f;
            System.err.println("SELECT GUY");
            SFX.SELECT_ROBOT.play();
        } else {
            System.err.println("UNSELECT GUY");
        }
    }
}
