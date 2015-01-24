package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.math.MathUtils;

public final class Robot {
    public PathNode sourceNode;
    public PathNode destNode;
    public float progress;
    
    public float x() {
        return MathUtils.lerp(sourceNode.x, destNode.x, progress);
    }
    
    public float y() {
        return MathUtils.lerp(sourceNode.y, destNode.y, progress);
    }
    
    public void update(float dt) {
        progress += dt;
        if (progress >= 1.0f) {
            progress -= 1.0f;
            sourceNode = destNode;
            //destNode = next target;
        }
    }
}
