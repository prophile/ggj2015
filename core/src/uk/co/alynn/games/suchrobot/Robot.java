package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.math.MathUtils;

public final class Robot {
    private static final float SPEED = 60.0f;
    private static final float AT_THRESHOLD = 0.05f;
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
        if (target == finalTarget)
            return;
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
        if (sourceNode == destNode) {
            if (destNode != finalTarget) {
                destNode = nodeSet.nextNodeFor(sourceNode, finalTarget);
                progress = 0.0f;
            } else {
                return;
            }
        }
        float distance = (float)Math.hypot(sourceNode.x - destNode.x, sourceNode.y - destNode.y);
        float increment = SPEED*dt / distance;
        progress += increment;
        if (progress >= 1.0f) {
            progress -= 1.0f;
            sourceNode = destNode;
            destNode = nodeSet.nextNodeFor(sourceNode, finalTarget);
        }
    }
    
    public PathNode at() {
        if (sourceNode == destNode)
            return sourceNode;
        if (progress > (1.0f - AT_THRESHOLD))
            return destNode;
        if (progress < AT_THRESHOLD)
            return sourceNode;
        return null;
    }
}
