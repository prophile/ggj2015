package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.math.MathUtils;

public final class Robot {
    public final NodeSet nodeSet;
    public PathNode sourceNode;
    public PathNode destNode;
    public PathNode finalTarget;
    public float progress = 0.0f;
    
    public Robot(NodeSet nodes) {
        nodeSet = nodes;
        PathNode home = nodes.lookup("home");
        sourceNode = home;
        destNode = home;
        finalTarget = home;
    }
    
    public float x() {
        return MathUtils.lerp(sourceNode.x, destNode.x, progress);
    }
    
    public float y() {
        return MathUtils.lerp(sourceNode.y, destNode.y, progress);
    }
    
    public void selectTarget(PathNode target) {
        finalTarget = target;
        PathNode selectedNextHop = nodeSet.nextNodeFor(sourceNode, finalTarget);
        if (selectedNextHop != destNode) {
            PathNode tmp = destNode;
            destNode = sourceNode;
            sourceNode = tmp;
            progress = 1.0f - progress;
        }
    }
    
    public void update(float dt) {
        progress += dt * 0.1f;
        if (progress >= 1.0f) {
            progress -= 1.0f;
            sourceNode = destNode;
            destNode = nodeSet.nextNodeFor(sourceNode, finalTarget);
        }
    }
}
