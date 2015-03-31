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
    public float peril;
    public float perilDelta;
    private boolean quicksandImmune;

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

    public Sprite rankSprite() {
        return cls.pips;
    }

    public void update(float dt) {
        if (at() == null) {
            accumulatedTimeAt = 0;
        } else {
            accumulatedTimeAt += dt;
        }
        peril += perilDelta * dt;
        if (peril < 0.0f) {
            peril = 0.0f;
            perilDelta = 0.0f;
        }
        if (peril > 1.0f) {
            perilDelta = 0.0f;
            peril = 1.0f;
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
        float increment = Constants.speed() * dt / distance;
        if (cls == RobotClass.PAUL) {
            increment *= Constants.l2SpeedFactor();
        } else if (cls == RobotClass.JOHN) {
            increment *= Constants.l3SpeedFactor();
        }
        if (peril > 0.0f) {
            increment = 0.0f;
        }
        progress += increment;
        if (progress >= 1.0f && peril == 0.0f) {
            if (destNode.type == NodeType.QUICKSAND
                    && !quicksandImmune
                    && MathUtils.randomBoolean(Constants.quicksandChance())) {
                peril = 0.0001f;
                perilDelta = 1.0f;
                quicksandImmune = true;
            } else {
                progress = 0.0f;
                sourceNode = destNode;
                destNode = nodeSet.nextNodeFor(sourceNode, finalTarget);
                if (destNode.x > sourceNode.x) {
                    flipped = false;
                } else if (destNode.x < sourceNode.x) {
                    flipped = true;
                }
                quicksandImmune = false;
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

    public boolean gatheredFor(float requiredTime) {
        if (cls == RobotClass.PAUL) {
            requiredTime /= Constants.l2GatherFactor();
        } else if (cls == RobotClass.JOHN) {
            requiredTime /= Constants.l3GatherFactor();
        }
        return accumulatedTimeAt > requiredTime;
    }
}
