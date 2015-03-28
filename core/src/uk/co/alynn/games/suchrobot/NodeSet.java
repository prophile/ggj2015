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

    private int[] routingTable = null;

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
        Map<RoutingKey, PathNode> nextHops = new HashMap<RoutingKey, PathNode>();
        for (PathNode node : this) {
            runDijkstra(nextHops, node);
        }
        generateRoutingTable(nextHops);
        System.gc();
    }

    private void generateRoutingTable(Map<RoutingKey, PathNode> nextHops) {
        int numNodes = nodes.size();
        for (int i = 0; i < numNodes; ++i) {
            nodes.get(i).id = i;
        }
        routingTable = new int[numNodes * numNodes];
        for (int src = 0; src < numNodes; ++src) {
            String sourceName = nodes.get(src).name;
            for (int dst = 0; dst < numNodes; ++dst) {
                String destName = nodes.get(dst).name;
                RoutingKey key = new RoutingKey(sourceName, destName);
                PathNode nxt = nextHops.get(key);
                int index = src*numNodes + dst;
                routingTable[index] = nxt.id;
            }
        }
    }

    private static float getOrDefault(Map<String, Float> map, String key,
            float dfl) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return dfl;
        }
    }

    private void runDijkstra(Map<RoutingKey, PathNode> nextHops, PathNode node) {
        Map<String, Float> distances = new HashMap<String, Float>();
        Map<String, String> predecessors = new HashMap<String, String>();
        Set<String> unvisited = new HashSet<String>();
        for (PathNode allNodes : this) {
            unvisited.add(allNodes.name);
        }
        distances.put(node.name, 0.0f);

        runDijkstraExploration(node, distances, predecessors, unvisited);

        runDijkstraBacktrackConstruction(nextHops, node, predecessors);
    }

    private void runDijkstraExploration(PathNode node,
            Map<String, Float> distances, Map<String, String> predecessors,
            Set<String> unvisited) {
        PathNode currentNode = node;
        while (true) {
            String bestOption = runDijkstraExplorationStep(distances,
                                                           predecessors,
                                                           unvisited,
                                                           currentNode);
            if (bestOption == null)
                break;
            currentNode = lookup(bestOption);
        }
    }

    private void runDijkstraBacktrackConstruction(
            Map<RoutingKey, PathNode> nextHops, PathNode node,
            Map<String, String> predecessors) {
        for (PathNode targetNode : this) {
            RoutingKey key = new RoutingKey(node.name, targetNode.name);
            if (targetNode == node) {
                nextHops.put(key, targetNode);
                continue;
            }
            String last = targetNode.name;
            while (true) {
                String backtrack = predecessors.get(last);
                if (backtrack == null) {
                    // bifurcated network, mark as stay in place
                    System.err.println("WARNING: bifurcated graph");
                    nextHops.put(key, lookup(key.sourceNode));
                    break;
                }
                if (backtrack.equals(node.name)) {
                    nextHops.put(key, lookup(last));
                    break;
                }
                last = backtrack;
            }
        }
    }

    private String runDijkstraExplorationStep(Map<String, Float> distances,
            Map<String, String> predecessors, Set<String> unvisited,
            PathNode currentNode) {
        float currentDistance = getOrDefault(distances, currentNode.name,
                                             Float.POSITIVE_INFINITY);
        for (PathNode neighbour : connectionsFrom(currentNode)) {
            if (!(unvisited.contains(neighbour.name))) {
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
        return bestOption;
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
        int index = from.id*nodes.size() + to.id;
        return nodes.get(routingTable[index]);
    }

    @Override
    public Iterator<PathNode> iterator() {
        return nodes.iterator();
    }
}
