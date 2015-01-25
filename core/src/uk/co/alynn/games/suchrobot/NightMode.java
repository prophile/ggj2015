package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class NightMode implements GameMode {

    private SpriteBatch batch;

    private final Box box;
    private Viewport viewport;

    private boolean update = false;
    private boolean clicked = false;

    public NightMode(Box box) {
        this.box = box;
    }

    @Override
    public void start() {
        batch = new SpriteBatch();
        viewport = new FitViewport(1417, 1276);
        viewport.getCamera().translate(1417 / 2.0f, 1276 / 2.0f, 0.0f);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        box.day += 1;
        box.water -= 1;
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public GameMode tick(ScreenEdge screenEdge) {
        boolean nextSelected = mouseInND();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        Texture tex = Overlord.get().assetManager.get(
                "UI/NightUIRough/NextDayUI.png", Texture.class);
        Texture texMO = Overlord.get().assetManager.get(
                "UI/NightUIRough/NextDayUIMO.png", Texture.class);
        Texture texBase = Overlord.get().assetManager.get(
                "UI/NightUIRough/Robotmenubase.png", Texture.class);
        Texture buy = Overlord.get().assetManager.get(
                "UI/NightUIRough/Addrobotbutton.png", Texture.class);
        Texture noRobbie = Overlord.get().assetManager.get(
                "UI/NightUIRough/Inactive robot.png", Texture.class);
        Texture robbieL1 = Overlord.get().assetManager.get(
                "UI/NightUIRough/RoboBGlvl1.png", Texture.class);
        Texture noRobbieMO = Overlord.get().assetManager.get(
                "UI/NightUIRough/Inactive robotMO.png", Texture.class);
        Texture robbieL1MO = Overlord.get().assetManager.get(
                "UI/NightUIRough/RoboMOlvl1.png", Texture.class);
        batch.begin();
        batch.setShader(null);
        batch.draw(texBase, 0, 0, 1417, 1276);
        if (nextSelected) {
            batch.draw(texMO, 0, 0, 1417, 1276);
        } else {
            batch.draw(tex, 0, 0, 1417, 1276);
        }
        int maxRobots = Constants.MAX_ROBOTS.asInt();
        boolean anyPurchasesPresented = false;
        for (int i = 0; i < maxRobots; ++i) {
            int colIndex = (i % 4);
            int rowIndex = (i / 4);
            boolean purchased = box.robots[i] != RobotClass.RINGO;
            Texture robotex;
            int centrePointX = 402 + colIndex * 200;
            int centrePointY = 990 - rowIndex * 252;
            int w = (int) (robbieL1.getWidth() * 0.5f), h = (int) (robbieL1
                    .getHeight() * 0.5f);
            int lbx = centrePointX - w;
            int ubx = centrePointX + w;
            int lby = centrePointY - h;
            int uby = centrePointY + h;
            boolean mo = mouseInBox(lbx, ubx, lby, uby);
            boolean purchasable = !purchased && !anyPurchasesPresented;
            if (purchasable) {
                if (clicked) {
                    int robotCost = Constants.ROBOT_METAL_COST.asInt();
                    if (box.metal >= robotCost) {
                        box.robots[i] = RobotClass.GEORGE;
                        box.metal -= robotCost;
                        purchased = true;
                    }
                }
            }
            if (purchased) {
                if (mo) {
                    robotex = robbieL1MO;
                } else {
                    robotex = robbieL1;
                }
            } else {
                if (mo) {
                    robotex = noRobbieMO;
                } else {
                    robotex = noRobbie;
                }
            }
            batch.draw(robotex, centrePointX - robotex.getWidth() * 0.5f,
                    centrePointY - robotex.getHeight() * 0.5f);
            if (purchasable) {
                batch.draw(buy, centrePointX - buy.getWidth() * 0.5f,
                        centrePointY - buy.getHeight() * 0.5f);
                anyPurchasesPresented = true;
            }
        }
        box.displayInfo(batch, 1030, 810);
        batch.end();

        clicked = false;

        return update ? new MainMode(box) : this;
    }

    private boolean mouseInBox(int left, int right, int bottom, int top) {
        int rawMouseX = Gdx.input.getX();
        int rawMouseY = Gdx.input.getY();
        Vector2 pickedMousePosition = viewport.unproject(new Vector2(rawMouseX,
                rawMouseY));
        float mx = pickedMousePosition.x;
        float my = pickedMousePosition.y;

        boolean nextSelected = (mx >= left && mx <= right && my >= bottom && my <= top);
        return nextSelected;
    }

    private boolean mouseInND() {
        return mouseInBox(880, 1120, 100, 250);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        if (mouseInND()) {
            update = true;
        }
        clicked = true;
    }

    @Override
    public void rightClick(int mouseX, int mouseY) {
        click(mouseX, mouseY);
    }

}
