package ml.cluster.datastructure;

import ml.cluster.to.PickLocationViewDO;

import java.util.ArrayList;
import java.util.List;

public final class PickSegment {

    private final double maxX;
    private final double minX;
    private final double maxY;
    private final double minY;
    private final String line;
    private final List<PickLocationViewDO> pickLocationViewDOs;
    private FixedRadiusMatrix matrix;

    public PickSegment(final String line, final double minY, final double maxY, final double minX, final double maxX) {
        this.line = line;
        this.minY = minY;
        this.maxY = maxY;
        this.minX = minX;
        this.maxX = maxX;
        this.pickLocationViewDOs = new ArrayList<PickLocationViewDO>();
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinY() {
        return minY;
    }

    public String getLine() {
        return line;
    }

    public List<PickLocationViewDO> getPickLocationViewDOs() {
        return pickLocationViewDOs;
    }

    public void addToPickLocations(final PickLocationViewDO pickLocationViewDO) {
        this.pickLocationViewDOs.add(pickLocationViewDO);
    }

    public FixedRadiusMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(FixedRadiusMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public String toString() {
        return "PickSegment {" +
               "maxX=" + maxX +
               ", minX=" + minX +
               ", maxY=" + maxY +
               ", minY=" + minY +
               ", line='" + line + '\'' +
               ", pickLocationViewDOs=" + pickLocationViewDOs +
               ", matrix=" + matrix +
               '}';
    }
}
