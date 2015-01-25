package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Box {
    public int water = Constants.INITIAL_WATER.asInt();
    public int salvage = Constants.INITIAL_SALVAGE.asInt();
    public int metal = Constants.INITIAL_METAL.asInt();

    public int robots = Constants.INITIAL_ROBOTS.asInt();

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
        return salvage >= Constants.TARGET_SALVAGE.asInt();
    }

    public boolean isLoss() {
        if (water <= 0)
            return true;
        if (day > Constants.LAST_DAY.asInt())
            return true;
        if (robots == 0 && metal < Constants.ROBOT_METAL_COST.asInt())
            return true;
        return false;
    }

    public void displayInfo(SpriteBatch batch, int x, int y) {
        final int SEPARATION_DISTANCE = Constants.RESOURCE_DISPLAY_SPACING
                .asInt();
        for (int i = 0; i < water; ++i) {
            Sprite.ICON_WATER.draw(batch, x - SEPARATION_DISTANCE * i, y);
        }
        for (int i = 0; i < metal; ++i) {
            Sprite.ICON_METAL.draw(batch, x - SEPARATION_DISTANCE * i, y
                    - SEPARATION_DISTANCE);
        }
        for (int i = 0; i < salvage; ++i) {
            Sprite.ICON_SALVAGE.draw(batch, x - SEPARATION_DISTANCE * i, y
                    - SEPARATION_DISTANCE * 2);
        }
    }
}
