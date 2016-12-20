package ml.cluster.service;

import ml.cluster.datastructure.segment.PickSegment;

import java.util.Set;

public interface OpticsService {

    void getOrderingPoints(Set<PickSegment> pickSegmentMatrices);
}
