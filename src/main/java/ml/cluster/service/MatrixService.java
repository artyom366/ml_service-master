package ml.cluster.service;

import java.util.List;
import java.util.Set;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import ml.cluster.error.OpticsException;

public interface MatrixService {

	Set<Segment> getSegmentedLocationPoints(List<Point> pickLocationViewDOs) throws OpticsException;
}
