package ml.cluster.service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Point;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service("opticsExtractorService")
public class OpticsExtractorServiceImpl implements OpticsExtractorService {

    @Override
    public List<Cluster> extractClusters(final List<Point> optics) {
        Validate.notEmpty(optics, "Ordering points are not defined");

        final List<Cluster> clusters = new LinkedList<>();

        final Iterator<Point> iterator = optics.iterator();
        while (iterator.hasNext()) {
            final Point point = iterator.next();

            if (isCorePoint(point)) {
                final Cluster cluster = extractCluster(iterator);
                clusters.add(cluster);
            }
        }

        return clusters;
    }

    private Cluster extractCluster(final Iterator<Point> iterator) {
        final Cluster cluster = new Cluster();

        while (iterator.hasNext()) {
            final Point point = iterator.next();

            if (isCorePoint(point)) {
                cluster.addToExtractedCluster(point);

            } else if (isClusterPoint(point)) {
                cluster.addToExtractedCluster(point);

            } else if (isBorderPoint(point)) {
                cluster.addToExtractedCluster(point);

            } else if (isOutlierPoint(point)) {
                break;
            }
        }

        return cluster;
    }

    //todo to separate class
    private boolean isOutlierPoint(final Point point) {
        return point.getReachabilityDistance() == Double.POSITIVE_INFINITY && point.getCoreDistance() == Double.POSITIVE_INFINITY;
    }

    private boolean isCorePoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() < Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() == Double.POSITIVE_INFINITY;
    }

    private boolean isClusterPoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() < Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() < Double.POSITIVE_INFINITY;
    }

    private boolean isBorderPoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() == Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() < Double.POSITIVE_INFINITY;
    }
}
