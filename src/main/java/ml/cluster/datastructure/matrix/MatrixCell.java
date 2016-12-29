package ml.cluster.datastructure.matrix;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MatrixCell {

    private final long maxX;
    private final long minX;
    private final long maxY;
    private final long minY;
    private final List<Point> points;
    private final Set<Pair<Long, Long>> neighboringCells;

    public MatrixCell(final long maxX, final long minX, final long maxY, final long minY) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        this.points = new ArrayList<Point>();
        this.neighboringCells = new HashSet<>();
    }

    public List<Point> getLocations() {
        return points;
    }

    public Set<Pair<Long, Long>> getNeighboringCells() {
        return neighboringCells;
    }

    public void addToLocationPoints(final Point point) {
        this.points.add(point);
    }

    public void addToNeighboringCells(final Pair<Long, Long> neighbor) {
        this.neighboringCells.add(neighbor);
    }

    public void addToNeighboringCells(final Set<Pair<Long, Long>> neighbors) {
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
                ", points=" + points +
                ", neighboringCells=" + neighboringCells +
                ", maxX=" + maxX +
                ", minX=" + minX +
                ", maxY=" + maxY +
                ", minY=" + minY +
                '}';
    }
}
