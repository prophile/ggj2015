package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ResultsScreen implements GameMode {
    private final Iterable<String> results;
    private final GameMode next;

    private Viewport viewport;
    private SpriteBatch batch;

    private float time = 0.0f;

    public ResultsScreen(Iterable<String> screenResults, GameMode advanceTo) {
        next = advanceTo;
        results = screenResults;
    }

    @Override
    public void start() {
        batch = new SpriteBatch();
        batch.setShader(Overlord.get().getFontShader());
        viewport = new ExtendViewport(400.0f, 400.0f);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public GameMode tick() {
        final float FADE_TIME = 0.4f;
        final float PAUSE_TIME = 0.3f;
        final float ADJUST = 60.0f;
        final float END_TIME = 6.0f;
        batch.setProjectionMatrix(viewport.getCamera().combined);
        int i = 0;
        float y = 150.0f;
        BitmapFont fnt = Overlord.get().assetManager.get("bitstream.fnt",
                BitmapFont.class);
        for (String resultLine : results) {
            float offsetTime = time - (i * (PAUSE_TIME + FADE_TIME));
            float alpha;
            if (offsetTime < 0)
                alpha = 0.0f;
            else if (offsetTime < FADE_TIME)
                alpha = offsetTime / FADE_TIME;
            else
                alpha = 1.0f;
            if (alpha > 0.0f) {
                fnt.setColor(1.0f, 1.0f, 1.0f, alpha);
                batch.begin();
                float width = fnt.getBounds(resultLine).width;
                fnt.draw(batch, resultLine, -width / 2, y);
                y -= ADJUST;
                batch.end();
            }
            i += 1;
        }
        fnt.setColor(Color.WHITE);
        float endTargetTime = i * (PAUSE_TIME + FADE_TIME) + END_TIME;
        if (time > endTargetTime)
            return next;
        time += Gdx.graphics.getDeltaTime();
        return this;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void click(int mouseX, int mouseY) {
    }

    @Override
    public void rightClick(int mouseX, int mouseY) {
    }
}
