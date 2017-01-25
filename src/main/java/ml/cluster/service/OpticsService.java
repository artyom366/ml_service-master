package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;

import java.util.List;
import java.util.Set;

public interface OpticsService {

    List<Point> getOrderingPoints(Set<Segment> segmentMatrices);
}
