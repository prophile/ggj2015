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
        SFX.THUNK.play();
        return new NightMode(box);
    }

}
