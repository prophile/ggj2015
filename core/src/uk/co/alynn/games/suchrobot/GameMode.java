package uk.co.alynn.games.suchrobot;

public interface GameMode {
    public void start();
    public void stop();
    public void draw();
    public void resize(int width, int height);
}
