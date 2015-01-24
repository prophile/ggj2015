package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMode implements GameMode {
    private SpriteBatch batch = null;

    @Override
    public void start() {
        batch = new SpriteBatch();
    }

    @Override
    public void stop() {
        batch.dispose();
    }

    @Override
    public void draw() {
        Texture img = Sprite.BADLOGIC.getTexture();
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

}
