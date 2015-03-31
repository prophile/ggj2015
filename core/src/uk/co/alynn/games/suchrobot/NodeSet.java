package uk.co.alynn.games.suchrobot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

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

    private int routingCacheKey = 0x11384767;

    private int[] routingTable = null;

    public NodeSet() {

    }

    private int routingTableMapLength() {
        int nNodes = nodes.size();
        return nNodes*nNodes + 2;
    }

    private int[] serializeRoutingTable() {
        // header: routing cache key, node count
        int nodeCount = nodes.size();
        int[] data = new int[routingTableMapLength()];
        data[0] = routingCacheKey;
        data[1] = nodeCount;
        System.arraycopy(routingTable, 0, data, 2, nodeCount*nodeCount);
        System.err.println("RCK = " + routingCacheKey);
        return data;
    }

    private void saveRoutingTable() {
        DataOutputStream encoder = null;
        try {
            FileHandle handle = Gdx.files.local(".nodertcache");
            encoder = new DataOutputStream(handle.write(false,
                                                        1024*64));
            for (int x : serializeRoutingTable()) {
                encoder.writeInt(x);
            }
            encoder.close();
        } catch (IOException e) {
            System.err.println("Could not write node route cache.");
        } finally {
            if (encoder != null) {
                try {
                    encoder.close();
                } catch (IOException e) {
                    System.err.println("Who uses this language?");
                }
            }
        }
    }

    private boolean unserializeRoutingTable(int[] data) {
        int nodeCount = nodes.size();
        if (data.length != routingTableMapLength()) {
            System.err.println("Saved routing table has wrong length.");
            return false;
        }
        if (data[0] != routingCacheKey) {
            System.err.println("Saved routing table has wrong cache key.");
            return false;
        }
        if (data[1] != nodeCount) {
            System.err.println("Node size sanity check from routing table failed.");
            return false;
        }
        routingTable = new int[nodeCount*nodeCount];
        System.arraycopy(data, 2, routingTable, 0, nodeCount*nodeCount);
        return true;
    }

    private boolean loadRoutingTable() {
        DataInputStream decoder = null;
        try {
            FileHandle handle = Gdx.files.local(".nodertcache");
            decoder = new DataInputStream(handle.read(1024*64));
            int decodedCacheKey = decoder.readInt();
            if (decodedCacheKey != routingCacheKey) {
                System.err.println("WARNING: DCK != RCK (" + decodedCacheKey + ")");
            }
            int decodedNodeCount = decoder.readInt();
            int expectedLength = 2 + decodedNodeCount*decodedNodeCount;
            System.err.println("Expecting " + expectedLength + " entries (" + expectedLength*4 + " bytes raw)");
            int[] data = new int[expectedLength];
            data[0] = decodedCacheKey;
            data[1] = decodedNodeCount;
            for (int i = 2; i < expectedLength; ++i) {
                data[i] = decoder.readInt();
            }
            return unserializeRoutingTable(data);
        } catch (IOException e) {
            System.err.println("Could not read node route cache.");
            System.err.println(e);
            return false;
        } catch (GdxRuntimeException e) {
            System.err.println("Could not read node route cache.");
            System.err.println(e);
            return false;
        } finally {
            if (decoder != null) {
                try {
                    decoder.close();
                } catch (IOException e) {
                    System.err.println("Can't close the decoder input stream. Really?");
                    return false;
                }
            }
        }
    }

    public void addNode(String type, String name, float x, float y, int reserves) {
        PathNode newNode = new PathNode(NodeType.valueOf(type), name, x, y);
        newNode.reserves = reserves;
        nodes.add(newNode);
        directConnections.put(name, new ArrayList<PathNode>());

        routingCacheKey = 37*routingCacheKey + name.hashCode();
    }

    public void addConnection(String from, String to) {
        directConnections.get(from).add(lookup(to));
        directConnections.get(to).add(lookup(from));

        if (from.compareTo(to) < 0) {
            routingCacheKey = 37*routingCacheKey + from.hashCode();
            routingCacheKey = 37*routingCacheKey + to.hashCode();
        } else {
            routingCacheKey = 37*routingCacheKey + to.hashCode();
            routingCacheKey = 37*routingCacheKey + from.hashCode();
        }
    }

    public void compile() {
        assignNodeIDs();
        if (loadRoutingTable()) {
            System.err.println("Routing table loaded from cache.");
            return;
        }
        long startTime = System.currentTimeMillis();
        Map<RoutingKey, PathNode> nextHops = new HashMap<RoutingKey, PathNode>();
        for (PathNode node : this) {
            runDijkstra(nextHops, node);
        }
        generateRoutingTable(nextHops);
        System.gc();
        long endTime = System.currentTimeMillis();
        System.err.println("Node map built in " + (endTime - startTime) + "ms");
        saveRoutingTable();
    }

    private void assignNodeIDs() {
        int numNodes = nodes.size();
        for (int i = 0; i < numNodes; ++i) {
            nodes.get(i).id = i;
        }
    }

    private void generateRoutingTable(Map<RoutingKey, PathNode> nextHops) {
        int numNodes = nodes.size();
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
