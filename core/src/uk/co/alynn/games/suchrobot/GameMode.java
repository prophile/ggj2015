package uk.co.alynn.games.suchrobot;

public interface GameMode {
    public void start();

    public void stop();

    public GameMode tick(ScreenEdge screenEdge);

    public void resize(int width, int height);

    public void click(int mouseX, int mouseY);

    public void rightClick(int mouseX, int mouseY);
}
