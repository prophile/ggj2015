package uk.co.alynn.games.suchrobot;

public final class PathNode {
    public final String name;
    public final float x;
    public final float y;
    
    public PathNode(String nodeName, float nodeX, float nodeY) {
        name = nodeName;
        x = nodeX;
        y = nodeY;
    }
    
    public String toString() {
        return "<PathNode " + name + ">";
    }
}
