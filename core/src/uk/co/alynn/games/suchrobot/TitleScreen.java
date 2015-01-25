package uk.co.alynn.games.suchrobot;

public class TitleScreen extends ScreenMode {

    @Override
    String screenPath() {
        return "UI/Titles.png";
    }

    private synchronized void loadThings() {
        MainMode.init();
    }

    @Override
    public void start() {
        super.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadThings();
            }
        }).start();
        loadThings();
    }

    @Override
    synchronized GameMode advance(int x, int y) {
        Box box = new Box();
        for (int i = 0; i < Constants.INITIAL_ROBOTS.asInt(); ++i) {
            box.robots[i] = RobotClass.GEORGE;
        }
        return new MainMode(box);
    }

}
