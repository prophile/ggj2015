package uk.co.alynn.games.suchrobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NodeSet implements Iterable<PathNode> {
    private static final class RoutingKey {
        public final String sourceNode;
        public final String destNode;
        
        public RoutingKey(String src, String dst) {
            sourceNode = src;
            destNode = dst;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof RoutingKey))
                return false;
            RoutingKey o = (RoutingKey)obj;
            return o.sourceNode.equals(sourceNode) && o.destNode.equals(destNode);
        }
        
        @Override
        public int hashCode() {
            return (903*sourceNode.hashCode()) + destNode.hashCode();
        }
    }
    
    private List<PathNode> nodes = new ArrayList<PathNode>();
    private Map<String, List<PathNode>> directConnections = new HashMap<String, List<PathNode>>();
    private Map<RoutingKey, PathNode> nextHops = null;
    
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
        System.err.println("computing routing tables");
        nextHops = new HashMap<RoutingKey, PathNode>();
        for (PathNode node : this) {
            System.err.println("- " + node);
            runDijkstra(node);
        }
    }
    
    private void runDijkstra(PathNode node) {
        // Not Dijkstra's algorithm!
        for (PathNode targetNode : this) {
            RoutingKey key = new RoutingKey(node.name, targetNode.name);
            nextHops.put(key, targetNode);
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
        RoutingKey key = new RoutingKey(from.name, to.name);
        return nextHops.get(key);
    }

    @Override
    public Iterator<PathNode> iterator() {
        return nodes.iterator();
    }
}
