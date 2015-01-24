package uk.co.alynn.games.suchrobot;

public final class PathNode {
    public final NodeType type;
    public final String name;
    public final float x;
    public final float y;
    public int reserves;

    public PathNode(NodeType nodeType, String nodeName, float nodeX, float nodeY) {
        type = nodeType;
        name = nodeName;
        x = nodeX;
        y = nodeY;
        reserves = 3;
    }

    @Override
    public String toString() {
        return "<PathNode " + name + ">";
    }
}
