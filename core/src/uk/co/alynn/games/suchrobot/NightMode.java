package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class NightMode implements GameMode {

    private SpriteBatch batch;

    private final Box box;
    private Viewport viewport;

    private boolean update = false;

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
        batch.begin();
        batch.setShader(null);
        batch.draw(texBase, 0, 0, 1417, 1276);
        if (nextSelected) {
            batch.draw(texMO, 0, 0, 1417, 1276);
        } else {
            batch.draw(tex, 0, 0, 1417, 1276);
        }
        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                BitmapFont.class);
        batch.setShader(Overlord.get().getFontShader());
        fnt.draw(batch, "mtl " + box.metal + " / rob " + box.robots, 0, 60);
        batch.end();

        return update ? new MainMode(box) : this;
    }

    private boolean mouseInND() {
        int rawMouseX = Gdx.input.getX();
        int rawMouseY = Gdx.input.getY();
        Vector2 pickedMousePosition = viewport.unproject(new Vector2(rawMouseX,
                rawMouseY));
        final float NEXT_LEFT_BOUND = 880;
        final float NEXT_RIGHT_BOUND = 1120;
        final float NEXT_BOTTOM_BOUND = 100;
        final float NEXT_TOP_BOUND = 250;

        float mx = pickedMousePosition.x;
        float my = pickedMousePosition.y;

        boolean nextSelected = (mx >= NEXT_LEFT_BOUND && mx <= NEXT_RIGHT_BOUND
                && my >= NEXT_BOTTOM_BOUND && my <= NEXT_TOP_BOUND);
        return nextSelected;
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
    }

    @Override
    public void rightClick(int mouseX, int mouseY) {
        int robotCost = Constants.ROBOT_METAL_COST.asInt();
        if (box.metal >= robotCost && box.robots < Constants.MAX_ROBOTS.asInt()) {
            box.robots += 1;
            box.metal -= robotCost;
        }
    }

}
