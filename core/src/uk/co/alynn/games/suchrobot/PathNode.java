package uk.co.alynn.games.suchrobot;

public final class PathNode {
    public final NodeType type;
    public final String name;
    public final float x;
    public final float y;
    public int reserves;

    public int id;

    public PathNode(NodeType nodeType, String nodeName, float nodeX, float nodeY) {
        type = nodeType;
        name = nodeName;
        x = nodeX;
        y = nodeY;
        reserves = 3;

        id = -1;
    }

    @Override
    public String toString() {
        return name;
    }
}
