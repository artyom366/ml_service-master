package ml.cluster.datastructure.optics.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Point;

public final class ClusterImpl implements Cluster {

    private double maxX;
    private double minX;
    private double maxY;
    private double minY;
    private double densityIndex;
    private String line;
    private final List<Point> locationPoints;

    public ClusterImpl() {
        this.locationPoints = new LinkedList<>();
    }

    public void addToCluster(final Point point) {
        Validate.notNull(point, "PointImpl is not defined");
        this.locationPoints.add(point);
    }

    @Override
    public List<Point> getClusterPoints() {
        return Collections.unmodifiableList(locationPoints);
    }
}
