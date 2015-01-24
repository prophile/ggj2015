package uk.co.alynn.games.suchrobot;

public class TitleScreen extends ScreenMode {

    @Override
    String screenPath() {
        return "UI/Titles.png";
    }

    @Override
    GameMode advance(int x, int y) {
        Box box = new Box();
        SFX.THUNK.play();
        return new MainMode(box);
    }

}
