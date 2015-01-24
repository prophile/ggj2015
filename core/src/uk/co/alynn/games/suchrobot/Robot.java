package uk.co.alynn.games.suchrobot;

public final class Robot {
    public PathNode sourceNode;
    public PathNode destNode;
    public Rational progress;
    
    public Rational x() {
        return progress.mul(destNode.x).add(Rational.ONE.sub(progress).mul(sourceNode.x));
    }
    
    public Rational y() {
        return progress.mul(destNode.y).add(Rational.ONE.sub(progress).mul(sourceNode.y));
    }
    
    public void update(Rational dt) {
        progress = progress.add(dt);
        if (progress.compareTo(Rational.ONE) >= 0) {
            progress = Rational.ONE;
            sourceNode = destNode;
            // destNode = next target
        }
    }
}
