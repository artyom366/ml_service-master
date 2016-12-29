package ml.cluster.service;

import ml.cluster.datastructure.segment.Segment;

import java.util.Set;

public interface OpticsService {

    void getOrderingPoints(Set<Segment> segmentMatrices);
}
