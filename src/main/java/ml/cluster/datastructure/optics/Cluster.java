package ml.cluster.datastructure.optics;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

public final class Cluster {

    private double maxX;
    private double minX;
    private double maxY;
    private double minY;
    private double densityIndex;
    private String line;
    private final List<OpticsPoint> locationPoints;

    public Cluster() {
        this.locationPoints = new LinkedList<>();
    }

    public void addToExtractedCluster(final OpticsPoint point) {
        Validate.notNull(point, "Point is not defined");
        this.locationPoints.add(point);
    }

    public List<OpticsPoint> getClusterPoints() {
        return locationPoints;
    }
}
