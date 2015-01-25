package uk.co.alynn.games.suchrobot;

public enum ScreenEdge {
    TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT, NONE;

    public static ScreenEdge getEdge(int x, int y) {
        ScreenEdge[] table = { BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT, LEFT, NONE,
                RIGHT, TOP_LEFT, TOP, TOP_RIGHT };
        int xz = (x < 0) ? 0 : ((x == 0) ? 1 : 2);
        int yz = (y < 0) ? 0 : ((y == 0) ? 1 : 2);
        int ix = yz * 3 + xz;
        return table[ix];
    }
}
