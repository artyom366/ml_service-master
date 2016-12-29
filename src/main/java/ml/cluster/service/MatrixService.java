package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import ml.cluster.error.MatrixException;

import java.util.List;
import java.util.Set;

public interface MatrixService {
    Set<Segment> getSegmentedLocationPoints(List<Point> pickLocationViewDOs) throws MatrixException;
}
