package uk.co.alynn.games.suchrobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            RoutingKey o = (RoutingKey) obj;
            return o.sourceNode.equals(sourceNode)
                    && o.destNode.equals(destNode);
        }

        @Override
        public int hashCode() {
            return (903 * sourceNode.hashCode()) + destNode.hashCode();
        }
    }

    private List<PathNode> nodes = new ArrayList<PathNode>();
    private Map<String, List<PathNode>> directConnections = new HashMap<String, List<PathNode>>();
    private Map<RoutingKey, PathNode> nextHops = null;

    public NodeSet() {

    }

    public void addNode(String type, String name, float x, float y, int reserves) {
        PathNode newNode = new PathNode(NodeType.valueOf(type), name, x, y);
        newNode.reserves = reserves;
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
        System.err.println("-- RT DATABASE --");
        System.err.println("src\tdst\tnext");
        for (Map.Entry<RoutingKey, PathNode> entry : nextHops.entrySet()) {
            System.err.println(entry.getKey().sourceNode + "\t"
                    + entry.getKey().destNode + "\t" + entry.getValue().name);
        }
        System.err.println("--");
    }

    private static float getOrDefault(Map<String, Float> map, String key,
            float dfl) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return dfl;
        }
    }

    private void runDijkstra(PathNode node) {
        Map<String, Float> distances = new HashMap<String, Float>();
        Map<String, String> predecessors = new HashMap<String, String>();
        Set<String> unvisited = new HashSet<String>();
        for (PathNode allNodes : this) {
            unvisited.add(allNodes.name);
        }
        distances.put(node.name, 0.0f);
        PathNode currentNode = node;
        while (true) {
            System.err.println("\texploring node " + currentNode.name);
            float currentDistance = getOrDefault(distances, currentNode.name,
                    Float.POSITIVE_INFINITY);
            for (PathNode neighbour : connectionsFrom(currentNode)) {
                if (!(unvisited.contains(neighbour.name))) {
                    System.err.println("\tskipping " + neighbour.name
                            + " since it has been visited");
                    continue;
                }
                float baseDistance = (float) Math.hypot(neighbour.x
                        - currentNode.x, neighbour.y - currentNode.y);
                float tentative = currentDistance + baseDistance;
                float assignedDist = getOrDefault(distances, neighbour.name,
                        Float.POSITIVE_INFINITY);
                if (tentative < assignedDist) {
                    distances.put(neighbour.name, tentative);
                    predecessors.put(neighbour.name, currentNode.name);
                    System.err.println("\tnew best path for " + neighbour.name
                            + " discovered: via " + currentNode.name);
                } else {
                    System.err.println("\tnew path to " + neighbour.name
                            + " discovered but suboptimal: " + tentative
                            + " rather than " + assignedDist);
                }
            }
            unvisited.remove(currentNode.name);
            String bestOption = null;
            float bestDistance = Float.POSITIVE_INFINITY;
            for (String option : unvisited) {
                float calcDist = getOrDefault(distances, option,
                        Float.POSITIVE_INFINITY);
                if (calcDist <= bestDistance) {
                    bestOption = option;
                    bestDistance = calcDist;
                }
            }
            if (bestOption == null)
                break;
            currentNode = lookup(bestOption);
        }

        for (Map.Entry<String, String> pred : predecessors.entrySet()) {
            System.err.println("PRED TABLE " + pred.getValue() + " -> "
                    + pred.getKey());
        }

        for (PathNode targetNode : this) {
            RoutingKey key = new RoutingKey(node.name, targetNode.name);
            if (targetNode == node) {
                nextHops.put(key, targetNode);
                continue;
            }
            System.err.println("\tbacktracking from " + targetNode.name);
            String last = targetNode.name;
            while (true) {
                String backtrack = predecessors.get(last);
                if (backtrack.equals(node.name)) {
                    nextHops.put(key, lookup(last));
                    break;
                }
                last = backtrack;
            }
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
