package uk.co.alynn.games.suchrobot;

import java.util.Scanner;

public enum Constants {
    SPEED, INITIAL_ROBOTS, INITIAL_WATER, INITIAL_METAL, INITIAL_SALVAGE, TARGET_SALVAGE, DAY_LENGTH, WELL_PUMP_TIME, METAL_MINE_TIME, SALVAGE_TIME, SAFE_ZONE_RADIUS, LAST_DAY;

    public float asFloat() {
        return lookups[ordinal()];
    }

    public boolean asBoolean() {
        return asFloat() > 0.5f;
    }

    public int asInt() {
        return (int) asFloat();
    }

    private static float lookups[];

    public static void loadConstants(Scanner source) {
        lookups = new float[Constants.values().length];
        while (source.hasNext()) {
            String name = source.next();
            Constants k = Constants.valueOf(name);
            float value = source.nextFloat();
            lookups[k.ordinal()] = value;
        }
    }
}
