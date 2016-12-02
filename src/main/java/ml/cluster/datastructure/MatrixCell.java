package ml.cluster.datastructure;

import ml.cluster.to.PickLocationViewDO;

import java.util.ArrayList;
import java.util.List;

public final class MatrixCell {

    private final long rowNumber;
    private final long columnNumber;
    private final List<PickLocationViewDO> pickLocationViewDOs;
    private final List<Integer> neighborPickingLocations;

    private double maxX;
    private double minX;
    private double maxY;
    private double minY;

    public MatrixCell(final long rowNumber, final long columnNumber) {
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.pickLocationViewDOs = new ArrayList<PickLocationViewDO>();
        this.neighborPickingLocations = new ArrayList<Integer>();
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public long getColumnNumber() {
        return columnNumber;
    }

    public List<PickLocationViewDO> getPickLocationViewDOs() {
        return pickLocationViewDOs;
    }

    public List<Integer> getNeighborPickingLocations() {
        return neighborPickingLocations;
    }

    public void addToPickLocations(final PickLocationViewDO pickLocationViewDO) {
        this.pickLocationViewDOs.add(pickLocationViewDO);
    }

    public void addToNeighborPickingLocations(final Integer cellNumber) {
        this.neighborPickingLocations.add(cellNumber);
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    @Override
    public String toString() {
        return "MatrixCell {" +
               "rowNumber=" + rowNumber +
               ", columnNumber=" + columnNumber +
               ", pickLocationViewDOs=" + pickLocationViewDOs +
               ", neighborPickingLocations=" + neighborPickingLocations +
               ", maxX=" + maxX +
               ", minX=" + minX +
               ", maxY=" + maxY +
               ", minY=" + minY +
               '}';
    }
}
