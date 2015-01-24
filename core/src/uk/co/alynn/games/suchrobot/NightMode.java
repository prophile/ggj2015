package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    public GameMode tick() {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        Texture tex = Overlord.get().assetManager.get(
                "UI/NightUIRough/NextDayUI.png", Texture.class);
        batch.begin();
        batch.setShader(null);
        batch.draw(tex, 0, 0, 1417, 1276);
        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                BitmapFont.class);
        batch.setShader(Overlord.get().getFontShader());
        fnt.draw(batch, "mtl " + box.metal, 0, 60);
        batch.end();

        if (box.salvage >= 1)
            return new VictoryMode();
        if (box.water < 0 || box.day > 10 || box.robots == 0)
            return new GameOverMode();

        return update ? new MainMode(box) : this;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        update = true;
    }

    @Override
    public void rightClick(int mouseX, int mouseY) {
    }

}
