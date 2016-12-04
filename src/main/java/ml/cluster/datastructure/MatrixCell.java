package ml.cluster.datastructure;

import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public final class MatrixCell {

    private final double maxX;
    private final double minX;
    private final double maxY;
    private final double minY;
    private final List<PickLocationViewDO> locations;
    private final List<Pair<Long, Long>> neighboringCells;

    public MatrixCell(final double maxX, final double minX, final double maxY, final double minY) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        this.locations = new ArrayList<PickLocationViewDO>();
        this.neighboringCells = new ArrayList<>();
    }

    public List<PickLocationViewDO> getLocations() {
        return locations;
    }

    public List<Pair<Long, Long>> getNeighboringCells() {
        return neighboringCells;
    }

    public void addToPickLocations(final PickLocationViewDO pickLocationViewDO) {
        this.locations.add(pickLocationViewDO);
    }

    public void addToNeighborPickingLocations(final Pair<Long, Long> neighbor) {
        this.neighboringCells.add(neighbor);
    }

    public void addToNeighborPickingLocations(final List<Pair<Long, Long>> neighbors) {
        this.neighboringCells.addAll(neighbors);
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

    @Override
    public String toString() {
        return "MatrixCell {" +
                ", locations=" + locations +
                ", neighboringCells=" + neighboringCells +
                ", maxX=" + maxX +
                ", minX=" + minX +
                ", maxY=" + maxY +
                ", minY=" + minY +
                '}';
    }
}
