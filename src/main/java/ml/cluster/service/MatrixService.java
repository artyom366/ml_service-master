package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.PickSegment;
import ml.cluster.error.MatrixException;
import ml.cluster.to.PickLocationViewDO;

import java.util.List;
import java.util.Set;

public interface MatrixService {
    Set<PickSegment> getSegmentedLocations(List<Point> pickLocationViewDOs) throws MatrixException;
}
