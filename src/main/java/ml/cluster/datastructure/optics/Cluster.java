package ml.cluster.datastructure.optics;

import org.apache.commons.lang3.Validate;

import java.util.LinkedList;
import java.util.List;

public final class Cluster {

    private double maxX;
    private double minX;
    private double maxY;
    private double minY;
    private double densityIndex;
    private String line;
    private final List<Point> locationPoints;

    public Cluster() {
        this.locationPoints = new LinkedList<>();
    }

    public void addToExtractedCluster(final Point point) {
        Validate.notNull(point, "Point is not defined");
        this.locationPoints.add(point);
    }

    public List<Point> getClusterPoints() {
        return locationPoints;
    }
}
