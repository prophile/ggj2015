package uk.co.alynn.games.suchrobot.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import uk.co.alynn.games.suchrobot.MainMode;
import uk.co.alynn.games.suchrobot.RobotGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = (int) MainMode.WORLD_WIDTH;
		config.height = (int) MainMode.WORLD_HEIGHT;
		
		config.title = "Robot Game";
		config.vSyncEnabled = true;
		config.resizable = true;
		
		new LwjglApplication(new RobotGame(), config);
	}
}
