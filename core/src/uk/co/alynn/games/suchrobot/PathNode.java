package uk.co.alynn.games.suchrobot;

public final class PathNode {
    public final String name;
    public final Rational x;
    public final Rational y;
    
    public PathNode(String nodeName, Rational nodeX, Rational nodeY) {
        name = nodeName;
        x = nodeX;
        y = nodeY;
    }
    
    public String toString() {
        return "<PathNode " + name + ">";
    }
}
