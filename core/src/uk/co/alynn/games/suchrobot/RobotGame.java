package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RobotGame extends ApplicationAdapter {
    SpriteBatch batch;

    @Override
    public void create () {
        Overlord.init();
        batch = new SpriteBatch();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Texture img = Sprite.BADLOGIC.getTexture();
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }
}
