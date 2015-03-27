package uk.co.alynn.games.suchrobot;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useCompass = false;
        config.useAccelerometer = false;
        config.orientationLandscape = false;
        config.orientationPortrait = true;
        config.preventScreenDimming = true;

        return new IOSApplication(new RobotGame(), config);
    }

    public static void main(String[] args) {
        System.err.println("Hello, world!");
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, IOSLauncher.class);
        pool.close();
    }
}
