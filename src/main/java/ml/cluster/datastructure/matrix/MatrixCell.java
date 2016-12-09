package ml.cluster.datastructure.matrix;

import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public final class MatrixCell {

    private final long maxX;
    private final long minX;
    private final long maxY;
    private final long minY;
    private final List<PickLocationViewDO> locations;
    private final List<Pair<Long, Long>> neighboringCells;

    public MatrixCell(final long maxX, final long minX, final long maxY, final long minY) {
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

    public long getMaxX() {
        return maxX;
    }

    public long getMinX() {
        return minX;
    }

    public long getMaxY() {
        return maxY;
    }

    public long getMinY() {
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
