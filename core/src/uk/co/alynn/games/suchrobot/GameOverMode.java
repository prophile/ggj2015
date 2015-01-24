package uk.co.alynn.games.suchrobot;

public class GameOverMode extends ScreenMode {

    @Override
    String screenPath() {
        return "UI/GameOver.png";
    }

    @Override
    GameMode advance(int x, int y) {
        return null;
    }

}
