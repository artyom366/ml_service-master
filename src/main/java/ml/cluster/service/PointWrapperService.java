package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.location.LocationException;
import ml.cluster.to.PickLocationViewDO;

import java.util.List;

public interface PointWrapperService {
    List<Point> getPoints(List<PickLocationViewDO> locations) throws LocationException;
}
