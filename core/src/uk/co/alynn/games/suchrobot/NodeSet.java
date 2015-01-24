package uk.co.alynn.games.suchrobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NodeSet implements Iterable<PathNode> {
    private List<PathNode> nodes = new ArrayList<PathNode>();
    private Map<String, List<PathNode>> directConnections = new HashMap<String, List<PathNode>>();
    
    public NodeSet() {
        
    }
    
    public void addNode(String type, String name, Rational x, Rational y) {
        PathNode newNode = new PathNode(name, x, y);
        nodes.add(newNode);
        directConnections.put(name, new ArrayList<PathNode>());
    }
    
    public void addConnection(String from, String to) {
        directConnections.get(from).add(lookup(to));
        directConnections.get(to).add(lookup(from));
    }
    
    public void compile() {
        System.err.println("compiled node set");
        for (PathNode node : this) {
            System.err.println("- " + node);
        }
    }
    
    public PathNode lookup(String name) {
        for (PathNode node : this) {
            if (node.name.equals(name))
                return node;
        }
        throw new RuntimeException("Missing path node " + name);
    }
    
    public Iterable<PathNode> connectionsFrom(PathNode node) {
        return directConnections.get(node.name);
    }
    
    public PathNode nextNodeFor(PathNode from, PathNode to) {
        return to; // TEMP: do some pathing
    }

    @Override
    public Iterator<PathNode> iterator() {
        return nodes.iterator();
    }
}
