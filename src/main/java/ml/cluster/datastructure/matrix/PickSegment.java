package ml.cluster.datastructure.matrix;

import ml.cluster.to.PickLocationViewDO;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

public final class PickSegment {

    private final double maxX;
    private final double minX;
    private final double maxY;
    private final double minY;
    private final String line;
    private final List<PickLocationViewDO> locations;
    private FixedRadiusMatrix matrix;

    public PickSegment(final String line, final double minY, final double maxY, final double minX, final double maxX) {
        Validate.notEmpty(line, "Pick segment line is not defined");
        this.line = line;
        this.minY = minY;
        this.maxY = maxY;
        this.minX = minX;
        this.maxX = maxX;
        this.locations = new ArrayList<PickLocationViewDO>();
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

    public List<PickLocationViewDO> getLocations() {
        return locations;
    }

    public void addToPickLocations(final PickLocationViewDO location) {
        Validate.notNull(location, "Pick location is not defined");
        this.locations.add(location);
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
               ", locations=" + locations +
               ", matrix=" + matrix +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PickSegment that = (PickSegment) o;

        if (Double.compare(that.maxX, maxX) != 0) return false;
        if (Double.compare(that.minX, minX) != 0) return false;
        if (Double.compare(that.maxY, maxY) != 0) return false;
        if (Double.compare(that.minY, minY) != 0) return false;
        if (!line.equals(that.line)) return false;
        if (!locations.equals(that.locations)) return false;
        return matrix != null ? matrix.equals(that.matrix) : that.matrix == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(maxX);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + line.hashCode();
        result = 31 * result + locations.hashCode();
        result = 31 * result + (matrix != null ? matrix.hashCode() : 0);
        return result;
    }
}
