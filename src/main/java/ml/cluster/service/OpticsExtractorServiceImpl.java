package ml.cluster.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;

@Service("opticsExtractorService")
public class OpticsExtractorServiceImpl implements OpticsExtractorService {

    @Autowired
    private PointService pointService;

    @Override
    public List<Cluster> extractClusters(final List<OpticsPoint> optics) {
        Validate.notEmpty(optics, "Ordering points are not defined");

        final List<Cluster> clusters = new LinkedList<>();
        final ListIterator<OpticsPoint> iterator = optics.listIterator();

        while (iterator.hasNext()) {
            final OpticsPoint point = iterator.next();
            startClusterFormation(point, iterator, clusters);
        }

        return clusters;
    }

    private void startClusterFormation(final OpticsPoint point, final Iterator<OpticsPoint> iterator, final List<Cluster> clusters) {
        if (pointService.isCorePoint(point)) {
            final Cluster cluster = extractCluster(iterator, point, clusters);
            clusters.add(cluster);
        }
    }

    private Cluster extractCluster(final Iterator<OpticsPoint> iterator, final OpticsPoint corePoint, final List<Cluster> clusters) {
        final Cluster cluster = new Cluster();
        cluster.addToExtractedCluster(corePoint);

        while (iterator.hasNext()) {
            final OpticsPoint point = iterator.next();

            if (pointService.isCorePoint(point)) {
                startClusterFormation(point, iterator, clusters);

            } else if (pointService.isClusterPoint(point)) {
                cluster.addToExtractedCluster(point);

            } else if (pointService.isBorderPoint(point)) {
                cluster.addToExtractedCluster(point);

            } else if (pointService.isOutlierPoint(point)) {
                break;
            }
        }

        return cluster;
    }
}
