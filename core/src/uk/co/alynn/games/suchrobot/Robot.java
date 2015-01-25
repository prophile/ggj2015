package uk.co.alynn.games.suchrobot;

import com.badlogic.gdx.math.MathUtils;

public final class Robot {
    private static final float AT_THRESHOLD = 0.05f;
    public final NodeSet nodeSet;
    public PathNode sourceNode;
    public PathNode destNode;
    public PathNode finalTarget;
    public PathNode spawnNode;
    public float progress = 0.0f;
    public CargoType carrying = CargoType.NOTHING;
    public float accumulatedTimeAt = 0.0f;
    public boolean flipped;
    public final RobotClass cls;

    public Robot(RobotClass cls, NodeSet nodes, PathNode home) {
        nodeSet = nodes;
        sourceNode = home;
        destNode = home;
        finalTarget = home;
        spawnNode = home;
        flipped = false;
        this.cls = cls;
    }

    public boolean available() {
        return carrying == CargoType.NOTHING;
    }

    public void pickUp(CargoType cargo) {
        if (carrying != CargoType.NOTHING) {
            throw new RuntimeException("Double-stacked");
        }
        carrying = cargo;
        headHome();
    }

    public boolean offload(CargoType cargo) {
        if (carrying == cargo) {
            carrying = CargoType.NOTHING;
            return true;
        }
        return false;
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
            if (destNode.x > sourceNode.x) {
                flipped = false;
            } else if (destNode.x < sourceNode.x) {
                flipped = true;
            }
        }
    }

    public void headHome() {
        selectTarget(spawnNode);
    }

    public void update(float dt) {
        if (at() == null) {
            accumulatedTimeAt = 0;
        } else {
            accumulatedTimeAt += dt;
        }
        if (sourceNode == destNode) {
            if (destNode != finalTarget) {
                destNode = nodeSet.nextNodeFor(sourceNode, finalTarget);
                progress = 0.0f;
                if (destNode.x > sourceNode.x) {
                    flipped = false;
                } else if (destNode.x < sourceNode.x) {
                    flipped = true;
                }
            } else {
                return;
            }
        }
        float distance = (float) Math.hypot(sourceNode.x - destNode.x,
                sourceNode.y - destNode.y);
        float increment = Constants.SPEED.asFloat() * dt / distance;
        progress += increment;
        if (progress >= 1.0f) {
            progress = 0.0f;
            sourceNode = destNode;
            destNode = nodeSet.nextNodeFor(sourceNode, finalTarget);
            if (destNode.x > sourceNode.x) {
                flipped = false;
            } else if (destNode.x < sourceNode.x) {
                flipped = true;
            }
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
