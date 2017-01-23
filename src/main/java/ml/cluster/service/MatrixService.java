package ml.cluster.service;

import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import ml.cluster.error.matrix.MatrixException;

import java.util.List;
import java.util.Set;

public interface MatrixService {
    Set<Segment> getSegmentedLocationPoints(List<OpticsPoint> pickLocationViewDOs) throws MatrixException;
}
