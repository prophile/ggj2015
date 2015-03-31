package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Box {
    public int water = Constants.initialWater();
    public int salvage = Constants.initialSalvage();
    public int metal = Constants.initialMetal();

    public final RobotClass[] robots;

    public int day = 1;

    public Box() {
        int numSlots = Constants.maxRobots();
        robots = new RobotClass[numSlots];
        clearRobots();
    }

    public void clearRobots() {
        for (int i = 0; i < robots.length; ++i) {
            robots[i] = RobotClass.RINGO;
        }
    }

    public int activeRobots() {
        int active = 0;
        for (RobotClass robot : robots) {
            if (robot != RobotClass.RINGO) {
                ++active;
            }
        }
        return active;
    }

    public Box copy() {
        int numSlots = Constants.maxRobots();
        Box cp = new Box();
        cp.water = water;
        cp.salvage = salvage;
        cp.metal = metal;
        for (int i = 0; i < numSlots; ++i) {
            cp.robots[i] = robots[i];
        }
        cp.day = day;
        return cp;
    }

    public boolean isWin() {
        return salvage >= Constants.targetSalvage();
    }

    public boolean isLoss() {
        if (water <= 0)
            return true;
        if (day > Constants.lastDay())
            return true;
        if (activeRobots() == 0 && metal < Constants.robotMetalCost())
            return true;
        return false;
    }

    public void displayInfo(SpriteBatch batch, int x, int y, float scale,
            boolean onlyMetal) {
        final float SEPARATION_DISTANCE = Constants.resourceDisplaySpacing() * scale;
        if (onlyMetal) {
            for (int i = 0; i < metal; ++i) {
                int colIndex = (i % 6);
                int rowIndex = (i / 6);
                Sprite.ICON_METAL.draw(batch, x - SEPARATION_DISTANCE
                        * colIndex, y - SEPARATION_DISTANCE * rowIndex, scale);
            }
        } else {
            for (int i = 0; i < water; ++i) {
                Sprite.ICON_WATER.draw(batch, x - SEPARATION_DISTANCE * i, y,
                        scale);
            }
            for (int i = 0; i < metal; ++i) {
                Sprite.ICON_METAL.draw(batch, x - SEPARATION_DISTANCE * i, y
                        - SEPARATION_DISTANCE, scale);
            }
            for (int i = 0; i < salvage; ++i) {
                Sprite.ICON_SALVAGE.draw(batch, x - SEPARATION_DISTANCE * i, y
                        - SEPARATION_DISTANCE * 2, scale);
            }
        }
    }
}
