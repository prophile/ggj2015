package uk.co.alynn.games.suchrobot;

public enum RobotClass {
    RINGO(null),
    GEORGE(null),
    PAUL(Sprite.CHEVRONS_L1),
    JOHN(Sprite.CHEVRONS_L2);

    public Sprite pips;

    private RobotClass(Sprite spr) {
        pips = spr;
    }
}
