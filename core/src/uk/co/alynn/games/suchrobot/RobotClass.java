package uk.co.alynn.games.suchrobot;

public enum RobotClass {
    RINGO(null), GEORGE(Sprite.CHEVRONS_L1), PAUL(Sprite.CHEVRONS_L2), JOHN(
            Sprite.CHEVRONS_L3);

    public Sprite pips;

    private RobotClass(Sprite spr) {
        pips = spr;
    }
}
