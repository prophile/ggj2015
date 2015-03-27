package uk.co.alynn.games.suchrobot.desktop;

import uk.co.alynn.games.suchrobot.RobotGame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = 1024;
        config.height = 640;

        config.title = "Salvage";
        config.vSyncEnabled = true;
        config.resizable = true;

        new LwjglApplication(new RobotGame(), config);
    }
}
