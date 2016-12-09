package ml.cluster.service;

import ml.cluster.datastructure.matrix.PickSegment;
import ml.cluster.to.PickLocationViewDO;

import java.util.List;
import java.util.Map;

public interface OpticsService {

    void getOrderingPoints(Map<PickSegment, List<PickLocationViewDO>> pickSegments);
}
