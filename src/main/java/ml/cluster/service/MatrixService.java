package ml.cluster.service;

import ml.cluster.datastructure.PickSegment;
import ml.cluster.to.PickLocationViewDO;

import java.util.List;
import java.util.Map;

public interface MatrixService {
    Map<PickSegment, List<PickLocationViewDO>> getSegmentedLocations(List<PickLocationViewDO> pickLocationViewDOs);
}
