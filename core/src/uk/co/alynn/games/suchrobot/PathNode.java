package uk.co.alynn.games.suchrobot;

public final class PathNode {
    public final NodeType type;
    public final String name;
    public final float x;
    public final float y;
    
    public PathNode(NodeType nodeType, String nodeName, float nodeX, float nodeY) {
        type = nodeType;
        name = nodeName;
        x = nodeX;
        y = nodeY;
    }
    
    public String toString() {
        return "<PathNode " + name + ">";
    }
}
