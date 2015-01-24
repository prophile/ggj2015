package uk.co.alynn.games.suchrobot;

public class VictoryMode extends ScreenMode {

    @Override
    String screenPath() {
        return "UI/Victory.png";
    }

    @Override
    GameMode advance(int x, int y) {
        return null;
    }

}
