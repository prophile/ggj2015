package uk.co.alynn.games.suchrobot;

public class Box {
    public int water;
    public int salvage;
    public int metal;

    public int robots = 2;

    public int day = 1;

    public Box copy() {
        Box cp = new Box();
        cp.water = water;
        cp.salvage = salvage;
        cp.metal = metal;
        cp.robots = robots;
        cp.day = day;
        return cp;
    }

    public boolean isWin() {
        return salvage >= 10;
    }

    public boolean isLoss() {
        if (water <= 0)
            return true;
        if (day > 10)
            return true;
        if (robots == 0)
            return true;
        return false;
    }
}
